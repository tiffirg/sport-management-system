package ru.emkn.kotlin.sms.classes

import ru.emkn.kotlin.sms.REMOVED_VALUE
import java.time.LocalTime


data class ResultsGroup(val group: Group, val results: List<ResultAthleteInGroup>) {

    private val leaderTime: LocalTime?
        get() = this.results[0].result

    fun getAthleteScore(athleteNumber: Int): Int {
        val athleteTime = this.results.find { resultAthleteGroup ->
            resultAthleteGroup.athleteNumber == athleteNumber
        }?.result
        return if (athleteTime == null || leaderTime == null) {
            0
        } else {
            val x = athleteTime.toSecondOfDay().toDouble()
            val y = leaderTime!!.toSecondOfDay().toDouble()
            0.coerceAtLeast((100 * (2 - x / y)).toInt())
        }
    }
}

data class SplitResultAthleteGroup(
    val athleteNumberInGroup: Int, val athleteNumber: Int,
    val surname: String, val name: String, val birthYear: Int,
    val rank: Rank, val teamName: String, val splits: List<CheckpointTime>?,
    val place: Int, val backlog: String
) {
    val listForSplitsResultsGroup: MutableList<String>
        get() {
            val result = mutableListOf(
                athleteNumberInGroup.toString(),
                athleteNumber.toString(),
                surname,
                name,
                birthYear.toString(),
                rank.rankName ?: "",
                teamName,
                place.toString(),
                if (splits.isNullOrEmpty()) REMOVED_VALUE else backlog
            )
            splits?.forEach {
                result.addAll(listOf(it.checkpoint, it.time.toString()))
            }
            return result
        }
}

data class SplitResultsGroup(val group: Group, val results: List<SplitResultAthleteGroup>)