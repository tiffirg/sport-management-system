package ru.emkn.kotlin.sms.classes

import java.time.LocalTime

data class Team(val teamName: String, val athletes: List<Competitor>)

data class AthletesGroup(val group: Group, val athletes: List<Competitor>)

data class TeamResults(val teamName: String, val teamScore: Int, val data: List<CompetitorResultInTeam>)

data class GroupResults(val group: Group, val results: List<CompetitorResultInGroup>) {

    private val leaderTime: LocalTime?
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

data class GroupSplitResults(val group: Group, val results: List<CompetitorSplitResultInGroup>)


