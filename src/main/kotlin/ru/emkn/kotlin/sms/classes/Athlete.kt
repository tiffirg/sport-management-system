package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.DISTANCE_CRITERIA
import ru.emkn.kotlin.sms.TimeFormatter
import ru.emkn.kotlin.sms.logger
import ru.emkn.kotlin.sms.utils.messageAboutIncorrectDataCheckpointOfAthlete
import java.time.LocalTime

data class Athlete(
    val surname: String, val name: String, val birthYear: Int,
    val group: Group, val rank: Rank, val teamName: String,
    var checkpoints: MutableList<CheckpointTime>? = null, var removed: Boolean = false,
    var athleteNumber: Int? = null, var startTime: LocalTime? = null,
) {
    val listForProtocolStart: List<String>
        get() {
            return listOf(
                athleteNumber.toString(),
                surname,
                name,
                birthYear.toString(),
                rank.rankName ?: "",
                teamName,
                startTime?.format(TimeFormatter) ?: ""
            )
        }

    override fun toString(): String {
        return "$surname $name Team: $teamName Group: ${group.groupName}"
    }

    fun checkCheckpoints() {
        if (checkpoints.isNullOrEmpty()) {
            exposeRemovedAndLogging()
            return
        }
        val orderedCheckpoints = DISTANCE_CRITERIA[group.distance]!!
        checkpoints?.let { data ->
            if (data.mapTo(mutableSetOf()) { it.checkpoint } != orderedCheckpoints.toSet()) {
                exposeRemovedAndLogging()
                return
            }
            val sortedData = data.sortedBy { el -> orderedCheckpoints.indexOfFirst { el.checkpoint == it } }
            checkpoints = sortedData as MutableList<CheckpointTime>?
            for (i in 1 until sortedData.size) {
                if (sortedData[i - 1].time >= sortedData[i].time) {
                    exposeRemovedAndLogging()
                    return
                }
            }
            removed = false
        }
    }
    private fun exposeRemovedAndLogging() {
        removed = true
        logger.info { messageAboutIncorrectDataCheckpointOfAthlete(this) }
    }
}
