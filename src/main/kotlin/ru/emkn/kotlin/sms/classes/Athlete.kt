package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.DISTANCE_CRITERIA
import ru.emkn.kotlin.sms.utils.TimeFormatter
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

    fun checkCheckpoints() {
        if (checkpoints.isNullOrEmpty()) {
            removed = true
            return
        }
        val orderedCheckpoints = DISTANCE_CRITERIA[group.distance]!!
        checkpoints?.let { data ->
            if (data.mapTo(mutableSetOf()) {it.checkpoint } != orderedCheckpoints.toSet()) {
                removed = true
                return
            }
            val sortedData = data.sortedBy { el -> orderedCheckpoints.indexOfFirst { el.checkpoint == it} }
            checkpoints = sortedData as MutableList<CheckpointTime>?
            for (i in 1 until sortedData.size) {
                if (sortedData[i - 1].time >= sortedData[i].time) {
                    removed = true
                    return
                }
            }
            removed = false
        }
    }
}
