package ru.emkn.kotlin.sms.utils

// Config
class InvalidConfigException(pathConfig: String): Exception("$pathConfig: No such config")

class InvalidFormatConfigException(pathConfig: String): Exception("$pathConfig: Invalid config format")

// Commands
class MissCommandException : Exception("Command is missing, use `protocolStart`, `resultsAthlete`, `resultsTeam`")

// Data
class InvalidFileException(path: String) : Exception("$path: No such file or directory")

class IncorrectProtocolStartException(pathProtocolStart: String) : Exception("$pathProtocolStart: Invalid start protocol format")

class IncorrectResultsGroupException(pathResultsGroup: String) : Exception("$pathResultsGroup: Invalid results group format")

class IncorrectDataException(file: String) : Exception("$file: Invalid data format")

class InvalidFileCheckpointException(fileCheckpoint: String) : Exception("$fileCheckpoint: Invalid checkpoint data")

// Check data
interface ExceptionData

class IncorrectGroupException(group: String) : Exception("$group: Incorrect group"), ExceptionData

class IncorrectRankException(rank: String) : Exception("$rank: Incorrect rank"), ExceptionData

class IncorrectBirthYearException(birthYear: String) : Exception("$birthYear: Not integer"), ExceptionData

class IncorrectNumberAthleteException(numberAthlete: String) : Exception("$numberAthlete: Not integer"), ExceptionData

class InvalidDateException(date: String) : Exception("$date: Invalid date format"), ExceptionData

class InvalidTimeException(time: String) : Exception("$time: Invalid time format"), ExceptionData




fun printMessageAboutMissTeam(nameFile: String) = println("$nameFile: No such request file or incorrect data format")

fun printMessageAboutMissAthleteRequest(request: String, team: String) =
    println("'$team' $request: Not allowed to compete")

fun printMessageAboutCancelCompetition() = println("Cancellation of the competition ")

fun printMessageAboutMissAthleteCheckpointData(unitData: String, checkpoint: String) = println("`$checkpoint` $unitData: Incorrect checkpoint format ")