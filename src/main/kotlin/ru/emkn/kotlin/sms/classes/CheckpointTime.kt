package ru.emkn.kotlin.sms.classes

import java.time.Duration
import java.time.LocalTime

data class CheckpointTime(val checkpoint: String, val time: LocalTime)

data class CheckpointDuration(val checkpoint: String, val duration: Duration)