package ru.emkn.kotlin.sms.services

import kotlinx.cli.*
import ru.emkn.kotlin.sms.data.Arguments
import ru.emkn.kotlin.sms.data.CommandResults
import ru.emkn.kotlin.sms.data.CommandResultsGroup
import ru.emkn.kotlin.sms.data.CommandStart
import ru.emkn.kotlin.sms.utils.UndefinedCommandException

@ExperimentalCli
object ArgumentsHandler {
    private const val programName = "Competition"

    abstract class MySubcommand(name: String, actionDescription: String) : Subcommand(name, actionDescription) {
        var use = false

        override fun execute() {
            use = true
        }
    }

    class ProtocolsStart : MySubcommand("protocolStart", "Get start protocols") {
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
        val title by parser.argument(ArgType.String, description = "Name of the competition")
        val date by parser.argument(ArgType.String, description = "Date of the competition")

        val protocolsStart = ProtocolsStart()
        val resultsGroup = ResultsGroup()
        val resultsTeam = ResultsTeam()
        parser.subcommands(protocolsStart, resultsGroup, resultsTeam)
        parser.parse(args)
        return Arguments(
            title = title,
            date = Arguments.checkDate(date),
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
                else -> throw UndefinedCommandException()
            }
        )
    }
}