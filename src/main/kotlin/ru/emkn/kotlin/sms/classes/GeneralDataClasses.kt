package ru.emkn.kotlin.sms.classes

data class Team(val teamName: String, val athletes: List<Athlete>)

data class AthletesGroup(val group: Group, val athletes: List<Athlete>)