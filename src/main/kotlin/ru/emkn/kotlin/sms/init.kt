package ru.emkn.kotlin.sms

import com.sksamuel.hoplite.ConfigLoader
import ru.emkn.kotlin.sms.utils.transformDate
import java.time.format.DateTimeFormatter

const val PATH_CONFIG = "/config.yaml"

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

val config = ConfigLoader().loadConfigOrThrow<ConfigData>(PATH_CONFIG)

val RANKS = config.ranks
val GROUP_NAMES = config.groups.map { it.group }
val GROUP_DISTANCES = config.groups.associate { groupData -> Pair(groupData.group, groupData.distance) }
val DISTANCE_CRITERIA =
    config.criteria.associate { criteriaData -> Pair(criteriaData.distance, criteriaData.checkpoints) }

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
val COMPETITION_DATE = transformDate(config.eventDate)
