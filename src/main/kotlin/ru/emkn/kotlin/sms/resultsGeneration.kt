package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.*
import java.time.LocalTime


const val INF = 1000000

fun LocalTime.minus(time: LocalTime?): LocalTime {
    return if (time == null) {
        this
    } else {
        val totalSeconds = this.toSecondOfDay() - time.toSecondOfDay()
        val h = totalSeconds / (60 * 60)
        val m = totalSeconds / 60 - h * 60
        val s = totalSeconds % 60
        LocalTime.of(h, m, s)
    }
}


// TODO("проверить корректность прохождения дистанции участником? С помощью Athlete.CheckCheckpoints")
fun getAthleteResult(athlete: Athlete): LocalTime? {

    if (athlete.checkpoints == null) {
        TODO("invalid athlete")
    } else {

        val distanceCriteria = DISTANCE_CRITERIA[athlete.group.distance]
        val finish = distanceCriteria?.last() ?: throw Exception() // TODO: invalid distance criteria

        return if (athlete.checkpoints != null) {
            val dataCheckpoints = athlete.checkpoints!!.associate { Pair(it.checkpoint, it.time) }
            dataCheckpoints[finish]?.minus(athlete.startTime) ?: throw Exception() // TODO: убрать вопросики
        } else {
            null
        }
    }
}


fun generateResultsGroup(athletesGroup: AthletesGroup): List<ResultAthleteGroup> {
    // TODO("добавить присвоение разрядов")

    val sortedAthletes = athletesGroup.athletes.sortedBy { athlete ->
        getAthleteResult(athlete)?.toSecondOfDay() ?: INF
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