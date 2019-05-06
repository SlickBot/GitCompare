package com.slicky.gitcompare.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.time.LocalDate

/**
 * Created by SlickyPC on 25.5.2017
 */
class RepositoryRequest {
    val languageProperty = SimpleStringProperty("Kotlin")
    val sizeProperty = SimpleIntegerProperty(100)
    val sizeOptionProperty = SimpleObjectProperty<GithubSpecialOperation>(EqualsMoreThan)
    val starsProperty = SimpleIntegerProperty(0)
    val starsOptionProperty = SimpleObjectProperty<GithubSpecialOperation>(EqualsMoreThan)
    val forksProperty = SimpleIntegerProperty(0)
    val forksOptionProperty = SimpleObjectProperty<GithubSpecialOperation>(EqualsMoreThan)
    val topicProperty = SimpleStringProperty("")
    val createdFromProperty = SimpleObjectProperty<LocalDate>(LocalDate.now().minusYears(1))
    val createdToProperty = SimpleObjectProperty<LocalDate>(LocalDate.now())
    val periodUnitProperty = SimpleObjectProperty<PeriodUnit>()
    val periodAmountProperty = SimpleIntegerProperty(1)
    val approxProperty = SimpleStringProperty("")
    val rpmProperty = SimpleIntegerProperty(30)
}

class RepositoryRequestModel(val request: RepositoryRequest) : ViewModel() {
    val language = bind { request.languageProperty }
    val sizeAmount = bind { request.sizeProperty }
    val sizeOption = bind { request.sizeOptionProperty }
    val starsAmount = bind { request.starsProperty }
    val starsOption = bind { request.starsOptionProperty }
    val forksAmount = bind { request.forksProperty }
    val forksOption = bind { request.forksOptionProperty }
    val topic = bind { request.topicProperty }
    val createdFrom = bind { request.createdFromProperty }
    val createdTo = bind { request.createdToProperty }
    val periodAmount = bind { request.periodAmountProperty }
    val periodUnit = bind { request.periodUnitProperty }
    val approx = bind { request.approxProperty }
    val rpm = bind { request.rpmProperty }
}
