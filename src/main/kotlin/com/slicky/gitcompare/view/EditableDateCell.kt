package com.slicky.gitcompare.view

import javafx.scene.control.DateCell
import java.time.LocalDate

/**
 * Created by SlickyPC on 26.5.2017
 */
class EditableDateCell(val op: EditableDateCell.(LocalDate?) -> Boolean) : DateCell() {

    override fun updateItem(item: LocalDate?, empty: Boolean) {
        super.updateItem(item, empty)
        isActive = op(item)
    }

    private var isActive: Boolean = true
        set(value) {
            field = value
            isDisable = !value
            if (!value)
                style = "-fx-background-color: #ffc0cb;"
        }
}