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


@ExperimentalCli
object ArgumentsHandler {
    private const val programName = "сompetition"

    class ProtocolsStart : Subcommand("protocolStart", "Get start protocols") {
        private val pathsRequests by argument(ArgType.String, description = "Paths to requests lists").vararg()
        lateinit var result: List<Path>

        override fun execute() {
            result = pathsRequests.map { Path(it) }
        }
    }

    class ProtocolsCheckpoint : Subcommand("protocolCheckpoint", "Get results for each athlete") {
        private val pathsProtocolsCheckpoint by argument(
            ArgType.String,
            description = "Paths to checkpoint protocols"
        ).vararg()
        private val pathsProtocolsStart by argument(
            ArgType.String,
            description = "Paths to start protocols or nothing"
        ).vararg().optional()

        lateinit var result: List<Path>
        var resultsPathsProtocolsStart: List<Path>? = null

        override fun execute() {
            result = pathsProtocolsCheckpoint.map { Path(it) }
            if (pathsProtocolsStart.isNotEmpty()) {
                resultsPathsProtocolsStart = pathsProtocolsStart.map { Path(it) }
            }
        }
    }

    class ResultsTeam : Subcommand("results", "Get results for each team") {
        private val pathsResults by argument(ArgType.String, description = "Paths to results for each athlete").vararg()
            .optional()
        lateinit var result: List<Path>

        override fun execute() {
            result = pathsResults.map { Path(it) }
        }
    }

    fun apply(args: Array<String>): Arguments {
        val parser = ArgParser(programName)
        val type by parser.argument(ArgType.String, description = "Type of the competition")
        val title by parser.argument(ArgType.String, description = "Name of the competition")
        val date by parser.argument(ArgType.String, description = "Date of the competition")

        val protocolsStart = ProtocolsStart()
        val protocolsCheckpoint = ProtocolsCheckpoint()
        val resultsTeam = ResultsTeam()
        parser.subcommands(protocolsStart, protocolsCheckpoint, resultsTeam)
        parser.parse(args)
        val command = when {
            protocolsStart.result.isNotEmpty() -> processCommandStart(protocolsStart)
            protocolsCheckpoint.result.isNotEmpty() -> processCommandCheckpoint(protocolsCheckpoint)
            resultsTeam.result.isNotEmpty() -> processCommandResult(resultsTeam)
            else -> throw Exception()  // Exit Code UNDEFINED_COMMAND
        }
        return Arguments(
            type = type,
            title = title,
            date = Arguments.transformDate(date),
            command = command
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

    private fun processCommandCheckpoint(protocolsCheckpoint: ProtocolsCheckpoint): Command {
        if (protocolsCheckpoint.resultsPathsProtocolsStart.isNullOrEmpty()) {
            protocolsCheckpoint.resultsPathsProtocolsStart = getPathsProtocolsStart()
        }
        return Command(
            CHECKPOINT,
            null,
            pathsProtocolsStart = protocolsCheckpoint.resultsPathsProtocolsStart,
            pathsProtocolsCheckpoint = protocolsCheckpoint.result,
            null
        )
    }

    private fun processCommandResult(resultsTeam: ResultsTeam): Command {
        if (resultsTeam.result.isEmpty()) {
            resultsTeam.result = getPathsResults()
        }
        return Command(
            RESULT,
            null,
            null,
            pathsProtocolsCheckpoint = resultsTeam.result,
            null
        )
    }
}
