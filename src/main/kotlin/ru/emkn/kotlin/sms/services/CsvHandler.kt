package ru.emkn.kotlin.sms.services

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import ru.emkn.kotlin.sms.data.*
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

    fun generationProtocolsStart(data: List<AthletesGroup>) {
        TODO()
    }

    fun parseProtocolsStart(paths: List<String>): List<AthletesGroup> {
        val protocols = mutableListOf<AthletesGroup>()
        for (path in paths) {
            protocols.add(parseProtocolStart(path) ?: continue)
        }
        return protocols
    }

    fun parseCheckpoints(paths: List<String>) {
        TODO()
    }

    fun generationResultsAthlete() {
        TODO()
    }

    fun parseResultsAthlete(paths: List<String>) {
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
        val result = mutableListOf<Athlete>()
        val data = csvReader().readAll(file)
        val teamName = data[0][0]
        var unit: List<String>
        for (i in 2 until data.size) {
            unit = data[i]
            try {
                result.add(
                    Athlete(
                        unit[1],
                        unit[2],
                        unit[3].toInt(),
                        Group(unit[0]),
                        Rank(unit[4]),
                        teamName, null, null
                    )
                )
            } catch (e: Exception) {
                printMessageAboutMissAthleteRequest(unit.joinToString(" "), teamName)
            }
        }
        if (result.isEmpty()) {
            return null
        }
        return Team(teamName, result)
    }

    private fun parseProtocolStart(path: String): AthletesGroup? {
        val file = File(path)
        if (!File(path).exists()) {
            return null
        }
        val result = mutableListOf<Athlete>()
        val data = csvReader().readAll(file)
        val groupName = Group(data[0][0])
        var unit: List<String>
        for (i in 1 until data.size) {
            unit = data[i]
            result.add(
                Athlete(
                    unit[1],
                    unit[2],
                    unit[3].toInt(),
                    groupName,
                    Rank(unit[4]),
                    unit[5],
                    unit[0].toInt(),
                    LocalDateTime.parse(unit[6])
                )
            )
        }
        if (result.isEmpty()) {
            return null
        }
        return AthletesGroup(groupName, result)
    }
}