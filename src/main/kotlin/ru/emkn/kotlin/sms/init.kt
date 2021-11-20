package ru.emkn.kotlin.sms

import com.sksamuel.hoplite.ConfigLoader

data class ConfigData(val sport: String, val groups: List<String>, val ranks: List<String>)

val config = ConfigLoader().loadConfigOrThrow<ConfigData>("/config.yaml")
val sport = config.sport
val groups = config.groups
val ranks = config.ranks

