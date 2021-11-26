package ru.emkn.kotlin.sms.classes

import java.time.LocalDateTime

data class Team(val teamName: String, val athletes: List<Athlete>)

data class AthletesGroup(val group: Group, val athletes: List<Athlete>)

// for the protocolResults
data class MedalTable(val athleteNumberInGroup: Int, val athleteNumber: Int,
                      val surname: String, val name: String, val birthYear: Int,
                      val rank: Rank?, val teamName: String, val result: LocalDateTime?,
                      val place: Int?, val backlog: String)

data class AthleteResults(val group: Group, val resultsPerGroup: List<MedalTable>)