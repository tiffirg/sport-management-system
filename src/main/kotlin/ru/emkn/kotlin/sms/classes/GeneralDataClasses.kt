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

data class ResultsTeam(val teamName: String, val teamScore: Int, val data: List<ResultsTeamString>)

data class ResultsTeamString(val startNumber: Int, val name: String, val surname: String,
                             val birthYear: Int, val rank: Rank,
                             val group: Group, val place: Int, val score: Int) {
    val listForResultsAthlete: List<String>
        get() {
            return listOf(
                startNumber.toString(),
                surname,
                name,
                birthYear.toString(),
                rank.rankName ?: "",
                group.groupName,
                place.toString(),
                score.toString()
            )
        }
}


data class ResultsGroup(val group: Group, val results: List<ResultAthleteGroup>) {

    private val leaderTime : LocalTime?
        get() = this.results[0].result

    fun getAthleteScore(athleteNumber: Int): Int {
        val athleteTime = this.results.find { resultAthleteGroup ->
            resultAthleteGroup.athleteNumber == athleteNumber
        }?.result
        return if (athleteTime == null || leaderTime == null) {
            0
        } else {
            val x = athleteTime.toSecondOfDay().toDouble()
            val y = leaderTime!!.toSecondOfDay().toDouble()
            0.coerceAtLeast((100 * (2 - x / y)).toInt())
        }
    }
}