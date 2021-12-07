package ru.emkn.kotlin.sms.classes

import java.time.Duration

data class Team(val teamName: String, val competitors: List<Competitor>)

data class AthletesGroup(val group: Group, val competitors: List<Competitor>)

data class TeamResults(val teamName: String, val teamScore: Int, val data: List<CompetitorResultInTeam>)

data class GroupResults(val group: Group, val results: List<CompetitorResultInGroup>) {

    private val leaderTime: Duration
        get() = this.results[0].result

    fun getCompetitorScore(athleteNumber: Int): Int {
        val athleteTime = this.results.find { resultAthleteGroup ->
            resultAthleteGroup.competitor.athleteNumber == athleteNumber
        }?.result
        return if (athleteTime == null) {
            0
        } else {
            val x = athleteTime.seconds.toDouble()
            val y = leaderTime.seconds.toDouble()
            0.coerceAtLeast((100 * (2 - x / y)).toInt())
        }
    }
}

data class GroupSplitResults(val group: Group, val results: List<CompetitorSplitResultInGroup>)


