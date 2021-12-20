package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.*
import ru.emkn.kotlin.sms.services.CommandsHandler
import ru.emkn.kotlin.sms.services.CsvHandler
import ru.emkn.kotlin.sms.utils.InvalidFileException
import ru.emkn.kotlin.sms.utils.messageAboutCancelCompetition
import java.io.File

object App {
    private val pathDirectory = File(PATH_CONFIG).resolveSibling("${EVENT_NAME}_$EVENT_DATE_STRING").path
    private val dir = File(pathDirectory)
    private val pathProtocolStart = dir.resolve("ps_${EVENT_NAME}_$EVENT_DATE_STRING.csv").path
    private val pathResultsGroup = dir.resolve("rg_${EVENT_NAME}_$EVENT_DATE_STRING.csv").path
    private val pathSplitResults = dir.resolve("rs_${EVENT_NAME}_$EVENT_DATE_STRING.csv").path
    private val pathResultsTeam = File(pathDirectory).resolve("rt_${EVENT_NAME}_$EVENT_DATE_STRING.csv").path

    fun run(command: Command) {
        when (command) {
            is CommandStart -> processCommandStart(command)
            is CommandResultsGroup -> processCommandResultsGroup(command)
            is CommandResults -> processCommandResultsTeam(command)
        }
    }

    private fun processCommandStart(command: CommandStart) {
        val data = CsvHandler.parseRequests(command.pathsRequests)
        if (data.isEmpty()) {
            LOGGER.info { messageAboutCancelCompetition() }
            return
        }
        val startLists: List<CompetitorsGroup> = CommandsHandler.startProtocolsGeneration(data)
        dir.mkdir()
        CsvHandler.generationProtocolsStart(pathProtocolStart, startLists)
    }

    private fun processCommandResultsGroup(command: CommandResultsGroup) {
        if (command.pathProtocolStart.isNullOrEmpty()) {
            checkExistDir()
        } else if (!dir.exists()) {
            dir.mkdir()
        }
        val dataProtocolStart: List<Competitor> = if (command.pathProtocolStart.isNullOrEmpty()) {
            CsvHandler.parseProtocolStart(pathProtocolStart)
        } else {
            CsvHandler.parseProtocolStart(command.pathProtocolStart)
        }
        val dataCheckpoint = if (command.pathProtocolCheckpoint.isNullOrEmpty()) {
            processStream(command.isCheckpointAthlete, dataProtocolStart)
        } else {
            CsvHandler.parseCheckpoints(command.pathProtocolCheckpoint, command.isCheckpointAthlete, dataProtocolStart)
        }
        CsvHandler.generationResultsGroup(pathResultsGroup, CommandsHandler.generateResults(dataCheckpoint))
        CsvHandler.generationSplitResults(
            pathSplitResults,
            CommandsHandler.generateSplitResults(dataCheckpoint)
        )
    }

    private fun processCommandResultsTeam(command: CommandResults) {
        if (command.pathResultsGroup.isNullOrEmpty()) {
            checkExistDir()
        } else if (!dir.exists()) {
            dir.mkdir()
        }
        val dataResultsGroup = if (command.pathResultsGroup.isNullOrEmpty()) {
            CsvHandler.parseResultsGroup(pathResultsGroup)
        } else {
            CsvHandler.parseResultsGroup(command.pathResultsGroup)
        }
        CsvHandler.generationResultsTeam(
            pathResultsTeam,
            CommandsHandler.generateTeamsResults(dataResultsGroup)
        )
    }

    private fun processStream(
        isCheckpointAthlete: Boolean,
        dataProtocolStart: List<Competitor>
    ): List<CompetitorData> {          // По ТЗ - не требуется, реализован шаблон на будущее
        TODO()
    }

    private fun checkExistDir() {
        if (!dir.exists()) {
            throw InvalidFileException(pathDirectory)
        }
    }

}