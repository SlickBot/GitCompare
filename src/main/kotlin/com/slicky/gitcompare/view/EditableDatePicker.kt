package com.slicky.gitcompare.view

import javafx.beans.property.Property
import javafx.event.EventTarget
import javafx.scene.control.DatePicker
import tornadofx.*
import java.time.LocalDate

/**
 * Created by SlickyPC on 26.5.2017
 */
class EditableDatePicker : DatePicker() {

    private var beforeOp: (EditableDateCell.(LocalDate?) -> Boolean)? = null
    private var afterOp: (EditableDateCell.(LocalDate?) -> Boolean)? = null

    fun isActiveBefore(before: EditableDateCell.(LocalDate?) -> Boolean) {
        beforeOp = before
        updateCellFactory()
    }

    fun isActiveAfter(after: EditableDateCell.(LocalDate?) -> Boolean) {
        afterOp = after
        updateCellFactory()
    }

    private fun updateCellFactory() {
        setDayCellFactory {
            EditableDateCell { date ->
                beforeOp?.invoke(this, date) ?: true && afterOp?.invoke(this, date) ?: true
            }
        }
    }
}

fun EventTarget.editabledatepicker(op: (EditableDatePicker.() -> Unit) = {}) = opcr(this, EditableDatePicker(), op)

fun EventTarget.editabledatepicker(property: Property<LocalDate>, op: (EditableDatePicker.() -> Unit)? = null) = editabledatepicker().apply {
    bind(property)
    op?.invoke(this)
}
