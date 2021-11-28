package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.*
import java.time.LocalTime


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


fun generateResultsGroup(athletesGroup: AthletesGroup): List<ResultAthleteGroup> {

    // TODO: присвоение разрядов

    // Атлеты сортируются по времени результата
    // Если человек дисквалифицирован, то его результатом буде специальное значение

    val sortedAthletes = athletesGroup.athletes.sortedBy { athlete ->
        val resultTimeOrNull = getAthleteResult(athlete)
        resultTimeOrNull?.toSecondOfDay() ?: INF
    }

    val protocols: List<ResultAthleteGroup> = sortedAthletes.mapIndexed { index, athlete ->
        ResultAthleteGroup(
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
    return protocols
}