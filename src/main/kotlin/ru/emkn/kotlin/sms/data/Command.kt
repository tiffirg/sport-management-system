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

data class CommandResultsAthlete(
    val pathsProtocolsStart: List<String>,
    val pathsProtocolsCheckpoint: List<String>
) : Command {
    override val type = TypeCommand.RESULTS_ATHLETE
}

data class CommandResults(val pathsResults: List<String>) : Command {
    override val type = TypeCommand.RESULTS_TEAM
}


//data class Command(
//    val type: TypeCommand,
//    val pathsRequests: List<Path>?,
//    val pathsProtocolsStart: List<Path>?,
//    val pathsProtocolsCheckpoint: List<Path>?,
//    val pathsResults: List<Path>?
//)
