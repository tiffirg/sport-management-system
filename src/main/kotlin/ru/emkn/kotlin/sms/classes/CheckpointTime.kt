package ru.emkn.kotlin.sms.classes

import java.time.LocalDateTime
import java.time.LocalTime

data class CheckpointTime(val checkpoint: String, val time: LocalTime) {
    init {
        require(true) { "message" }
    }
}
