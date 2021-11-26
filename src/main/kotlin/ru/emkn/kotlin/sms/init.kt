package ru.emkn.kotlin.sms

import com.sksamuel.hoplite.ConfigLoader
import ru.emkn.kotlin.sms.utils.InvalidConfigException
import ru.emkn.kotlin.sms.utils.InvalidFormatConfigException
import ru.emkn.kotlin.sms.utils.existPathFile
import ru.emkn.kotlin.sms.utils.transformDate
import java.io.File

var PATH_CONFIG = ""
var EVENT_NAME = ""
var EVENT_DATE = ""
var EVENT_SPORT = ""
var RANKS: List<String> = listOf()
var GROUP_NAMES: List<String> = listOf()
var GROUP_DISTANCES: Map<String, String> = mapOf()
var DISTANCE_CRITERIA: Map<String, List<String>> = mapOf()

var COMPETITION_DATE = transformDate(EVENT_DATE)


data class GroupData(
    val group: String,
    val distance: String
)

data class CriteriaData(
    val distance: String,
    val checkpoints: List<String>
)

data class ConfigData(
    val eventName: String, val eventDate: String, val eventSport: String,
    val ranks: List<String>,
    val groups: List<GroupData>,
    val criteria: List<CriteriaData>
)

fun initConfig(pathConfig: String) {
    if (!existPathFile(pathConfig)) {
        throw InvalidConfigException(pathConfig)
    }
    try {
        val config = ConfigLoader().loadConfigOrThrow<ConfigData>(File(pathConfig))
        PATH_CONFIG = File(pathConfig).absolutePath
        EVENT_NAME = config.eventName
        EVENT_DATE = config.eventDate
        EVENT_SPORT = config.eventSport
    }
    catch(e: Exception) {
        // TODO(Проверить конкретную ошибку и в случае чего: println(e))
        throw InvalidFormatConfigException(pathConfig)
    }
}
