package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.DISTANCE_CRITERIA
import ru.emkn.kotlin.sms.logger
import ru.emkn.kotlin.sms.utils.messageAboutIncorrectDataCheckpointOfAthlete
import java.time.Duration
import java.time.LocalTime

interface Person {
    val surname: String
    val name: String
    val birthYear: Int
}

open class Competitor(
    override val surname: String, override val name: String, override val birthYear: Int,
    open val group: Group, open val rank: Rank, open val teamName: String
) : Person {
    override fun toString(): String {
        return "$surname $name Team: $teamName Group: ${group.groupName}"
    }
}

data class Athlete(
    val athleteNumber: Int,
    val startTime: LocalTime,
    override val surname: String,
    override val name: String,
    override val birthYear: Int,
    override val group: Group,
    override val rank: Rank,
    override val teamName: String
) : Competitor(surname, name, birthYear, group, rank, teamName)

data class AthleteResults(
    val athlete: Athlete,
    val checkpoints: List<CheckpointTime>,
    val removed: Boolean
) {

    companion object {
        fun checkCheckpoints(athleteStart: Athlete, checkpoints: List<CheckpointTime>): Boolean {
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

data class AthleteResultInGroup(
    val athlete: Athlete, val athleteNumberInGroup: Int,
    val result: Duration, val place: Int, var backlog: String
)

data class AthleteResultInTeam(
    val athlete: Athlete, val place: Int, val score: Int
)

data class AthleteSplitResultInGroup(
    val athleteResultInGroup: AthleteResultInGroup, val splits: List<CheckpointTime>
)
