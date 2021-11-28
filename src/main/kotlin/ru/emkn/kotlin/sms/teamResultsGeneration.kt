package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.*


fun teamResultsGeneration(listOfGroups: MutableList<ResultsGroup>): Map<String, ResultsTeam> {


    val resultsForGroups = listOfGroups.associate { resultsGroup -> Pair(resultsGroup.group, resultsGroup.results) }
    val athleteResults = resultsForGroups.flatMap { it.value }
    val teamsResults = athleteResults.groupBy { athleteResult -> athleteResult.teamName }

    val scoresByAthleteNumber: MutableMap<Int, Int> = mutableMapOf()
    val groupByAthleteNumber: MutableMap<Int, Group> = mutableMapOf()

    listOfGroups.forEach { resultsGroup ->
        resultsGroup.results.forEach { athleteResult ->
            scoresByAthleteNumber[athleteResult.athleteNumber] =
                resultsGroup.getAthleteScore(athleteResult.athleteNumber)
            groupByAthleteNumber[athleteResult.athleteNumber] = resultsGroup.group
        }
    }

    fun generateTeamResult(teamName: String, teamResults: List<ResultAthleteInGroup>): ResultsTeam {
        val data = teamResults.map { (_, athleteNumber, surname, name, birthYear, rank, _, _, place, _) ->
            AthleteResultInTeam(
                athleteNumber, name, surname, birthYear, rank,
                groupByAthleteNumber[athleteNumber]!!, place, scoresByAthleteNumber[athleteNumber]!!
            )
        }
        val teamScore = data.sumOf { it.score }
        return ResultsTeam(teamName, teamScore, data)
    }

    val res = teamsResults.map { (teamName, teamResults) ->
        Pair(teamName, generateTeamResult(teamName, teamResults))
    }.toMap()

    return res.toSortedMap(compareByDescending { res[it]!!.teamScore })

}

