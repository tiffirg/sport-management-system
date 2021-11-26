package ru.emkn.kotlin.sms.services

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import ru.emkn.kotlin.sms.DISTANCE_CRITERIA
import ru.emkn.kotlin.sms.GROUP_NAMES
import ru.emkn.kotlin.sms.classes.*
import ru.emkn.kotlin.sms.utils.InvalidFileException
import ru.emkn.kotlin.sms.utils.printMessageAboutMissAthleteRequest
import ru.emkn.kotlin.sms.utils.printMessageAboutMissTeam
import java.io.File
import java.time.LocalDateTime
import kotlin.String as String


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

    fun parseProtocolStart(path: String): Map<Int, Athlete> {
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

    fun parseCheckpoints(
        path: String,
        isCheckpointAthlete: Boolean,
        dataProtocolStart: Map<Int, Athlete>
    ): List<Athlete> { // TODO(Добавить эксепшен)
        val file = File(path)
        if (!File(path).exists()) {
            throw InvalidFileException(path)
        }
        dataProtocolStart.forEach { it.value.checkpoints = mutableListOf() }
        if (isCheckpointAthlete) {
            TODO("Реализация по участнику")
        } else {
            val data = csvReader().readAll(file)
            var checkpoint = data[0][0]
            var unit: List<String>
            var numberAthlete: Int
            for (i in 1 until data.size) {
                unit = data[i]
                if (DISTANCE_CRITERIA.contains(unit[0])) {  // TODO("SAD!. Добавить эксепшен")
                    checkpoint = unit[0]
                } else {
                    numberAthlete = unit[0].toInt()  // TODO(Exception)
                    dataProtocolStart[numberAthlete]!!.checkpoints!!.add(
                        CheckpointTime(
                            checkpoint,
                            LocalDateTime.parse(unit[1])
                        )
                    )  // TODO(Exception)
                }
            }
        }
        return dataProtocolStart.values.toList()
    }

    fun generationResultsGroup() {
        TODO()
    }

    // Block of funcs for fun parseResultsGroup()
    fun toRankOrNull(string: String): Rank?{
        return  when(string){
            "" -> null
            else -> Rank(string)
        }
    }

    fun toLocalDateTimeOrNull(string: String): LocalDateTime?{
        return  when(string){
            "снят." -> null
            else -> null    // !!!HERE SHOULD BE String -> LocalDateTime. Not null
        }
    }

    fun stringToIntOrNull(string: String): Int?{
        return  when(string){
            "" -> null
            else -> string.toInt()
        }
    }

    fun parseResultsGroup(path: String): MutableList<AthleteResults> {
        val file = File(path)
        if (!File(path).exists()) {
            throw InvalidFileException(path)
        }
        val linesFromResultsCsv: List<List<String>> = csvReader().readAll(file)
        var listOfAthletes: MutableList<MedalTable> = mutableListOf()
        val listOfGroups: MutableList<AthleteResults> = mutableListOf()

        var group = ""
        var unit: List<String>
        for(i in 1 until linesFromResultsCsv.size) {
            unit = linesFromResultsCsv[i]
            if(GROUP_NAMES.contains(unit[0])) {
                if (listOfAthletes.isNotEmpty()) {      // = we've already written down the first group of Athletes
                    listOfGroups.add(AthleteResults(Group(group), listOfAthletes))
                    listOfAthletes = mutableListOf()    // .clear() causes troubles
                }
                group = unit[0]
            }
            else if (unit[0].toIntOrNull() != null){   // actually, checks if unit[i] doesn't equal to "@№ п/п,Номер,Фамилия,Имя,Г.р.,Разр.,Команда,Результат,Место,Отставание"
                listOfAthletes.add(MedalTable(  unit[0].toInt(), unit[1].toInt(),
                    unit[2], unit[3], unit[4].toInt(),
                    toRankOrNull(unit[5]),  unit[6], toLocalDateTimeOrNull(unit[7]),
                    stringToIntOrNull(unit[8]), unit[9]  ))
            }
        }
        listOfGroups.add(AthleteResults(Group(group), listOfAthletes))      // writing down the last group of Athletes
        return listOfGroups
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