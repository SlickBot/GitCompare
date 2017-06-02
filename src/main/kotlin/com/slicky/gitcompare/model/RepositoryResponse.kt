package com.slicky.gitcompare.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import tornadofx.*
import javax.json.JsonObject

/**
 * Created by SlickyPC on 25.5.2017
 */
class RepositoryResponse : JsonModel {

    val totalProperty = SimpleIntegerProperty()
    var total by totalProperty

    val incompleteProperty = SimpleBooleanProperty()
    var incomplete by incompleteProperty

    override fun updateModel(json: JsonObject) {
        with(json) {
            total = int("total_count") ?: -1
            incomplete = boolean("incomplete_results") ?: true
        }
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("total_count", total)
            add("incomplete_results", incomplete)
        }
    }
}
