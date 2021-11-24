package ru.emkn.kotlin.sms.data

import ru.emkn.kotlin.sms.GROUP_NAMES
import ru.emkn.kotlin.sms.RANKS
import java.time.LocalDateTime

data class Group(val groupName: String) {
    init {
        require(GROUP_NAMES.contains(groupName)) { "group name must be mentioned in config file" }
    }
}

data class Rank(val rankName: String) {
    init {
        require(RANKS.contains(rankName)) { "rank name must be mentioned in config file" }
    }
}

data class CheckpointTime(val checkpoint: String, val time: LocalDateTime) {
    init {
        require(true) { "message" }
    }
}

data class Athlete(
    val surname: String, val name: String, val birthYear: Int,
    val group: Group, val rank: Rank, val teamName: String,
    var checkpoints: MutableList<CheckpointTime>? = null, var removed: Boolean = false,
    var athleteNumber: Int? = null, var startTime: LocalDateTime? = null,
) {
    val listForProtocolStart: List<String>
        get() {
            return listOf(
                athleteNumber.toString(),
                surname,
                name,
                birthYear.toString(),
                rank.rankName,
                teamName,
                startTime.toString()
            )
        }
}

data class Team(val teamName: String, val athletes: List<Athlete>)

data class AthletesGroup(val group: Group, val athletes: List<Athlete>)

// for the protocolResults
data class MedalTable(val athleteNumberInGroup: Int, val athleteNumber: Int,
                      val suranme: String, val name: String, val birthYear: Int,
                      val rank: Rank, val teamName: String, val result: LocalDateTime?,
                      val place: Int, val backlog: String? = null)

data class AthleteResults(val group: Group, val resultsPerGroup: List<MedalTable>)