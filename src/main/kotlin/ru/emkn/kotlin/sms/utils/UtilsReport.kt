package ru.emkn.kotlin.sms.utils

import ru.emkn.kotlin.sms.data.ExitCode.INVALID_FILE
import ru.emkn.kotlin.sms.data.ExitCode.INVALID_DATE
import ru.emkn.kotlin.sms.data.ExitCode.UNDEFINED_COMMAND
import ru.emkn.kotlin.sms.data.ExitCode.INCORRECT_DATA
import ru.emkn.kotlin.sms.data.ExitCode.INCORRECT_CHECKPOINT

abstract class ExceptionWithExitCode(message: String) : Exception(message) {
    abstract val exitCode: Int
}

class InvalidFileException(file: String) : ExceptionWithExitCode("$file: No such file or directory") {
    override val exitCode = INVALID_FILE.exitCode
}


class InvalidDateException(date: String) : ExceptionWithExitCode("$date: Invalid date format") {
    override val exitCode = INVALID_DATE.exitCode
}


class UndefinedCommandException : ExceptionWithExitCode("Command is missing, use `protocolStart`, `resultsAthlete`, `resultsTeam`") {
    override val exitCode = UNDEFINED_COMMAND.exitCode
}


class IncorrectDateException(file: String) : ExceptionWithExitCode("$file: Invalid data format") {
    override val exitCode = INCORRECT_DATA.exitCode
}

class IncorrectCheckpointException(file: String) : ExceptionWithExitCode("$file: Invalid checkpoint date") {
    override val exitCode = INCORRECT_CHECKPOINT.exitCode
}

