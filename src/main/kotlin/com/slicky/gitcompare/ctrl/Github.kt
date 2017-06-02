package com.slicky.gitcompare.ctrl

import com.slicky.gitcompare.core.PropertyHandler
import com.slicky.gitcompare.core.RequestPerMinuteExecutor
import com.slicky.gitcompare.model.*
import com.slicky.gitcompare.view.MainView
import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.scene.SnapshotParameters
import javafx.scene.chart.XYChart
import javafx.scene.control.Alert
import javafx.stage.FileChooser
import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO

/**
 * Created by SlickyPC on 25.5.2017
 *
 * Github API v3 docs
 * https://developer.github.com/v3/search/
 *
 * Example query
 * https://api.github.com/search/repositories?q=language:Java size:>=100 created:2017-05-23..2017-05-25
 */
class Github : Controller() {

    private val view: MainView by inject()
    private val api: Rest by inject()

    val model = RepositoryRequestModel(RepositoryRequest())

    private val executor = RequestPerMinuteExecutor(model.rpm, "Github Executor Thread", false)

    init {
        api.baseURI = "https://api.github.com"
        try {
            val props = PropertyHandler("github.properties")
            api.setBasicAuth(props["username"], props["password"])
        } catch (e: Exception) {
            println("Could not find github properties, setting rpm to 10.")
            model.rpm.value = 10
        }
    }

    fun startQuery() {
        // Return if model could not commit.
        if (!model.commit())
            return

        // Run on async thread.
        runAsync {
            // Set view state to loading.
            view.isLoading = true
            try {
                // Do work!
                doWork()
            } catch (e: Exception) {
                // Notify user.
                Platform.runLater {
                    alert(Alert.AlertType.ERROR, "Error occurred while querying!", e.localizedMessage)
                }
                // Print it.
                e.printStackTrace()
                // Shut it down.
                executor.shutdownNow()
            } finally {
                // Set view state to normal.
                view.isLoading = false
            }
        }
    }

    private fun doWork() = with(model) {
        // If period amount is less than 1, its pointless to do anything.
        if (periodAmount.value.toInt() < 1)
            return@with

        // Clear graph on UI thread.
        Platform.runLater { view.graph.data.clear() }

        // Process each language in row.
        language.value.split(",")
                .map { it.trim() }
                .filter { !it.isNullOrEmpty() }
                .forEach { processNewLanguage(it) }
    }

    private fun processNewLanguage(language: String) = with(model) {
        // Create new XYChart.Series to add to XYChart.
        val series = createNewSeries(language)
        // Add XYChart.Series to XYChart on main thread.
        Platform.runLater { view.graph.data.add(series) }

        // This variable stores current Date. It starts from createdFrom.
        var currentDate = createdFrom.value

        // Main loop, which loops from createdFrom to createdTo.
        while (currentDate.isBefore(createdTo.value)) {
            // Save Date from previous loop.
            val previousDate = currentDate
            // Add period amount to current Date.
            currentDate = periodUnit.value.addTo(currentDate, periodAmount.value.toLong())

            // Create GithubSpecialQuery to query Github API
            val query = createGithubSpecialQuery(model, language, previousDate, currentDate)
            // Try multiple times to get valid Response.
            val response = getValidResponse(query)
            // Create new XYChart.Data to add to XYChart.Series.
            val data = createNewData(currentDate, response)
            // Add XYChart.Data to XYChart.Series on main thread.
            Platform.runLater { series.data.add(data) }
        }
    }

    private fun createNewData(date: LocalDate, response: RepositoryResponse) =
            XYChart.Data<String, Number>(date.format(DateTimeFormatter.ofPattern("d. M. yyyy")), response.total)

    private fun createNewSeries(language: String) =
            XYChart.Series(language, mutableListOf<XYChart.Data<String, Number>>().observable())

    private fun createGithubSpecialQuery(
            model: RepositoryRequestModel,
            language: String,
            previousDate: LocalDate,
            currentDate: LocalDate
    ) = model.let {
        GithubSpecialQuery(
                Language(language),
                Size(it.sizeOption.value, it.sizeAmount.value.toInt()),
                Stars(it.starsOption.value, it.starsAmount.value.toInt()),
                Forks(it.forksOption.value, it.forksAmount.value.toInt()),
                Created(previousDate, currentDate))
    }

    private fun getValidResponse(query: GithubSpecialQuery): RepositoryResponse {
        var fail = 0
        // Try it 10 times.

        while (fail < 10) {
            // Submit query to executor and wait for response.
            val response = executor.submit {
                getRepositoryResponse(query)
            }.get()

            // If github API did not finish its search, try again.
            if (response.incomplete)
                fail++
            else
                return response
        }
        throw GithubException { "Could not get valid response in 10 tries." }
    }

    private fun getRepositoryResponse(query: GithubSpecialQuery): RepositoryResponse {
        val response = api.get("search/repositories${query.string}")
        val json = response.one()
        if (response.ok().not())
            throw GithubException { "Response was not 200 OK.\n$json" }
        return json.toModel<RepositoryResponse>()
    }

    fun calculateApprox() {
        runAsync {
            with(model) {
                // If these two are not present, we can not do anything.
                periodAmount.value ?: return@with
                periodUnit.value ?: return@with

                // Period amount has to be at least 1.
                if (periodAmount.value.toInt() < 1)
                    return@runAsync

                // Init current date and counter.
                var current = createdFrom.value
                var count = 0

                // Loop from createdFrom to createdTo and count how many iterations it took.
                while (current.isBefore(createdTo.value)) {
                    count++
                    // Add period amount to previous date and set it as current.
                    current = periodUnit.value.addTo(current, periodAmount.value.toLong())
                }

                // Calculate wait time from rpm to rps.
                val waitTimePerRequest = 60 / rpm.value.toDouble()

                // Count how many languages user had entered.
                val languageCount = language.value
                        .split(',')
                        .map { it.trim() }
                        .count { !it.isNullOrEmpty() }

                // Multiply wait time with count of languages
                val waitTime = Math.ceil(count * waitTimePerRequest).toInt() * languageCount

                // Display in appropriate form.
                ui {
                    approx.value = when (waitTime) {
                        in 0..59 -> {
                            "Approximately $waitTime second${if (waitTime != 1) "s" else ""}."
                        }
                        in 60..3599 -> {
                            val waitTimeMinutes = waitTime / 60
                            "Approximately $waitTimeMinutes minute${if (waitTimeMinutes != 1) "s" else ""}."
                        }
                        else -> {
                            val waitTimeHours = waitTime / 3600
                            "Approximately $waitTimeHours hour${if (waitTimeHours != 1) "s" else ""}."
                        }
                    }
                }
            }
        }
    }

    fun saveSnapshot() {
        chooseFile("Save Snapshot",
                arrayOf(FileChooser.ExtensionFilter("PNG Images", "*.png")),
                FileChooserMode.Save,
                primaryStage
        ).firstOrNull()?.let { file ->
            val image = view.graph.snapshot(SnapshotParameters(), null)
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file)
        }
    }
}
