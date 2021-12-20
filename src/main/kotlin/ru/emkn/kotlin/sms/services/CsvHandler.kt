package ru.emkn.kotlin.sms.services

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import ru.emkn.kotlin.sms.GROUP_NAMES
import ru.emkn.kotlin.sms.classes.*
import ru.emkn.kotlin.sms.toLocalTime
import ru.emkn.kotlin.sms.LOGGER
import ru.emkn.kotlin.sms.utils.*
import java.io.File
import java.time.Duration
import java.time.LocalTime


object CsvHandler {

    fun parseRequests(paths: List<String>): List<Team> {
        val teams = mutableListOf<Team>()
        var request: Team?
        for (path in paths) {
            request = parseRequest(path)
            if (request == null) {
                LOGGER.info(messageAboutMissTeam(path))
                continue
            }
            teams.add(request)
        }
        return teams
    }

    fun generationProtocolsStart(path: String, data: List<CompetitorsGroup>) {
        csvWriter().open(path) {
            data.forEach { (group, competitors) ->
                writeRow(listOf(group.groupName, "", "", "", "", "", ""))
                competitors.forEach {
                    writeRow(it.listForProtocolStart)
                }
            }
        }
    }

    fun parseProtocolStart(path: String): List<Competitor> {
        val file = File(path)
        if (!file.exists()) {
            throw InvalidFileException(path)
        }
        val competitors = mutableListOf<Competitor>()
        try {
            val data = csvReader().readAll(file)
            var group = Group(data[0][0])
            var unit: List<String>
            for (i in 1 until data.size) {
                unit = data[i]
                if (GROUP_NAMES.contains(unit[0])) {
                    group = Group(unit[0])
                } else {
                    val number = unit[0].toInt()
                    val surname = unit[1]
                    val name = unit[2]
                    val birthYear = unit[3].toInt()
                    val rank = toRank(unit[4])
                    val teamName = unit[5]
                    val startTime = unit[6].toLocalTime()!!
                    val athlete = Athlete(surname, name, birthYear, group, rank, teamName)
                    val competitor = Competitor(number, startTime, athlete)
                    competitors.add(competitor)
                }
            }
        } catch (exception: Exception) {
            LOGGER.debug { exception.message }
        }
        return competitors
    }

    fun parseCheckpoints(
        path: String,
        isCheckpointAthlete: Boolean,
        dataProtocolStart: List<Competitor>
    ): List<CompetitorData> {
        val file = File(path)
        if (!File(path).exists()) {
            throw InvalidFileException(path)
        }
        val data = csvReader().readAll(file)
        val numberToCompetitors = dataProtocolStart.associateBy { it.athleteNumber }
        return (if (isCheckpointAthlete) {
            parseCheckpointsOfAthlete(data, numberToCompetitors)
        } else {
            parseCheckpointsOfPoints(data, numberToCompetitors)
        }) ?: throw InvalidFileCheckpointException(path)
    }

    fun generationResultsGroup(path: String, data: List<GroupResults>) {
        csvWriter().open(path) {
            data.forEach { (group, competitorResults) ->
                writeRow(listOf(group.groupName, "", "", "", "", "", "", "", "", ""))
                competitorResults.forEach { competitorResultInGroup ->
                    writeRow(competitorResultInGroup.listForResultsGroup)
                }
            }
        }
    }

    fun generationSplitResults(path: String, data: List<GroupSplitResults>) {
        val maxDistance = data.maxOf { groupSplitsResult ->
            groupSplitsResult.results.maxOf { competitorSplit -> competitorSplit.splits?.size ?: 0 }
        }
        csvWriter().open(path) {
            data.forEach { groupSplitResults ->
                val group = groupSplitResults.group
                val splitResults = groupSplitResults.results
                val title = mutableListOf(group.groupName, "", "", "", "", "", "", "", "")
                title.addAll(List(2 * maxDistance) { "" })
                writeRow(title)
                splitResults.forEach {
                    val result = it.listForSplitsResultsGroup
                    result.addAll(List(2 * (maxDistance - (it.splits?.size ?: 0))) { "" })
                    writeRow(result)
                }
            }
        }
    }

