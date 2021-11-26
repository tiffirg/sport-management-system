package ru.emkn.kotlin.sms.utils

import ru.emkn.kotlin.sms.classes.Rank
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun transformDate(date: String): LocalDate? {
    return try {
        LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    } catch (e: DateTimeParseException) {
        null
    }
}

fun existPathFile(path: String): Boolean {
    return File(path).exists()
}
