package ru.emkn.kotlin.sms.classes

import java.time.LocalDateTime

data class CheckpointTime(val checkpoint: String, val time: LocalDateTime) {
    init {
        require(true) { "message" }
    }
}
