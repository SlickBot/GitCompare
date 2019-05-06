package com.slicky.gitcompare.app

import com.slicky.gitcompare.view.MainView
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*
import kotlin.system.exitProcess

class MyApp: App(MainView::class, Styles::class) {

    init {
        dumpStylesheets()
    }

    override fun start(stage: Stage) {
        super.start(stage.apply {
            icons += Image(resources["/github_icon.png"])
            width = 1000.0
            height = 600.0
            setOnCloseRequest { exitProcess(0) }
        })
    }

}
