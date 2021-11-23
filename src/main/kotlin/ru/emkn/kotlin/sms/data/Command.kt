package ru.emkn.kotlin.sms.data

enum class TypeCommand {
    START,
    RESULTS_ATHLETE,
    RESULTS_TEAM
}

interface Command {
    val type: TypeCommand
}

data class CommandStart(val pathsRequests: List<String>) : Command {
    override val type = TypeCommand.START

}

data class CommandResultsGroup(
    val pathProtocolStart: String?,
    val pathProtocolCheckpoint: String?,
    val isCheckpointAthlete: Boolean
) : Command {
    override val type = TypeCommand.RESULTS_ATHLETE
}

data class CommandResults(val pathResultsAthlete: String?) : Command {
    override val type = TypeCommand.RESULTS_TEAM
}
