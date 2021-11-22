package ru.emkn.kotlin.sms.data

enum class ExitCode(val exitCode: Int) {
    SUCCESS(0),
    HELP(1),
    INVALID_DATE(2),
    UNDEFINED_COMMAND(3),
    INVALID_FILE(4),
    INCORRECT_DATA(5),
    INCORRECT_CHECKPOINT(6)
}