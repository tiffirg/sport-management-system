package ru.emkn.kotlin.sms.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Arguments(
    val title: String,
    val date: LocalDate,
    val command: Command
) {
    companion object {
        fun transformDate(date: String): LocalDate {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"))  // Exception: INVALID_DATE
        }
    }
}
