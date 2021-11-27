package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.utils.TimeFormatter
import java.time.LocalTime

data class Team(val teamName: String, val athletes: List<Athlete>)

data class AthletesGroup(val group: Group, val athletes: List<Athlete>)

data class ResultAthleteGroup(val athleteNumberInGroup: Int, val athleteNumber: Int,
                              val surname: String, val name: String, val birthYear: Int,
                              val rank: Rank, val teamName: String, val result: LocalTime?,
                              val place: Int, val backlog: String) {

    val listForResultsGroup: List<String>
        get() {
            return listOf(
                athleteNumberInGroup.toString(),
                athleteNumber.toString(),
                surname,
                name,
                birthYear.toString(),
                rank.rankName ?: "",
                teamName,
                result?.format(TimeFormatter) ?: "снят",
                place.toString(),
                backlog
            )
        }
}

data class ResultsGroup (val group: Group, val results: List<ResultAthleteGroup>)