    fun parseResultsGroup(path: String): List<GroupResults> {
        val file = File(path)
        if (!File(path).exists()) {
            throw InvalidFileException(path)
        }
        val linesFromResultsCsv: List<List<String>> = csvReader().readAll(file)
        var listOfAthletes: MutableList<CompetitorResultInGroup> = mutableListOf()
        val listOfGroups: MutableList<GroupResults> = mutableListOf()

        var groupName = ""
        var unit: List<String>
        try {
            for (element in linesFromResultsCsv) {
                unit = element
                if (GROUP_NAMES.contains(unit[0])) {
                    if (listOfAthletes.isNotEmpty()) {
                        // = we've already written down the first group of Athletes
                        listOfGroups.add(GroupResults(Group(groupName), listOfAthletes))
                        listOfAthletes = mutableListOf()
                    }
                    groupName = unit[0]
                } else if (unit[0].toIntOrNull() != null) {
                    // checks if unit[i] doesn't equal to
                    // "@№ п/п,Номер,Фамилия,Имя,Г.р.,Разр.,Команда,Результат,Место,Отставание"
                    val place = unit[0].toIntOrNull()
                    val athleteNumber = unit[1].toInt()
                    val surname = unit[2]
                    val name = unit[3]
                    val birthYear = unit[4].toInt()
                    val rank = toRank(unit[5])
                    val teamName = unit[6]
                    LOGGER.debug { unit[7] }
                    val result = if (unit[7] != "") {
                        Duration.between(LocalTime.MIN, unit[7].toLocalTime())
                    } else {
                        null
                    }
                    val backlog = if (unit[9] != "") {
                        Duration.between(LocalTime.MIN, unit[7].trim('+').toLocalTime())
                    } else {
                        null
                    }
                     val athlete = Athlete(surname, name, birthYear, Group(groupName), rank, teamName)
                     val competitor = Competitor(athleteNumber, LocalTime.of(0, 0), athlete)
                    // TODO: we know it's a competitor, but we don't know his start time,
                    //  and we don't need it for scores
                    listOfAthletes.add(CompetitorResultInGroup(competitor, result, place, backlog))
                }
            }
        } catch (exception: Exception) {
            LOGGER.debug { exception.message }
            throw IncorrectResultsGroupException(path)
        }

        // writing down the last group of Athletes
        listOfGroups.add(GroupResults(Group(groupName), listOfAthletes))
        return listOfGroups
    }

    fun generationResultsTeam(path: String, data: List<TeamResults>) {
        csvWriter().open(path) {
            data.forEach { (teamName, teamScore, teamData) ->
                writeRow(listOf(teamName, teamScore, "", "", "", "", "", ""))
                teamData.forEach { writeRow(it.listForResultsAthlete) }
            }
        }
    }

    private fun parseRequest(path: String): Team? {
        val file = File(path)
        if (!file.exists()) {
            return null
        }
        val athletes = mutableListOf<Athlete>()
        val data = csvReader().readAll(file)
        if (data.size < 2 || data[0].isEmpty()) {
            return null
        }
        val teamName = data[0][0]
        var unit: List<String>
        for (i in 1 until data.size) {
            unit = data[i]
            try {
                athletes.add(
                    Athlete(
                        unit[1],
                        unit[2],
                        unit[3].toIntOrNull() ?: throw IncorrectBirthYearException(unit[3]),
                        Group(unit[0]),
                        toRank(unit[4]),
                        teamName
                    )
                )
            } catch (exception: Exception) {
                LOGGER.info { messageAboutMissAthleteRequest(teamName, unit.joinToString(" ")) }
                if (exception is ExceptionData) {
                    LOGGER.info { exception.message }
                }
            }
        }
        if (athletes.isEmpty()) {
            return null
        }
        return Team(teamName, athletes)
    }

    private fun toRank(rank: String) = Rank(rank.ifBlank { null })


    private fun parseCheckpointsOfAthlete(
        data: List<List<String>>,
        dataProtocolStart: Map<Int, Competitor>
    ): List<CompetitorData>? {
        TODO()
    }

    private fun parseCheckpointsOfPoints(
        data: List<List<String>>,
        dataProtocolStart: Map<Int, Competitor>
    ): List<CompetitorData>? {
        println(dataProtocolStart)
        if (data.size < 2 || data[0].isEmpty()) {
            return null
        }
        var checkpoint = data[0][0]
        var unit: List<String>
        var numberAthlete: Int
        var competitor: Competitor
        val competitorsData = dataProtocolStart.values.associateWith { mutableListOf<CheckpointTime>() }
        for (i in 1 until data.size) {
            unit = data[i]
            try {
                if (unit[0].isNotBlank() && unit[1].isBlank()) {
                    checkpoint = unit[0]
                } else {
                    numberAthlete = unit[0].toIntOrNull() ?: throw IncorrectNumberAthleteException(unit[0])
                    competitor = dataProtocolStart[numberAthlete]
                        ?: throw IllegalStateException("athlete number is not in start protocol")
                    competitorsData[competitor]!!.add(
                        CheckpointTime(
                            checkpoint, unit[1].toLocalTime() ?: throw InvalidTimeException(unit[1])
                        )
                    )
                }
            } catch (exception: Exception) {
                LOGGER.info { messageAboutMissAthleteCheckpointData(checkpoint, unit.joinToString(" ")) }
                if (exception is ExceptionData) {
                    LOGGER.info { exception.message }
                } else {
                    return null
                }
            }
        }
        return competitorsData.map { CompetitorData(it.key, it.value, false) }
    }
}