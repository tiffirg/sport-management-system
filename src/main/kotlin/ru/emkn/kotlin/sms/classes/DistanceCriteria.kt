package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.logger
import ru.emkn.kotlin.sms.utils.InvalidConfigData
import ru.emkn.kotlin.sms.utils.messageAboutIncorrectDataCheckpointOfAthlete
import java.time.Duration

enum class DistanceType {
    FIXED, CHOICE
}

fun getCriteriaByType(typeName: String, checkpoints: List<String>): DistanceCriteria {
    return when (typeName) {
        "fixed" -> {
            DistanceType.FIXED
            FixedRoute(checkpoints)
        }
        "choice" -> {
            DistanceType.CHOICE
            if (checkpoints.size != 1) {
                throw InvalidConfigData("for Choice Route use one parameter: number of checkpoints")
            }
            val checkpointsCount = checkpoints[0].toIntOrNull()
                ?: throw InvalidConfigData("for Choice Route use one parameter: number of checkpoints")
            ChoiceRoute(checkpointsCount)
        }
        else -> {
            throw InvalidConfigData("$typeName is an invalid distance type")
        }
    }
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
        return if (!isValid(competitorData)) {
            null
        } else {
            val finishTime = competitorData.orderedCheckpoints.last().time
            val startTime = competitorData.competitor.startTime
            Duration.between(startTime, finishTime)
        }
    }

    override fun getSplit(competitorData: CompetitorData): List<CheckpointDuration>? {
        return if (!isValid(competitorData)) {
            null
        } else {
            // генерация сплитов: время на 1 КП - разница между временем отсечки и временем старта
            // время на последующие КП - разница времен текущего и предыдущего КП
            val splits = mutableListOf<CheckpointDuration>()
            competitorData.orderedCheckpoints.forEachIndexed { index, _ ->
                if (index == 0) {
                    val firstCheckpoint = competitorData.orderedCheckpoints[0]
                    splits.add(
                        CheckpointDuration(
                            firstCheckpoint.checkpoint,
                            Duration.between(competitorData.competitor.startTime, firstCheckpoint.time)
                        )
                    )
                } else {
                    val currCheckpoint = competitorData.orderedCheckpoints[index]
                    val prevCheckpoint = competitorData.orderedCheckpoints[index - 1]
                    splits.add(
                        CheckpointDuration(
                            currCheckpoint.checkpoint,
                            Duration.between(prevCheckpoint.time, currCheckpoint.time)
                        )
                    )
                }
            }
            splits
        }
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