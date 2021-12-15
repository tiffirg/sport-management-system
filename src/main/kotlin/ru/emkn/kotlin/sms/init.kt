package ru.emkn.kotlin.sms

import com.sksamuel.hoplite.ConfigLoader
import ru.emkn.kotlin.sms.classes.DistanceCriteria
import ru.emkn.kotlin.sms.classes.getCriteriaByType
import ru.emkn.kotlin.sms.utils.InvalidConfigException
import ru.emkn.kotlin.sms.utils.InvalidDateException
import ru.emkn.kotlin.sms.utils.InvalidFormatConfigException
import ru.emkn.kotlin.sms.utils.InvalidTimeException
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

val TimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
val DateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
const val REMOVED_VALUE = "снят"

private fun checkConfigDate(date: String) {
    try {
        LocalDate.parse(date, DateFormat)
    } catch (e: DateTimeParseException) {
        throw InvalidDateException(date)
    }
}

private fun checkConfigLocalTime(time: String): LocalTime {
    return time.toLocalTime() ?: throw InvalidTimeException(time)
}

fun String.toLocalTime(): LocalTime? {
    return try {
        LocalTime.parse(this, TimeFormatter)
    } catch (e: DateTimeParseException) {
        null
    }
}

fun LocalTime.minus(time: LocalTime?): LocalTime {
    return if (time == null) {
        this
    } else {
        val totalSeconds = this.toSecondOfDay() - time.toSecondOfDay()
        val hour = totalSeconds / (60 * 60)
        val minute = totalSeconds / 60 - hour * 60
        val second = totalSeconds % 60
        LocalTime.of(hour, minute, second)
    }
}

var PATH_CONFIG = ""
var EVENT_NAME = ""
var EVENT_DATE_STRING = ""
var EVENT_TIME_STRING = ""
var EVENT_TIME: LocalTime = LocalTime.MIN
var EVENT_SPORT = ""
var RANKS: List<String> = listOf()
var GROUP_NAMES: List<String> = listOf()
var GROUP_DISTANCES: Map<String, String> = mapOf()
var DISTANCE_CRITERIA: Map<String, DistanceCriteria> = mapOf()
var CHECKPOINTS_LIST: List<String> = listOf()


data class GroupData(
    val group: String,
    val distance: String
)

data class CriteriaData(
    val distance: String,
    val type: String,
    val checkpoints: List<String>
)

data class ConfigData(
    val eventName: String, val eventDate: String, val eventTime: String, val eventSport: String,
    val checkpoints: List<String>,
    val ranks: List<String>,
    val groups: List<GroupData>,
    val criteria: List<CriteriaData>
)


fun initConfig(pathConfig: String) {
    if (!File(pathConfig).exists()) {
        throw InvalidConfigException(pathConfig)
    }
    try {
        val config = ConfigLoader().loadConfigOrThrow<ConfigData>(File(pathConfig))
        PATH_CONFIG = File(pathConfig).absolutePath
        EVENT_NAME = config.eventName
        checkConfigDate(config.eventDate)
        EVENT_DATE_STRING = config.eventDate
        EVENT_TIME_STRING = config.eventTime
        EVENT_TIME = checkConfigLocalTime(config.eventTime)
        EVENT_SPORT = config.eventSport
        RANKS = config.ranks
        GROUP_NAMES = config.groups.map { it.group }
        GROUP_DISTANCES = config.groups.associate { groupData -> Pair(groupData.group, groupData.distance) }
        DISTANCE_CRITERIA = config.criteria.associate {
            criteriaData ->
            Pair(criteriaData.distance, getCriteriaByType(criteriaData.type, criteriaData.checkpoints))
        }
        CHECKPOINTS_LIST = config.checkpoints
    } catch (e: Exception) {
        logger.debug { e.message }
        throw InvalidFormatConfigException(pathConfig)
    }
}


