package ru.emkn.kotlin.sms.classes

import java.time.Duration
import java.time.LocalTime

data class CheckpointTime(val checkpoint: String, val time: LocalTime)

data class CheckpointDuration(val checkpoint: String, val duration: Duration)

fun Duration?.toResultFormat(): String {
    return if (this == null) {
        ""
    } else {
        val hours = this.toHoursPart().toString().padStart(2, '0')
        val minutes = this.toMinutesPart().toString().padStart(2, '0')
        val seconds = this.toSecondsPart().toString().padStart(2, '0')
        "$hours:$minutes:$seconds"
    }
}

fun Duration?.toBacklogFormat(): String {
    return if (this == null) {
        ""
    } else {
        "+${this.toResultFormat()}"
    }
}