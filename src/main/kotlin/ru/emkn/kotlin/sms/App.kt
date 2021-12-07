package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.Competitor
import ru.emkn.kotlin.sms.classes.CompetitorsGroup
import ru.emkn.kotlin.sms.classes.Command
import ru.emkn.kotlin.sms.classes.CommandResults
import ru.emkn.kotlin.sms.classes.CommandResultsGroup
import ru.emkn.kotlin.sms.classes.CommandStart
import ru.emkn.kotlin.sms.classes.CheckpointTime
import ru.emkn.kotlin.sms.services.CsvHandler
import ru.emkn.kotlin.sms.services.GenerationResultsOfCommands
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
            logger.info { messageAboutCancelCompetition() }
            return
        }
        val startLists: List<CompetitorsGroup> = GenerationResultsOfCommands.startProtocolsGeneration(data)
        dir.mkdir()
        CsvHandler.generationProtocolsStart(pathProtocolStart, startLists)
    }

    private fun processCommandResultsGroup(command: CommandResultsGroup) {
        if (command.pathProtocolStart.isNullOrEmpty()) {
            checkExistDir()
        } else if (!dir.exists()) {
            dir.mkdir()
        }
        val dataProtocolStart: Map<Int, Competitor> = if (command.pathProtocolStart.isNullOrEmpty()) {
            CsvHandler.parseProtocolStart(pathProtocolStart)
        } else {
            CsvHandler.parseProtocolStart(command.pathProtocolStart)
        }
        val dataCheckpoint = if (command.pathProtocolCheckpoint.isNullOrEmpty()) {
            processStream(command.isCheckpointAthlete, dataProtocolStart)
        } else {
            CsvHandler.parseCheckpoints(command.pathProtocolCheckpoint, command.isCheckpointAthlete, dataProtocolStart)
        }
        dataCheckpoint.forEach { it.checkCheckpoints() }
        CsvHandler.generationResultsGroup(pathResultsGroup, GenerationResultsOfCommands.generateResults(dataCheckpoint))
        CsvHandler.generationSplitResults(
            pathSplitResults,
            GenerationResultsOfCommands.generateSplitResults(dataCheckpoint)
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
            GenerationResultsOfCommands.teamResultsGeneration(dataResultsGroup)
        )
    }

    private fun processStream(
        isCheckpointAthlete: Boolean,
        dataProtocolStart: Map<Int, Competitor>
    ): List<Competitor> {          // По ТЗ - не требуется, реализован шаблон на будущее
        var line =
            readLine()
        var splits: List<String>
        if (isCheckpointAthlete) {
            TODO("Реализация по участнику")
        } else {
            var isWait = true
            var athlete: Competitor? = null
            var numberAthlete: Int? = null
            var checkpoints = mutableListOf<CheckpointTime>()
            while (line != null) {  // TODO(Добавить эксепшен)
                line = line.trim()
                if (isWait) {
                    if (numberAthlete != null && athlete != null) {
                        dataProtocolStart[numberAthlete]!!.checkpoints = checkpoints
                        checkpoints = mutableListOf()
                    }
                    numberAthlete = line.toInt()
                    athlete = dataProtocolStart[numberAthlete]!! // TODO(Exception)
                    isWait = false
                } else {
                    splits = line.split(" ")
                    checkpoints.add(
                        CheckpointTime(
                            splits[0],
                            splits[1].toLocalTime() ?: throw Exception()
                        )
                    )  // TODO(Сделать нормальный)
                }
                line = readLine()
            }
        }
        return dataProtocolStart.values.toList()
    }

    private fun checkExistDir() {
        if (!dir.exists()) {
            throw InvalidFileException(pathDirectory)
        }
    }

}