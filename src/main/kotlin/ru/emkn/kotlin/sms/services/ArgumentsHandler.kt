package ru.emkn.kotlin.sms.services

import kotlinx.cli.*
import ru.emkn.kotlin.sms.data.Arguments
import ru.emkn.kotlin.sms.data.CommandResults
import ru.emkn.kotlin.sms.data.CommandResultsAthlete
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

    class ResultsAthlete : MySubcommand("resultsAthlete", "Get results for each athlete") {
        val pathProtocolCheckpoint by argument(
            ArgType.String,
            description = "Path to checkpoint protocol"
        ).optional()
        val pathProtocolStart by option(
            ArgType.String,
            fullName = "protocolStart",
            shortName = "ps",
            description = "Path to start protocol or nothing"
        )
    }

    class ResultsTeam : MySubcommand("resultsTeam", "Get results for each team") {
        val pathResultsAthlete by argument(ArgType.String, description = "Path to results for each athlete").optional()
    }

    fun apply(args: Array<String>): Arguments {
        val parser = ArgParser(programName, strictSubcommandOptionsOrder = true)
        val title by parser.argument(ArgType.String, description = "Name of the competition")
        val date by parser.argument(ArgType.String, description = "Date of the competition")

        val protocolsStart = ProtocolsStart()
        val resultsAthlete = ResultsAthlete()
        val resultsTeam = ResultsTeam()
        parser.subcommands(protocolsStart, resultsAthlete, resultsTeam)
        parser.parse(args)
        return Arguments(
            title = title,
            date = Arguments.checkDate(date),
            command = when {
                protocolsStart.use -> CommandStart(
                    pathsRequests = protocolsStart.pathsRequests
                )
                resultsAthlete.use -> CommandResultsAthlete(
                    pathProtocolStart = resultsAthlete.pathProtocolStart,
                    pathProtocolCheckpoint = resultsAthlete.pathProtocolCheckpoint,
                )
                resultsTeam.use -> CommandResults(
                    pathResultsAthlete = resultsTeam.pathResultsAthlete,
                )
                else -> throw UndefinedCommandException()
            }
        )
    }
}