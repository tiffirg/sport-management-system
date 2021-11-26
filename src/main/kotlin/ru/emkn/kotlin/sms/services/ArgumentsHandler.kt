package ru.emkn.kotlin.sms.services

import kotlinx.cli.*
import ru.emkn.kotlin.sms.classes.Arguments
import ru.emkn.kotlin.sms.classes.CommandResults
import ru.emkn.kotlin.sms.classes.CommandResultsGroup
import ru.emkn.kotlin.sms.classes.CommandStart
import ru.emkn.kotlin.sms.utils.MissCommandException

@ExperimentalCli
object ArgumentsHandler {
    private const val programName = "Competition"

    abstract class MySubcommand(name: String, actionDescription: String) : Subcommand(name, actionDescription) {
        var use = false

        override fun execute() {
            use = true
        }
    }

    class ProtocolsStart : MySubcommand("protocolStart", "Get start protocol") {
        val pathsRequests by argument(ArgType.String, description = "Paths to requests lists").vararg()
    }

    class ResultsGroup : MySubcommand("resultsGroup", "Get results for each group") {
        val pathProtocolCheckpoint by argument(
            ArgType.String,
            description = "Path to checkpoint protocol"
        ).optional()
        val isCheckpointAthlete by option(
            ArgType.Boolean,
            fullName = "checkpointAthlete",
            shortName = "cp",
            description = "Checkpoints data by athlete").default(false)
        val pathProtocolStart by option(
            ArgType.String,
            fullName = "protocolStart",
            shortName = "ps",
            description = "Path to start protocol or nothing"
        )
    }

    class ResultsTeam : MySubcommand("resultsTeam", "Get results for each team") {
        val pathResultsGroup by argument(ArgType.String, description = "Path to results for each group").optional()
    }

    fun apply(args: Array<String>): Arguments {
        val parser = ArgParser(programName, strictSubcommandOptionsOrder = true)
        val pathConfig by parser.argument(ArgType.String, description = "Path to config")

        val protocolsStart = ProtocolsStart()
        val resultsGroup = ResultsGroup()
        val resultsTeam = ResultsTeam()
        parser.subcommands(protocolsStart, resultsGroup, resultsTeam)
        parser.parse(args)
        return Arguments(
            pathConfig = pathConfig,
            command = when {
                protocolsStart.use -> CommandStart(
                    pathsRequests = protocolsStart.pathsRequests
                )
                resultsGroup.use -> CommandResultsGroup(
                    pathProtocolStart = resultsGroup.pathProtocolStart,
                    pathProtocolCheckpoint = resultsGroup.pathProtocolCheckpoint,
                    isCheckpointAthlete = resultsGroup.isCheckpointAthlete
                )
                resultsTeam.use -> CommandResults(
                    pathResultsAthlete = resultsTeam.pathResultsGroup,
                )
                else -> throw MissCommandException()
            }
        )
    }
}