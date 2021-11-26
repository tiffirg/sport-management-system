package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.utils.TimeFormatter
import java.sql.Time
import java.time.LocalDateTime
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
}
