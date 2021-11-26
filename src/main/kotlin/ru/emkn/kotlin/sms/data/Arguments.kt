package ru.emkn.kotlin.sms.data

import ru.emkn.kotlin.sms.utils.InvalidDateException
import ru.emkn.kotlin.sms.utils.transformDate
import java.time.LocalDate

data class Arguments(
    val pathConfig: String,
    val command: Command
)
