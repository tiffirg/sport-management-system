package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.DISTANCE_CRITERIA
import ru.emkn.kotlin.sms.logger
import ru.emkn.kotlin.sms.utils.messageAboutIncorrectDataCheckpointOfAthlete
import java.time.Duration
import java.time.LocalTime


open class Person(
    val surname: String,
    val name: String,
    val birthYear: Int
)


open class Athlete(
    surname: String, name: String, birthYear: Int,
    open val group: Group, open val rank: Rank, open val teamName: String
) : Person(surname, name, birthYear) {
    override fun toString(): String {
        return "$surname $name Team: $teamName Group: ${group.groupName}"
    }

    constructor(athlete: Athlete) : this(
        athlete.surname, athlete.name, athlete.birthYear,
        athlete.group, athlete.rank, athlete.teamName
    )
}


data class Competitor(
    val athleteNumber: Int,
    val startTime: LocalTime,
    val athlete: Athlete
) : Athlete(athlete)


open class CompetitorData(
    val competitor: Competitor,
    val checkpoints: List<CheckpointTime>,
    val removed: Boolean
) {
    companion object {
        fun checkCheckpoints(athleteStart: Competitor, checkpoints: List<CheckpointTime>): Boolean {
            if (checkpoints.isEmpty()) {
                logger.info { messageAboutIncorrectDataCheckpointOfAthlete(athleteStart) }
                return false
            }
            val orderedCheckpoints = DISTANCE_CRITERIA[athleteStart.group.distance]!!
            if (checkpoints.mapTo(mutableSetOf()) { it.checkpoint } != orderedCheckpoints.toSet()) {  // TODO(Fix algorithm)
                logger.info { messageAboutIncorrectDataCheckpointOfAthlete(athleteStart) }
                return false
            }
            val sortedData = checkpoints.sortedBy { el -> orderedCheckpoints.indexOfFirst { el.checkpoint == it } }
            for (i in 1 until sortedData.size) {
                if (sortedData[i - 1].time >= sortedData[i].time) {
                    logger.info { messageAboutIncorrectDataCheckpointOfAthlete(athleteStart) }
                    return false
                }
            }
            return true
        }
    }
}



data class CompetitorResultInGroup(
    val competitor: Competitor, val athleteNumberInGroup: Int,
    val result: Duration?, val place: Int, var backlog: String
)

data class CompetitorResultInTeam(
    val competitor: Competitor, val place: Int, val score: Int
)

data class CompetitorSplitResultInGroup(
    val competitorResultInGroup: CompetitorResultInGroup, val splits: List<CheckpointTime>
)
