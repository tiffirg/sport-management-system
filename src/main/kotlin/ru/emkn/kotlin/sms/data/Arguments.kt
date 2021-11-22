package ru.emkn.kotlin.sms.data

import ru.emkn.kotlin.sms.utils.transformDate
import java.time.LocalDate

data class Arguments(
    val title: String,
    val date: LocalDate,
    val command: Command
) {
   companion object {
       fun checkDate(date: String): LocalDate {
           return transformDate(date)?: throw Exception() // Exception: INVALID_DATE
       }
   }
}
