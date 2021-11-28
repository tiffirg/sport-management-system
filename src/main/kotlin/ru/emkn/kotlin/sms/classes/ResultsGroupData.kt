package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.REMOVED_VALUE
import ru.emkn.kotlin.sms.TimeFormatter
import java.time.LocalTime

data class ResultAthleteInGroup(
    val athleteNumberInGroup: Int, val athleteNumber: Int,
    val surname: String, val name: String, val birthYear: Int,
    val rank: Rank, val teamName: String, val result: LocalTime?,
    val place: Int, var backlog: String
) {

    val listForResultsGroup: List<String>
        get() {
            return listOf(
                athleteNumberInGroup.toString(),
                athleteNumber.toString(),
                surname,
                name,
                birthYear.toString(),
                rank.rankName ?: "",
                teamName,
                result?.format(TimeFormatter) ?: REMOVED_VALUE,
                place.toString(),
                backlog
            )
        }
}