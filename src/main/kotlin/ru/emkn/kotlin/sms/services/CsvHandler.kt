package ru.emkn.kotlin.sms.services

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import ru.emkn.kotlin.sms.GROUP_NAMES
import ru.emkn.kotlin.sms.data.*
import ru.emkn.kotlin.sms.utils.InvalidFileException
import ru.emkn.kotlin.sms.utils.printMessageAboutMissAthleteRequest
import ru.emkn.kotlin.sms.utils.printMessageAboutMissTeam
import java.io.File
import java.time.LocalDateTime


object CsvHandler {
    fun parseRequests(paths: List<String>): List<Team> {
        val teams = mutableListOf<Team>()
        for (path in paths) {
            teams.add(parseRequest(path) ?: continue)
        }
        return teams
    }

    fun generationProtocolsStart(path: String, data: List<AthletesGroup>) {
        csvWriter().open(path) {
            data.forEach { (group, athletes) ->
                writeRow(group.groupName)
                athletes.forEach {
                    writeRow(it.listForProtocolStart)
                }
            }
        }
    }

    private fun parseProtocolStart(path: String): Map<Int, Athlete> {
        val file = File(path)
        if (!File(path).exists()) {
            throw InvalidFileException(path)
        }
        val athletes = mutableMapOf<Int, Athlete>()
        val data = csvReader().readAll(file)
        var group = Group(data[0][0])
        var unit: List<String>
        var number: Int
        for (i in 1 until data.size) {
            unit = data[i]
            if (GROUP_NAMES.contains(unit[0])) {  // TODO("Сделать функцию у Group. Добавить эксепшен")
                group = Group(unit[0])
            } else {
                number = unit[0].toInt()
                athletes[number] = Athlete(
                    unit[1],
                    unit[2],
                    unit[3].toInt(),
                    group,
                    Rank(unit[4]),
                    unit[5],
                    athleteNumber = number,
                    startTime = LocalDateTime.parse(unit[6])
                )
            }
        }
        return athletes
    }

    fun parseCheckpoints(path: String, isCheckpointAthlete: Boolean): List<Athlete> {
        val file = File(path)
        if (!File(path).exists()) {
            throw InvalidFileException(path)
        }
        val athletes = mutableListOf<Athlete>()
        if (isCheckpointAthlete) {
            TODO()
        } else {
            TODO()
        }

    }

    fun generationResultsGroup() {
        TODO()
    }

    fun parseResultsGroup(paths: List<String>) {
        TODO()
    }

    fun generationResultsTeam() {
        TODO()
    }

    private fun parseRequest(path: String): Team? {
        val file = File(path)
        if (!File(path).exists()) {
            printMessageAboutMissTeam(file.name)
            return null
        }
        val athletes = mutableListOf<Athlete>()
        val data = csvReader().readAll(file)
        val teamName = data[0][0]
        var unit: List<String>
        for (i in 2 until data.size) {
            unit = data[i]
            try {
                athletes.add(
                    Athlete(
                        unit[1],
                        unit[2],
                        unit[3].toInt(),
                        Group(unit[0]),
                        Rank(unit[4]),
                        teamName,
                        checkpoints = null,
                        athleteNumber = null,
                        startTime = null
                    )
                )
            } catch (e: Exception) {
                printMessageAboutMissAthleteRequest(unit.joinToString(" "), teamName)
            }
        }
        if (athletes.isEmpty()) {
            return null
        }
        return Team(teamName, athletes)
    }
}