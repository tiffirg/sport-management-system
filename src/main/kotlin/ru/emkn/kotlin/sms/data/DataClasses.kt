package ru.emkn.kotlin.sms.data
import ru.emkn.kotlin.sms.GROUP_NAMES
import ru.emkn.kotlin.sms.RANKS
import java.time.LocalDateTime

class Group(groupName: String) {
    init {
        require(GROUP_NAMES.contains(groupName)) { "group name must be mentioned in config file" }
    }
}

class Rank(rankName: String) {
    init {
        require(RANKS.contains(rankName)) { "rank name must be mentioned in config file" }
    }
}

data class Athlete(
    val surname: String, val name: String, val birthYear: Int,
    val group: Group, val rank: Rank,
    val athleteNumber: Int, val startTime: LocalDateTime,
    val teamName: String
)

data class Team(val teamName: String, val athletes: List<Athlete>)

data class AthletesGroup(val group: Group, val athletes: List<Athlete>)

