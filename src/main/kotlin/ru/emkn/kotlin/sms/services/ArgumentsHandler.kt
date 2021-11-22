package ru.emkn.kotlin.sms.services

import kotlinx.cli.*
import ru.emkn.kotlin.sms.data.*
import ru.emkn.kotlin.sms.utils.transformDate

@ExperimentalCli
object ArgumentsHandler {
    private const val programName = "сompetition"

    abstract class MySubcommand(name: String, actionDescription: String) : Subcommand(name, actionDescription) {
        var use = false
    }

    class ProtocolsStart : MySubcommand("protocolStart", "Get start protocols") {
        private val pathsRequests by argument(ArgType.String, description = "Paths to requests lists").vararg()
        var result: List<Path>? = null

        override fun execute() {
            use = true
            result = pathsRequests.map { Path(it) }
        }
    }

    class ResultsAthlete : MySubcommand("resultsAthlete", "Get results for each athlete") {
        private val pathsProtocolsCheckpoint by argument(
            ArgType.String,
            description = "Paths to checkpoint protocols"
        ).vararg().optional()
        private val pathsProtocolsStart by option(
            ArgType.String,
            fullName = "protocolsStart",
            shortName = "ps",
            description = "Paths to start protocols or nothing"
        ).delimiter(" ")

        var result: List<Path>? = null
        var resultsPathsProtocolsStart: List<Path>? = null

        override fun execute() {
            use = true
            result = pathsProtocolsCheckpoint.map { Path(it) }
            resultsPathsProtocolsStart = pathsProtocolsStart.map { Path(it) }
        }
    }

    class ResultsTeam : MySubcommand("resultsTeam", "Get results for each team") {
        private val pathsResults by argument(ArgType.String, description = "Paths to results for each athlete").vararg()
            .optional()
        var result: List<Path>? = null

        override fun execute() {
            use = true
            result = pathsResults.map { Path(it) }
        }
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
                else -> throw Exception()  // Exit Code UNDEFINED_COMMAND
            }
        )
    }
}