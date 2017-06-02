package com.slicky.gitcompare.model

import tornadofx.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Created by SlickyPC on 28.5.2017
 */
class GithubSpecialQuery(vararg params: GithubSpecialParameter) {
    val string = "?q=${params.joinToString(" ").urlEncoded}"
}

sealed class GithubSpecialOperation(val operator: String)

object EqualsWith : GithubSpecialOperation("") {
    override fun toString() = "Equals"
}

object MoreThan : GithubSpecialOperation(">") {
    override fun toString() = "More"
}

object EqualsMoreThan : GithubSpecialOperation(">=") {
    override fun toString() = "Equals or More "
}

object LessThan : GithubSpecialOperation("<") {
    override fun toString() = "Less"
}

object EqualsLessThan : GithubSpecialOperation("<=") {
    override fun toString() = "Equals or Less"
}

sealed class GithubSpecialParameter

// ISO 8601
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

class Language(val language: String) : GithubSpecialParameter() {
    override fun toString() = "language:$language"
}

class Size(val operation: GithubSpecialOperation, val bytes: Int) : GithubSpecialParameter() {
    override fun toString() = "size:${operation.operator}$bytes"
}

class Repo(val repo: String) : GithubSpecialParameter() {
    override fun toString() = "repo:$repo"
}

class User(val user: String) : GithubSpecialParameter() {
    override fun toString() = "user:$user"
}

class Created(val createdFrom: LocalDate, val createdTo: LocalDate) : GithubSpecialParameter() {
    override fun toString() = "created:${createdFrom.format(formatter)}..${createdTo.format(formatter)}"
}

class Pushed(val pushedFrom: LocalDate, val pushedTo: LocalDate) : GithubSpecialParameter() {
    override fun toString() = "pushed:${pushedFrom.format(formatter)}..${pushedTo.format(formatter)}"
}

class Forks(val operation: GithubSpecialOperation, val forks: Int) : GithubSpecialParameter() {
    override fun toString() = "forks:${operation.operator}$forks"
}

class Stars(val operation: GithubSpecialOperation, val stars: Int) : GithubSpecialParameter() {
    override fun toString() = "stars:${operation.operator}$stars"
}

class Topic(val topic: String) : GithubSpecialParameter() {
    override fun toString() = "topic:$topic"
}

class In(val `in`: String) : GithubSpecialParameter() {
    override fun toString() = "in:$`in`"
}
