package ru.emkn.kotlin.sms.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

val TimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
val DateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun String.toLocalDate(): LocalDate? {
    return try {
        LocalDate.parse(this, DateFormat)
    } catch (e: DateTimeParseException) {
        null
    }
}

fun String.toLocalTime(): LocalTime? {
    return try {
        LocalTime.parse(this, TimeFormatter)
    } catch (e: DateTimeParseException) {
        null
    }
}

