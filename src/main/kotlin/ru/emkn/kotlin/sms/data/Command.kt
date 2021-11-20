package ru.emkn.kotlin.sms.data

enum class TypeCommand {
    START,
    CHECKPOINT,
    RESULT
}

data class Command(
    val type: TypeCommand,
    val pathsRequests: List<Path>?,
    val pathsProtocolsStart: List<Path>?,
    val pathsProtocolsCheckpoint: List<Path>?,
    val pathsResults: List<Path>?
)
