package ru.emkn.kotlin.sms.utils

import ru.emkn.kotlin.sms.classes.Competitor

// Config
class InvalidConfigException(pathConfig: String) : Exception("$pathConfig: No such config")

class InvalidFormatConfigException(pathConfig: String) : Exception("$pathConfig: Invalid config format")

class InvalidConfigData(message: String) : Exception("Config data is invalid: $message")

// Check data
interface ExceptionData

class IncorrectGroupException(group: String) : Exception("$group: Incorrect group"), ExceptionData

class IncorrectRankException(rank: String) : Exception("$rank: Incorrect rank"), ExceptionData


class InvalidDateException(date: String) : Exception("$date: Invalid date format"), ExceptionData

class InvalidTimeException(time: String) : Exception("$time: Invalid time format"), ExceptionData


fun messageAboutIncorrectDataCheckpointOfAthlete(athlete: Competitor, message: String? = null): String {
    var res = "Incorrect data on the checkpoint of the $athlete"
    if (message != null) {
        res += ":$message"
    }
    return res
}