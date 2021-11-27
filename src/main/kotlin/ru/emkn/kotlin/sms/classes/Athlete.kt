package ru.emkn.kotlin.sms.classes

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
            removed = false
            return
        }
        checkpoints?.let {
            for (i in 1 until it.size) {
                if (it[i - 1].time >= it[i].time) {
                    removed = false
                    return
                }
            }
            removed = true
        }
    }
}
