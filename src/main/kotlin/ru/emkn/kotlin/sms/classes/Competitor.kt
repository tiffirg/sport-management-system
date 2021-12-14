package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.DISTANCE_CRITERIA
import ru.emkn.kotlin.sms.TimeFormatter
import ru.emkn.kotlin.sms.logger
import ru.emkn.kotlin.sms.utils.messageAboutIncorrectDataCheckpointOfAthlete
import java.time.Duration
import java.time.LocalTime


open class Person(
    val surname: String,
    val name: String,
    val birthYear: Int
)


open class Athlete(
    surname: String, name: String, birthYear: Int,
    open val group: Group, open val rank: Rank, open val teamName: String
) : Person(surname, name, birthYear) {
    override fun toString(): String {
        return "$surname $name Team: $teamName Group: ${group.groupName}"
    }

    constructor(athlete: Athlete) : this(
        athlete.surname, athlete.name, athlete.birthYear,
        athlete.group, athlete.rank, athlete.teamName
    )
}


data class Competitor(
    val athleteNumber: Int,
    val startTime: LocalTime,
    val athlete: Athlete
) : Athlete(athlete) {

    //Пример: 21, Санников, Вадим, 2003, 3р, СПбГУ, 12:02:00
    val listForProtocolStart: List<String>
        get() = listOf(
            athleteNumber.toString(),
            surname,
            name,
            birthYear.toString(),
            rank.toString(),
            teamName,
            startTime.format(TimeFormatter)
        )
}


open class CompetitorData(
    val competitor: Competitor,
    val checkpoints: List<CheckpointTime>,
    val removed: Boolean
) {
    val orderedCheckpoints = checkpoints.sortedBy { it.time }
}


data class CompetitorResultInGroup(
    val competitor: Competitor, val athleteNumberInGroup: Int,
    val result: Duration?, val place: Int?, var backlog: Duration?
) {
    // Пример: 1, 22, Ананикян, Александр, 2002, 2р, СПбГУ, 00:08:11, 1, +00:00:00
    val listForResultsGroup: List<String>
        get() = listOf(
            place.toString(),
            competitor.athleteNumber.toString(),
            competitor.surname,
            competitor.name,
            competitor.birthYear.toString(),
            competitor.rank.toString(),
            competitor.teamName,
            result.toResultFormat(),
            place.toString(),
            backlog.toBacklogFormat()
        )
}

data class CompetitorResultInTeam(
    val competitor: Competitor, val place: Int?, val score: Int
) {
    // Пример: 21, Шишкин, Владислав, 2002, 1ю, М10, 2, 77
    val listForResultsAthlete: List<String>
    get() = listOf(
        competitor.athleteNumber.toString(),
        competitor.surname,
        competitor.name,
        competitor.birthYear.toString(),
        competitor.rank.toString(),
        competitor.group.groupName,
        place.toString(),
        score.toString()
    )
}

data class CompetitorSplitResultInGroup(
    val competitorResultInGroup: CompetitorResultInGroup, val splits: List<CheckpointDuration>?
) {
    // Пример: 2, 21, Шишкин, Владислав, 2002, 1ю, МГУ, 2, +00:01:50,
    // 32, 00:01:58, 46, 00:03:30, 34, 00:01:11, 33, 00:01:21, 53, 00:02:01
    val listForSplitsResultsGroup: MutableList<String>
        get() {
            val competitor = competitorResultInGroup.competitor
            val res = mutableListOf<String>(
                competitorResultInGroup.place.toString(),
                competitor.athleteNumber.toString(),
                competitor.surname,
                competitor.name,
                competitor.birthYear.toString(),
                competitor.rank.toString(),
                competitor.teamName,
                competitorResultInGroup.place.toString(),
                competitorResultInGroup.backlog.toBacklogFormat()
            )
            val stringSplits = splits?.flatMap { it -> listOf(it.checkpoint, it.duration.toResultFormat()) } ?: listOf()
            res.addAll(stringSplits)
            return res
        }

}
