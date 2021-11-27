package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.*
import java.time.LocalTime


// TODO("проверить корректность прохождения дистанции участником? С помощью Athlete.CheckCheckpoints")
fun getAthleteResult(athlete: Athlete): LocalTime? {
    return if (athlete.checkpoints != null) {
        athlete.checkpoints!!.last().time
    } else {
        null
    }
}

fun generateResultsGroup(athletesGroup: List<Athlete>): List<ResultAthleteGroup> {
    // TODO("добавить присвоение разрядов")
    athletesGroup.sortedBy { athlete -> getAthleteResult(athlete) }
    val protocols: List<ResultAthleteGroup> = athletesGroup.mapIndexed { index, athlete ->
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
        Pair(group, ResultsGroup(group, generateResultsGroup(athletesGroup)))
    }).toMap()
    return protocols
}