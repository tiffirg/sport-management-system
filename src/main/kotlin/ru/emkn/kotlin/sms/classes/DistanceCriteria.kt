package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.LOGGER
import ru.emkn.kotlin.sms.utils.InvalidConfigData
import ru.emkn.kotlin.sms.utils.messageAboutIncorrectDataCheckpointOfAthlete
import java.time.Duration

enum class DistanceType {
    FIXED, CHOICE
}

fun getCriteriaByType(typeName: String, count: String, checkpoints: List<String>): DistanceCriteria {
    val checkpointsCount: Int = count.toIntOrNull()
        ?: throw InvalidConfigData("the count of checkpoints must be an integer")
    return when (typeName) {
        "fixed" -> {
            if (checkpointsCount != checkpoints.size) {
                throw InvalidConfigData("Checkpoints count and list of checkpoints size don't match")
            }
            FixedRoute(checkpoints)
        }
        "choice" -> {
            if (checkpoints.isEmpty()) {
                throw InvalidConfigData("no parameters got for Choice Route")
            } else {
                if (checkpoints.isEmpty()) {
                    ChoiceRoute(checkpointsCount, listOf())
                } else {
                    ChoiceRoute(checkpointsCount, checkpoints)
                }
            }
        }
        else -> {
            throw InvalidConfigData("$typeName is an invalid distance type")
        }
    }
}

interface DistanceCriteria {
    val checkpointsOrder: List<String>
    val checkpointsCount: Int
    val distanceType: DistanceType
    fun isValid(competitorData: CompetitorData): Boolean

    fun getResult(competitorData: CompetitorData): Duration? {
        return if (!isValid(competitorData)) {
            null
        } else {
            val finishTime = competitorData.orderedCheckpoints.last().time
            val startTime = competitorData.competitor.startTime
            Duration.between(startTime, finishTime)
        }
    }

    fun getSplit(competitorData: CompetitorData): List<CheckpointDuration>? {
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

class FixedRoute(override val checkpointsOrder: List<String>) : DistanceCriteria {
    override val checkpointsCount = checkpointsOrder.size
    override val distanceType = DistanceType.FIXED

    override fun isValid(competitorData: CompetitorData): Boolean {

        /*
        В данном типе дистанции участник должен пройти чекпоинты строго в заданном checkpointsOrder порядке.
        Один чекпоинт может встречаться в заданном списке несколько раз.
        Любое нарушение порядка отметок или их количества приведут к дисквалификации участника
         */

        val competitor = competitorData.competitor
        val checkpoints = competitorData.orderedCheckpoints

        if (checkpoints.size != checkpointsOrder.size) {
            LOGGER.info {
                messageAboutIncorrectDataCheckpointOfAthlete(
                    competitor,
                    " invalid checkpoints number for the fixed route"
                )
            }
            return false
        }

        checkpointsOrder.forEachIndexed { ind, checkpointName ->
            if (checkpoints[ind].checkpoint != checkpointName) {
                LOGGER.info {
                    messageAboutIncorrectDataCheckpointOfAthlete(
                        competitor,
                        " invalid checkpoints order on the fixed route"
                    )
                }
                return false
            }
        }
        return true
    }

}

class ChoiceRoute(override val checkpointsCount: Int, override val checkpointsOrder: List<String>) : DistanceCriteria {

    override val distanceType = DistanceType.CHOICE

    override fun isValid(competitorData: CompetitorData): Boolean {

        /*
        В данном типе дистанции участнику предлагается взять K (checkpointsCount) любых чекпоинтов
        из определенного списка (checkpointsRange) или из любых доступных (checkpointsRange = null) чекпоинтов.
        При этом повторное прохождение чекпоинта не учитывается, а также не учитывается отметка на
        чекпоинтах не из заданного списка. За большее, чем указано, количество взятых чекпоинтов участник
        не дисквалифицируется, но и не получает никаких преимуществ.
         */

        val competitor = competitorData.competitor
        val checkpoints = competitorData.orderedCheckpoints
        val validCheckPointsSet = if (checkpointsOrder.isEmpty()) {
            checkpoints.toSet()
        } else {
            checkpoints.filter { checkpointsOrder.contains(it.checkpoint) }.toSet()
        }
        return if (validCheckPointsSet.size < checkpointsCount) {
            LOGGER.info {
                messageAboutIncorrectDataCheckpointOfAthlete(
                    competitor,
                    " not enough checkpoints for the choice route: " +
                            "expected at least $checkpointsCount different checkpoints, but got only ${validCheckPointsSet.size}"
                )
            }
            false
        } else {
            true
        }
    }

}