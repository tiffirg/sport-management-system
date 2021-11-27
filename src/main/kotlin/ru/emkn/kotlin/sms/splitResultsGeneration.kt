package ru.emkn.kotlin.sms
import ru.emkn.kotlin.sms.classes.*
import java.time.LocalTime



fun getAthleteSplit(athlete: Athlete): List<LocalTime>? {
    require(athlete.checkpoints != null)
    val orderedCheckpoints = DISTANCE_CRITERIA[athlete.group.distance]
    val athleteData = athlete.checkpoints!!.associate { Pair(it.checkpoint, it.time) }
    val orderedData = orderedCheckpoints?.associate { checkpoint -> Pair(checkpoint, athleteData[checkpoint]) }
    val splits : List<LocalTime>

    return null
}


fun generateSplitResultsGroup(athletesGroup: AthletesGroup): List<ResultAthleteGroup> {
    TODO()
}


fun generateSplitResults(dataCheckpoints: List<Athlete>): Map<Group, Any> {
    TODO()
}