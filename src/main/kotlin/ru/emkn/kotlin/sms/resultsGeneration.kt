package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.*
import ru.emkn.kotlin.sms.utils.TimeFormatter
import java.time.LocalTime



data class ResultAthleteInGroup(val athleteNumberInGroup: Int, val athleteNumber: Int,
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




data class ResultsGroup(val group: Group, val results: List<ResultAthleteInGroup>) {

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


const val INF = 1000000

fun LocalTime.minus(time: LocalTime?): LocalTime {
    return if (time == null) {
        this
    } else {
        val totalSeconds = this.toSecondOfDay() - time.toSecondOfDay()
        val hour = totalSeconds / (60 * 60)
        val minute = totalSeconds / 60 - hour * 60
        val second = totalSeconds % 60
        LocalTime.of(hour, minute, second)
    }
}


fun getAthleteResult(athlete: Athlete): LocalTime? {
    athlete.checkCheckpoints()
    return if (athlete.removed) {
        null
    } else {
        val finishTime = athlete.checkpoints!!.last().time
        finishTime.minus(athlete.startTime)
    }
}


fun generateResultsGroup(athletesGroup: AthletesGroup): List<ResultAthleteInGroup> {

    // TODO: присвоение разрядов

    // Атлеты сортируются по времени результата
    // Если человек дисквалифицирован, то его результатом буде специальное значение

    val sortedAthletes = athletesGroup.athletes.sortedBy { athlete ->
        val resultTimeOrNull = getAthleteResult(athlete)
        resultTimeOrNull?.toSecondOfDay() ?: INF
    }

    val protocols: List<ResultAthleteInGroup> = sortedAthletes.mapIndexed { index, athlete ->
        ResultAthleteInGroup(
            index + 1, athlete.athleteNumber!!,
            athlete.surname, athlete.name, athlete.birthYear,
            athlete.rank, athlete.teamName, getAthleteResult(athlete),
            index + 1, ""
        )
    }
    return protocols
}


fun generateResults(dataCheckpoints: List<Athlete>): Map<Group, ResultsGroup> {
    val athletesGroups = dataCheckpoints.groupBy { athlete -> athlete.group }
    val protocols = (athletesGroups.map { (group, athletesGroup) ->
        Pair(group, ResultsGroup(group, generateResultsGroup(AthletesGroup(group, athletesGroup))))
    }).toMap()
    protocols.toSortedMap(compareBy { it.groupName })
    return protocols
}