package ru.emkn.kotlin.sms.data

enum class TypeCommand {
    START,
    RESULTS_ATHLETE,
    RESULTS_TEAM
}

data class Command(
    val type: TypeCommand,
    val pathsRequests: List<Path>?,
    val pathsProtocolsStart: List<Path>?,
    val pathsProtocolsCheckpoint: List<Path>?,
    val pathsResults: List<Path>?
)
