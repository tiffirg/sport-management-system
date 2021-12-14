package ru.emkn.kotlin.sms.services

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.skia.impl.interopScope

private const val checkpointsLength = 100


interface DatabaseInterface {
    fun connect()
}

class MySQLDatabase : DatabaseInterface {
    override fun connect() {
        TODO("Not yet implemented")
    }
}

object Competitions : IntIdTable("competitions", "id") {
    private const val dateLength = 16
    private const val timeLength = 8
    private const val sportLength = 64
    private const val eventLength = 256
    val eventName = varchar("event", eventLength).uniqueIndex()
    val sport = varchar("sport", sportLength)
    val date = varchar("date", dateLength)  // Example: 20.02.2022
    val time = varchar("time", timeLength)  // Example: 12:00:00
}

abstract class IntIdTableWithCompetitionId(name: String) : IntIdTable(name, "id") {
    val competitionId =
        reference("competitionId", Competitions, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

abstract class TableWithCompetitionId(name: String) : IntIdTable(name, "id") {
    val competitionId =
        reference("competitionId", Competitions, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

object Athletes : IntIdTableWithCompetitionId("athletes") {
    private const val surnameLength = 32
    private const val nameLength = 32
    val name = varchar("name", nameLength)
    val surname = varchar("surname", surnameLength)
    val birthYear = integer("birthYear")
    val groupId = reference("groupId", Group, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val rankId = reference("rankId", Rank, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val teamId = reference("teamId", Team, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

object Team : IntIdTableWithCompetitionId("team") {
    private const val teamLength = 65
    val team = varchar("team", teamLength).uniqueIndex()
}

object Group : IntIdTableWithCompetitionId("group") {
    private const val groupLength = 65
    val group = varchar("group", groupLength).uniqueIndex()
    val distanceId = reference("distanceId", Distances, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

object Rank : IntIdTableWithCompetitionId("rank") {
    private const val rankLength = 32
    val rank = varchar("rank", rankLength).uniqueIndex()
}

object Distances : IntIdTableWithCompetitionId("distances") {
    private const val distanceLength = 64
    private const val checkpointsInfoLength = 256
    val distance = varchar("distance", distanceLength).uniqueIndex()
    val isFixed = bool("isFixed")
    val amountCheckpoints = integer("amountCheckpoints")
    val checkpointsInfo = varchar("checkpointsInfo", checkpointsInfoLength)
}

object Competitors : TableWithCompetitionId("competitors") {
    private const val startTimeLength = 8
    private val athleteNumber = integer("athleteNumber").uniqueIndex()
    val athleteId = reference("athleteId", Athletes, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val startTime = varchar("startTime", startTimeLength)
    override val primaryKey = PrimaryKey(athleteNumber, name = "athleteNumber")
}

object CompetitorsData : IntIdTableWithCompetitionId("competitors") {
    private const val checkpointsWithSplitsLength = checkpointsLength * 2
    val competitorNumber = reference("competitorNumber", Competitors, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val checkpoints = varchar("checkpoints", checkpointsWithSplitsLength)
    val isRemoved = bool("isRemoved")
}

object CheckpointsProtocols : IntIdTableWithCompetitionId("checkpointsProtocols") {
    private const val timeMeasurementLength = 8
    val groupId = reference("groupId", Group, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val competitorNumber = reference("competitorNumber", Competitors, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val timeMeasurement = varchar("timeMeasurement", timeMeasurementLength)
}

object ResultsGroup : IntIdTableWithCompetitionId("resultsGroup") {
    private const val resultLength = 8
    private const val backlogLength = 9
    val competitorNumber = reference("competitorNumber", Competitors, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val result = varchar("result", resultLength)
    val backlog = varchar("backlog", backlogLength)
    val place = integer("place")
}

object SplitsResultsGroup : IntIdTableWithCompetitionId("splitResultsGroup") {
    private const val timeMeasurementsAtCheckpointsLength = checkpointsLength * 8 + checkpointsLength  // TODO()
    val competitorNumber = reference("competitorNumber", Competitors, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val timeMeasurementsAtCheckpoints = varchar("timeMeasurementsAtCheckpoints", timeMeasurementsAtCheckpointsLength)
}

object ResultsTeam : IntIdTableWithCompetitionId("resultsGroup") {
    val competitorNumber = reference("competitorNumber", Competitors, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val place = integer("place")
    val score = integer("score")
}
