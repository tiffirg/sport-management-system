package ru.emkn.kotlin.sms.services

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


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

abstract class TableWithCompetitionId(name: String) : Table(name) {
    val competitionId =
        reference("competitionId", Competitions, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

object Athletes : IntIdTableWithCompetitionId("athletes") {
    private const val surnameLength = 32
    private const val nameLength = 32
    val name = varchar("name", nameLength)
    val surname = varchar("surname", surnameLength)
    val birthYear = integer("birthYear")
    val groupId = reference("groupId", Groups, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val rankId = reference("rankId", Ranks, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val teamId = reference("teamId", Teams, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

object Teams : IntIdTableWithCompetitionId("team") {
    private const val teamLength = 65
    val team = varchar("team", teamLength)
}

object Groups : IntIdTableWithCompetitionId("group") {
    private const val groupLength = 65
    val group = varchar("group", groupLength)
    val distanceId =
        reference("distanceId", Distances, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

object Ranks : IntIdTableWithCompetitionId("rank") {
    private const val rankLength = 32
    val rank = varchar("rank", rankLength)
}

object Checkpoints : IntIdTableWithCompetitionId("checkpoints") {
    private const val checkpointLength = 16
    val checkpoint = varchar("checkpoint", checkpointLength)
}

object DistancesToCheckpoints : Table("distancesToCheckpoints") {
    private val distanceId =
        reference("distanceId", Distances, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    private val checkpointId =
        reference("checkpointId", Checkpoints, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(distanceId, checkpointId)
}

object Distances : IntIdTableWithCompetitionId("distances") {
    private const val distanceLength = 64
    val distance = varchar("distance", distanceLength)
    val isFixed = bool("isFixed")
    val amountCheckpoints = integer("amountCheckpoints")
}

object Competitors : IntIdTable("competitors") {
    private const val startTimeLength = 8
    val competitorNumber = integer("competitorNumber")
    val athleteId =
        reference("athleteId", Athletes, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val startTime = varchar("startTime", startTimeLength)
}

object CompetitorsData : IntIdTable("competitorsData") {
    val competitorId = reference(
        "competitorId",
        Competitors,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val isRemoved = bool("isRemoved").default(false)
}

object CheckpointsProtocolsToCompetitorsData : Table("CheckpointsProtocolsToCompetitorsData") {
    private val checkpointProtocolId = reference(
        "checkpointProtocolId",
        CheckpointsProtocols,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    private val competitorDataId = reference(
        "competitorDataId",
        CompetitorsData,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    override val primaryKey = PrimaryKey(checkpointProtocolId, competitorDataId)
}

object CheckpointsProtocols : IntIdTable("checkpointsProtocols") {
    private const val timeMeasurementLength = 8
    val competitorId = reference(
        "competitorId",
        Competitors,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val checkpointId =
        reference("checkpointId", Checkpoints, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val timeMeasurement = varchar("timeMeasurement", timeMeasurementLength)
}

object ResultsGroup : IntIdTable("resultsGroup") {
    private const val resultLength = 8
    private const val backlogLength = 9
    val competitorId = reference(
        "competitorId",
        Competitors,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val result = varchar("result", resultLength)
    val backlog = varchar("backlog", backlogLength)
    val place = integer("place")
}

object DurationAtCheckpointsToResultsGroupSplit : IntIdTable("durationAtCheckpointsToResultsGroupSplit") {
    private const val timeMeasurementAtCheckpointLength = 8
    val checkpoint = reference("checkpointId", Checkpoints, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val durationAtCheckpoint = varchar("timeMeasurementAtCheckpoint", timeMeasurementAtCheckpointLength)
    val splitsResultGroupId =
        reference(
            "splitsResultGroupId",
            SplitsResultsGroup,
            onDelete = ReferenceOption.CASCADE,
            onUpdate = ReferenceOption.CASCADE
        )
}

object SplitsResultsGroup : IntIdTable("splitsResultsGroup") {
    val resultsGroupId = reference(
        "resultsGroupId",
        ResultsGroup,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
}

object ResultsTeam : IntIdTable("resultsGroup") {
    val competitorId = reference(
        "competitorId",
        Competitors,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val place = integer("place")
    val score = integer("score")
}
