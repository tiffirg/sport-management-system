package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.*
import java.time.LocalTime
import kotlin.Double.Companion.POSITIVE_INFINITY


data class ResultAthleteInGroup(val athleteNumberInGroup: Int, val athleteNumber: Int,
                                val surname: String, val name: String, val birthYear: Int,
                                val rank: Rank, val teamName: String, val result: LocalTime?,
                                val place: Int, var backlog: String) {

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


const val INF = POSITIVE_INFINITY.toInt()


// функция вычисляет отставание от лидера
fun getBacklog(result: LocalTime?, leaderTime: LocalTime?) : String {
    return if (result == null) {
        ""
    } else {
        "+${result.minus(leaderTime).format(TimeFormatter)}"
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

    // вычисление отставания от лидера
    val leaderTime = protocols.first().result
    protocols.forEach { resultAthleteInGroup ->
        val result = resultAthleteInGroup.result
        resultAthleteInGroup.backlog = getBacklog(result, leaderTime)
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