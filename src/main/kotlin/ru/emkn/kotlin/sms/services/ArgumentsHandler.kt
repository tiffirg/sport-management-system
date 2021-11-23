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
        val result by argument(ArgType.String, description = "Paths to requests lists").vararg()
    }

    class ResultsAthlete : MySubcommand("resultsAthlete", "Get results for each athlete") {
        val result by argument(
            ArgType.String,
            description = "Paths to checkpoint protocols"
        ).vararg().optional()
        val resultsPathsProtocolsStart by option(
            ArgType.String,
            fullName = "protocolsStart",
            shortName = "ps",
            description = "Paths to start protocols or nothing"
        ).delimiter(" ")
    }

    class ResultsTeam : MySubcommand("resultsTeam", "Get results for each team") {
        val result by argument(ArgType.String, description = "Paths to results for each athlete").vararg()
            .optional()
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
                    pathsRequests = protocolsStart.result
                )
                resultsAthlete.use -> CommandResultsAthlete(
                    pathsProtocolsStart = resultsAthlete.resultsPathsProtocolsStart,
                    pathsProtocolsCheckpoint = resultsAthlete.result,
                )
                resultsTeam.use -> CommandResults(
                    pathsResults = resultsTeam.result,
                )
                else -> throw UndefinedCommandException()
            }
        )
    }
}