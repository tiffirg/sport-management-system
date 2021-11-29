package ru.emkn.kotlin.sms.classes

data class ResultsTeam(val teamName: String, val teamScore: Int, val data: List<AthleteResultInTeam>)

data class AthleteResultInTeam(
    val startNumber: Int, val name: String, val surname: String,
    val birthYear: Int, val rank: Rank,
    val group: Group, val place: Int, val score: Int
) {
    val listForResultsAthlete: List<String>
        get() {
            return listOf(
                startNumber.toString(),
                surname,
                name,
                birthYear.toString(),
                rank.rankName ?: "",
                group.groupName,
                place.toString(),
                score.toString()
            )
        }
}