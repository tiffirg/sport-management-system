package ru.emkn.kotlin.sms.services

import kotlinx.cli.*
import ru.emkn.kotlin.sms.data.Arguments
import ru.emkn.kotlin.sms.data.Command
import ru.emkn.kotlin.sms.data.Path
import ru.emkn.kotlin.sms.data.TypeCommand.*

fun getPathsProtocolsStart(): List<Path> {
    TODO()  // Exception, если не найдены
}

fun getPathsResults(): List<Path> {
    TODO()  // Exception, если не найдены
}

fun readStream(): List<Path> {
    TODO()
}

@ExperimentalCli
object ArgumentsHandler {
    private const val programName = "сompetition"

    class ProtocolsStart : Subcommand("protocolStart", "Get start protocols") {
        private val pathsRequests by argument(ArgType.String, description = "Paths to requests lists").vararg()
        var result: List<Path>? = null
        var use = false

        override fun execute() {
            use = true
            result = pathsRequests.map { Path(it) }
        }
    }

    class ResultsAthlete : Subcommand("resultsAthlete", "Get results for each athlete") {
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
        var use = false

        override fun execute() {
            use = true
            result = pathsProtocolsCheckpoint.map { Path(it) }
            resultsPathsProtocolsStart = pathsProtocolsStart.map { Path(it) }
        }
    }

    class ResultsTeam : Subcommand("resultsTeam", "Get results for each team") {
        private val pathsResults by argument(ArgType.String, description = "Paths to results for each athlete").vararg()
            .optional()
        var result: List<Path>? = null
        var use = false

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
            date = Arguments.transformDate(date),
            command = when {
                protocolsStart.use -> processCommandStart(protocolsStart)
                resultsAthlete.use -> processCommandResultsAthlete(resultsAthlete)
                resultsTeam.use -> processCommandResultsTeam(resultsTeam)
                else -> throw Exception()  // Exit Code UNDEFINED_COMMAND
            }
        )
    }

    private fun processCommandStart(protocolsStart: ProtocolsStart): Command {
        return Command(
            START,
            pathsRequests = protocolsStart.result,
            null,
            null,
            null
        )
    }

    private fun processCommandResultsAthlete(resultsAthlete: ResultsAthlete): Command {
        return Command(
            RESULTS_ATHLETE,
            null,
            pathsProtocolsStart = resultsAthlete.resultsPathsProtocolsStart,
            pathsProtocolsCheckpoint = resultsAthlete.result,
            null
        )
    }

    private fun processCommandResultsTeam(resultsTeam: ResultsTeam): Command {
        return Command(
            RESULTS_TEAM,
            null,
            null,
            null,
            pathsResults = resultsTeam.result,
        )
    }
}