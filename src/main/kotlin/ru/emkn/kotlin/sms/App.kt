package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.data.*
import ru.emkn.kotlin.sms.services.CsvHandler
import ru.emkn.kotlin.sms.utils.InvalidFileException
import ru.emkn.kotlin.sms.utils.printMessageAboutCancelCompetition
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

class App(val title: String, val date: LocalDate) {
    private val pathDirectory = File(PATH_CONFIG).resolveSibling("${title}_$date").path
    private val pathProtocolStart = File(pathDirectory).resolve("ps_${title}_$date.csv").path
    private val dir = File(pathDirectory)

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
            printMessageAboutCancelCompetition()
            return
        }
        // генерирование стартовых списков
        val startLists: List<AthletesGroup> = startProtocolsGeneration(data)
        // записывание данных в csv
        CsvHandler.generationProtocolsStart(pathProtocolStart, startLists)
        dir.mkdir()
    }

    private fun processCommandResultsGroup(command: CommandResultsGroup) {
        if (command.pathProtocolCheckpoint.isNullOrEmpty() || command.pathProtocolStart.isNullOrEmpty()) {
            checkExistDir()
        }
        val dataProtocolStart: Map<Int, Athlete> = if (command.pathProtocolStart.isNullOrEmpty()) {
            CsvHandler.parseProtocolStart(pathProtocolStart)
        } else {
            CsvHandler.parseProtocolStart(command.pathProtocolStart)
        }

        val dataCheckpoint = if (command.pathProtocolCheckpoint.isNullOrEmpty()) {
            processStream(command.isCheckpointAthlete, dataProtocolStart)
        } else {
            CsvHandler.parseCheckpoints(command.pathProtocolCheckpoint, command.isCheckpointAthlete, dataProtocolStart)
        }

    }

    private fun processCommandResultsTeam(command: CommandResults): List<String> {
        TODO()
    }

    private fun processStream(isCheckpointAthlete: Boolean, dataProtocolStart: Map<Int, Athlete>): List<Athlete> {
        var line = readLine()
        var splits: List<String>
        if (isCheckpointAthlete) {
            TODO("Реализация по участнику")
        }
        else {
            var isWait = true
            var athlete: Athlete? = null
            var numberAthlete: Int? = null
            val checkpoints = mutableListOf<CheckpointTime>()
            while (line != null) {  // TODO(Добавить эксепшен)
                line = line.trim()
                if (isWait) {
                    if (numberAthlete != null && athlete != null) {
                        dataProtocolStart[numberAthlete]!!.checkpoints = checkpoints
                        checkpoints.clear()
                    }
                    numberAthlete = line.toInt()
                    athlete = dataProtocolStart[numberAthlete]!! // TODO(Exception)
                    isWait = false
                }
                else {
                    splits = line.split(" ")
                    checkpoints.add(CheckpointTime(splits[0], LocalDateTime.parse(splits[1])))
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