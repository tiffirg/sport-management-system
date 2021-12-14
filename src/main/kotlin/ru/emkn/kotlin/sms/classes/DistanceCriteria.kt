package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.logger
import ru.emkn.kotlin.sms.utils.messageAboutIncorrectDataCheckpointOfAthlete
import java.time.Duration

enum class DistanceType {
    FIXED, CHOICE
}

interface DistanceCriteria {
    val distanceType: DistanceType
    fun isValid(competitorData: CompetitorData): Boolean
    fun getResult(competitorData: CompetitorData): Duration?
    fun getSplit(competitorData: CompetitorData): List<CheckpointDuration>?
}

class FixedRoute(private val checkpointsOrder: List<String>) : DistanceCriteria {

    override val distanceType = DistanceType.FIXED

    override fun isValid(competitorData: CompetitorData): Boolean {
        val competitor = competitorData.competitor
        val checkpoints = competitorData.orderedCheckpoints

        if (checkpoints.size != checkpointsOrder.size) {
            logger.info {
                messageAboutIncorrectDataCheckpointOfAthlete(
                    competitor,
                    "invalid number of checkpoints"
                )
            }
            return false
        }

        checkpointsOrder.forEachIndexed { ind, checkpointName ->
            if (checkpoints[ind].checkpoint != checkpointName) {
                logger.info {
                    messageAboutIncorrectDataCheckpointOfAthlete(
                        competitor,
                        "invalid order of checkpoints"
                    )
                }
                return false
            }
        }
        return true
    }


    override fun getResult(competitorData: CompetitorData): Duration? {
        TODO("Not yet implemented")
    }

    override fun getSplit(competitorData: CompetitorData): List<CheckpointDuration>? {
        TODO("Not yet implemented")
    }

}

class ChoiceRoute(private val checkpointsCount: Int) : DistanceCriteria {

    override val distanceType = DistanceType.CHOICE

    override fun isValid(competitorData: CompetitorData): Boolean {
        val competitor = competitorData.competitor
        val checkpoints = competitorData.orderedCheckpoints
        if (checkpoints.size != checkpointsCount) {
            logger.info {
                messageAboutIncorrectDataCheckpointOfAthlete(
                    competitor,
                    "invalid number of checkpoints"
                )
            }
            return false
        }
        return true
    }

    override fun getResult(competitorData: CompetitorData): Duration? {
        TODO("Not yet implemented")
    }

    override fun getSplit(competitorData: CompetitorData): List<CheckpointDuration>? {
        TODO("Not yet implemented")
    }
}