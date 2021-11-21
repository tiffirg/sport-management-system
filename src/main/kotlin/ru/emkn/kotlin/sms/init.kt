package ru.emkn.kotlin.sms

import com.sksamuel.hoplite.ConfigLoader

data class GroupData(val name: String, val distance: String, val criteria: List<String>)
data class ConfigData(val sport: String, val ranks: List<String>, val groups: List<GroupData>, val documents: List<String>)

val config = ConfigLoader().loadConfigOrThrow<ConfigData>("/config.yaml")
val SPORT = config.sport
val RANKS = config.ranks
val GROUP_NAMES = config.groups.map { it.name }
val DOCUMENTS_LIST = config.documents
