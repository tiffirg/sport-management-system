package ru.emkn.kotlin.sms.utils


class InvalidFileException(file: String) : Exception("$file: No such file or directory")


class InvalidDateException(date: String) : Exception("$date: Invalid date format")


class UndefinedCommandException : Exception("Command is missing, use `protocolStart`, `resultsAthlete`, `resultsTeam`")


class IncorrectDateException(file: String) : Exception("$file: Invalid data format")

class IncorrectCheckpointException(file: String) : Exception("$file: Invalid checkpoint date")

fun printMessageAboutMissTeam(nameFile: String) = println("$nameFile: No such request file or directory")

fun printMessageAboutMissAthleteRequest(request: String, team: String) =
    println("'$team' $request: Not allowed to compete")

fun printMessageAboutCancelCompetition() = println("Cancellation of the competition ")
