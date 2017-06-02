package com.slicky.gitcompare.model

import java.time.LocalDate

/**
 * Created by SlickyPC on 26.5.2017
 */
sealed class PeriodUnit {
    abstract fun takeFrom(other: LocalDate, amount: Long) : LocalDate
    abstract fun addTo(other: LocalDate, amount: Long) : LocalDate
}

object Days : PeriodUnit() {
    override fun toString() = "Days"
    override fun takeFrom(other: LocalDate, amount: Long): LocalDate = other.minusDays(amount)
    override fun addTo(other: LocalDate, amount: Long): LocalDate = other.plusDays(amount)
}

object Weeks : PeriodUnit() {
    override fun toString() = "Weeks"
    override fun takeFrom(other: LocalDate, amount: Long): LocalDate = other.minusWeeks(amount)
    override fun addTo(other: LocalDate, amount: Long): LocalDate = other.plusWeeks(amount)
}

object Months : PeriodUnit() {
    override fun toString() = "Months"
    override fun takeFrom(other: LocalDate, amount: Long): LocalDate = other.minusMonths(amount)
    override fun addTo(other: LocalDate, amount: Long): LocalDate = other.plusMonths(amount)
}

object Years : PeriodUnit() {
    override fun toString() = "Years"
    override fun takeFrom(other: LocalDate, amount: Long): LocalDate = other.minusYears(amount)
    override fun addTo(other: LocalDate, amount: Long): LocalDate = other.plusYears(amount)
}
