package com.slicky.gitcompare.app

import javafx.geometry.Pos
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val alignCenter by cssclass()
        val buttonField by cssclass()
        val indicator by cssclass()
        val approxText by cssclass()
    }

    init {
        alignCenter {
            alignment = Pos.CENTER
        }

        buttonField {
            alignment = Pos.CENTER
            button {
                fontSize = 18.px
                fontWeight = FontWeight.BOLD
            }
        }

        progressIndicator and indicator {
            maxWidth = 100.px
            maxHeight = 100.px
        }

        approxText {
            alignment = Pos.CENTER
            label {
                fontSize = 10.px
                fontWeight = FontWeight.THIN
            }
        }
    }
}