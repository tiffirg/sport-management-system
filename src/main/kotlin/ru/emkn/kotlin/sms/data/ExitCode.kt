package ru.emkn.kotlin.sms.data

enum class ExitCode(val exitCode: Int) {
    SUCCESS(0),
    HELP(1),
    INVALID_DATE(2),
    UNDEFINED_COMMAND(3),
    UNDEFINED_FILE(4),
    EMPTY_DATA(5),
    READ_ERROR(6),
    INCORRECT_DATA(7),
    INCORRECT_CHECKPOINT(8)
}