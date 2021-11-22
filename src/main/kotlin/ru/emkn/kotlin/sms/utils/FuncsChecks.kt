package ru.emkn.kotlin.sms.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun transformDate(date: String): LocalDate? {
    return try {
        LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    } catch (e: NumberFormatException) {
        null
    }
}