package ru.emkn.kotlin.sms.classes
import java.time.format.DateTimeFormatter

val TimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
val DateFormat : DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")


/*
usage example:
val sampleDateTime = LocalDateTime.of(LocalDate.parse("2003-09-22"), LocalTime.parse("12:01:18"))
println(sampleDateTime)    // 2003-09-22T12:01:18
println(sampleDateTime.format(TimeFormatter))   // 12:01:18
println(sampleDateTime.format(DateFormat))  // 22.09.2003
 */
