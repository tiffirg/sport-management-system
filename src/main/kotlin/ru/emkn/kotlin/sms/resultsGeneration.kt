package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.*
import java.time.LocalTime


val disqualifiedTime: LocalTime = LocalTime.parse("23:59:59") // resulting time for those with incorrect finished
// TODO("проверить корректность прохождения дистанции участником?")
fun getAthleteResult(athlete: Athlete): LocalTime {
    return if (athlete.checkpoints != null) {
        athlete.checkpoints!!.last().time
    } else {
        disqualifiedTime
    }
}

fun generateResultsGroup(athletesGroup: List<Athlete>): List<ProtocolString> {
    // TODO("добавить присвоение разрядов")
    athletesGroup.sortedBy { athlete -> getAthleteResult(athlete) }
    val protocols: List<ProtocolString> = athletesGroup.mapIndexed { index, athlete ->
        ProtocolString(
            index + 1, athlete.athleteNumber ?: 0,
            athlete.surname, athlete.name, athlete.birthYear,
            athlete.rank, athlete.teamName, getAthleteResult(athlete),
            index + 1, ""
        )
    }
    return protocols
}


fun generateResults(dataCheckpoints: List<Athlete>): Map<Group, ProtocolGroup> {
    val athletesGroups = dataCheckpoints.groupBy { athlete -> athlete.group }
    val protocols = (athletesGroups.map { (group, athletesGroup) ->
        Pair(group, ProtocolGroup(group, generateResultsGroup(athletesGroup)))
    }).toMap()
    return protocols
}