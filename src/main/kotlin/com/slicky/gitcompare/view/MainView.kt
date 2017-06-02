package com.slicky.gitcompare.view

import com.slicky.gitcompare.app.Styles
import com.slicky.gitcompare.ctrl.Github
import com.slicky.gitcompare.model.*
import javafx.application.Platform
import javafx.scene.chart.AreaChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.control.DatePicker
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.BorderPane
import tornadofx.*
import java.time.LocalDate

class MainView : View("GitCompare") {
    private val github: Github by inject()

    lateinit var mainPane: BorderPane
    lateinit var indicator: ProgressIndicator

    lateinit var graph: AreaChart<String, Number>

    var isLoading: Boolean = false
        set(value) {
            field = value
            Platform.runLater {
                mainPane.isDisable = value
                indicator.isVisible = value
            }
        }

    private lateinit var fromField: DatePicker
    private lateinit var toField: DatePicker

    override val root = stackpane {
        mainPane = borderpane {

            center {
                graph = areachart(
                        x = CategoryAxis().apply { label = "Time period" },
                        y = NumberAxis().apply { label = "Repositories created" }
                ) {
                    createSymbols = false
                }
            }

            right = form {
                prefWidth = 280.0

                fieldset("Parameters") {
                    field("Language") {
                        textfield(github.model.language) {
                            textProperty().onChange { github.calculateApprox() }
                        }.required()
                    }
                    field("Size in bytes") {
                        hbox {
                            integerfield(github.model.sizeAmount) {
                                prefWidth = 80.0
                            }.required()
                            spinner(listOf(LessThan, EqualsLessThan, EqualsWith, EqualsMoreThan, MoreThan).observable()) {
                                increment(3) // set to "EqualsMoreThan"
                                github.model.sizeOption.bind(valueProperty())
                            }
                        }
                    }
                    field("Stars") {
                        hbox {
                            integerfield(github.model.starsAmount) {
                                prefWidth = 80.0
                            }.required()
                            spinner(listOf(LessThan, EqualsLessThan, EqualsWith, EqualsMoreThan, MoreThan).observable()) {
                                increment(3) // set to "EqualsMoreThan"
                                github.model.starsOption.bind(valueProperty())
                            }
                        }
                    }
                    field("Forks") {
                        hbox {
                            integerfield(github.model.forksAmount) {
                                prefWidth = 80.0
                            }.required()
                            spinner(listOf(LessThan, EqualsLessThan, EqualsWith, EqualsMoreThan, MoreThan).observable()) {
                                increment(3) // set to "EqualsMoreThan"
                                github.model.forksOption.bind(valueProperty())
                            }
                        }
                    }
                }

                fieldset("Search") {
                    val tomorrow = LocalDate.now().plusDays(1)
                    val twentyYearsAgo = LocalDate.now().minusYears(20)

                    field("From") {
                        fromField = editabledatepicker(github.model.createdFrom) {
                            isActiveBefore { item.isBefore(toField.value) && item.isBefore(tomorrow) }
                            isActiveAfter { item.isAfter(twentyYearsAgo) }
                            valueProperty().onChange { github.calculateApprox() }
                            required()
                        }
                    }
                    field("To") {
                        toField = editabledatepicker(github.model.createdTo) {
                            isActiveBefore { item.isBefore(tomorrow) }
                            isActiveAfter { item.isAfter(fromField.value) && item.isAfter(twentyYearsAgo) }
                            valueProperty().onChange { github.calculateApprox() }
                            required()
                        }
                    }
                    field("Period") {
                        hbox {
                            integerfield(github.model.periodAmount) {
                                prefWidth = 80.0
                                textProperty().onChange { github.calculateApprox() }
                            }.required()
                            spinner(listOf(Days, Weeks, Months, Years).observable()) {
                                increment(2) // set to "Months"
                                github.model.periodUnit.bind(valueProperty())
                                valueProperty().onChange { github.calculateApprox() }
                            }
                        }
                    }
                    field {
                        vbox {
                            addClass(Styles.approxText)
                            label(github.model.approx)
                        }
                    }
                }

                fieldset {
                    hbox {
                        addClass(Styles.buttonField)
                        button("Query") {
                            setOnAction { github.startQuery() }
                        }
                        button("Save") {
                            setOnAction { github.saveSnapshot() }
                        }
                    }
                }
            }
        }

        indicator = progressindicator {
            addClass(Styles.indicator)
            isVisible = false
        }
    }
}