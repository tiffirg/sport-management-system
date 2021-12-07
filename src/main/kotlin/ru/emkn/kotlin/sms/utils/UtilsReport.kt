package ru.emkn.kotlin.sms.utils

import ru.emkn.kotlin.sms.classes.Competitor

// Config
class InvalidConfigException(pathConfig: String): Exception("$pathConfig: No such config")

class InvalidFormatConfigException(pathConfig: String): Exception("$pathConfig: Invalid config format")

// Commands
class MissCommandException : Exception("Command is missing, use `protocolStart`, `resultsAthlete`, `resultsTeam`")

// Data
class InvalidFileException(path: String) : Exception("$path: No such file or directory")

class IncorrectProtocolStartException(pathProtocolStart: String) : Exception("$pathProtocolStart: Invalid start protocol format")

class IncorrectResultsGroupException(pathResultsGroup: String) : Exception("$pathResultsGroup: Invalid results group format")

class InvalidFileCheckpointException(fileCheckpoint: String) : Exception("$fileCheckpoint: Invalid checkpoint data")

// Check data
interface ExceptionData

class IncorrectGroupException(group: String) : Exception("$group: Incorrect group"), ExceptionData

class IncorrectRankException(rank: String) : Exception("$rank: Incorrect rank"), ExceptionData

class IncorrectBirthYearException(birthYear: String) : Exception("$birthYear: Not integer"), ExceptionData

class IncorrectNumberAthleteException(numberAthlete: String) : Exception("$numberAthlete: Not integer"), ExceptionData

class InvalidDateException(date: String) : Exception("$date: Invalid date format"), ExceptionData

class InvalidTimeException(time: String) : Exception("$time: Invalid time format"), ExceptionData


fun messageAboutMissTeam(path: String) = "$path: No such request file or incorrect data format"

fun messageAboutMissAthleteRequest(team: String, request: String) = "'$team' $request: Not allowed to compete"

fun messageAboutCancelCompetition() = "Cancellation of the competition"

fun messageAboutMissAthleteCheckpointData(checkpoint: String, unitData: String) = "`$checkpoint` $unitData: Incorrect checkpoint format"

fun messageAboutIncorrectDataCheckpointOfAthlete(athlete: Competitor) = "Incorrect data on the checkpoint of the $athlete"