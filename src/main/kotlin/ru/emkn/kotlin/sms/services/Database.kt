package ru.emkn.kotlin.sms.services

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    val db = GeneralDatabase()
    db.create()
}


interface DatabaseInterface {
    fun create()
}

open class GeneralDatabase : DatabaseInterface {

    val db: Database = Database.connect(url = "jdbc:h2:./database/competitions", driver = "org.h2.Driver")

    init {
        transaction {
            addLogger(StdOutSqlLogger)
        }
    }

    override fun create() {
        SchemaUtils.create(TCompetitions)
    }
}


object TCompetitions : IntIdTable("competitions", "id") {
    private const val dateLength = 16
    private const val timeLength = 8
    private const val sportLength = 64
    private const val eventLength = 256
    val eventName = varchar(name = "event", length = eventLength).uniqueIndex()
    val sport = varchar(name = "sport", length = sportLength)
    val date = varchar(name = "date", length = dateLength)  // Example: 20.02.2022
    val time = varchar(name = "time", length = timeLength)  // Example: 12:00:00
}

class TCompetition(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TCompetition>(TCompetitions)

    val eventName by TCompetitions.eventName
    val sport by TCompetitions.sport
    val date by TCompetitions.date
    val time by TCompetitions.time
}

abstract class IntIdTableWithCompetitionId(name: String) : IntIdTable(name, "id") {
    val competitionId =
        reference(
            name = "competitionId",
            TCompetitions,
            onDelete = ReferenceOption.CASCADE,
            onUpdate = ReferenceOption.CASCADE
        )
}

object TAthletes : IntIdTableWithCompetitionId("athletes") {
    private const val surnameLength = 32
    private const val nameLength = 32
    val name = varchar(name = "name", length = nameLength)
    val surname = varchar(name = "surname", surnameLength)
    val birthYear = integer(name = "birthYear")
    val groupId = reference(
        name = "groupId", foreign = TGroups,
        onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE
    )
    val rankId = reference(
        name = "rankId", foreign = TRanks,
        onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE
    )
    val teamId = reference(
        name = "teamId", foreign = TTeams,
        onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE
    )
}

class TAthlete(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TAthlete>(TAthletes)
    val name by TAthletes.name
    val surname by TAthletes.surname
    val birthYear by TAthletes.birthYear
    val groupId by TAthletes.groupId
    val rankId by TAthletes.rankId
}

object TTeams : IntIdTableWithCompetitionId("team") {
    private const val teamLength = 65
    val team = varchar("team", teamLength)
}

class TTeam(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TTeam>(TTeams)
    val team by TTeams.team
}

object TGroups : IntIdTableWithCompetitionId("group") {
    private const val groupLength = 65
    val group = varchar("group", groupLength)
    val distanceId =
        reference("distanceId", TDistances, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

class TGroup(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TGroup>(TGroups)
    val group by TGroups.group
}

object TRanks : IntIdTableWithCompetitionId("rank") {
    private const val rankLength = 32
    val rank = varchar("rank", rankLength)
}

class TRank(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TRank>(TRanks)
    val rank by TRanks.rank
}

object TCheckpoints : IntIdTableWithCompetitionId("checkpoints") {
    private const val checkpointLength = 16
    val checkpoint = varchar("checkpoint", checkpointLength)
}

class TCheckpoint(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TCheckpoint>(TCheckpoints)
    val checkpoint by TCheckpoints.checkpoint
}

object TDistancesToCheckpoints : Table("distancesToCheckpoints") {
    private val distanceId =
        reference("distanceId", TDistances, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    private val checkpointId =
        reference("checkpointId", TCheckpoints, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(distanceId, checkpointId)
}

object TDistances : IntIdTableWithCompetitionId("distances") {
    private const val distanceLength = 64
    private const val typeLength = 64
    val distance = varchar(name = "distance", length = distanceLength)
    val type = varchar(name = "type", length = typeLength)
    val checkpointsCount = integer("amountCheckpoints")
}

class TDistance(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TDistance>(TDistances)
    val distance by TDistances.distance
    val type by TDistances.type
    val checkpointsCount by TDistances.checkpointsCount
    val checkpoints by TCheckpoint via TDistancesToCheckpoints // many-to-many reference
}

object TCompetitors : IntIdTable("competitors") {
    private const val startTimeLength = 8
    val competitorNumber = integer("competitorNumber")
    val athleteId =
        reference("athleteId", TAthletes, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val startTime = varchar("startTime", startTimeLength)
}

class TCompetitor(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TCompetitor>(TCompetitors)
    val athleteId by TCompetitors.athleteId
    val competitorNumber by TCompetitors.competitorNumber
    val startTime by TCompetitors.startTime
}

object TCompetitorsData : IntIdTable("competitorsData") {
    val competitorId = reference(
        "competitorId",
        TCompetitors,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val isRemoved = bool("isRemoved").default(false)
}

class TCompetitorData(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TCompetitorData>(TCompetitorsData)
    val competitorId by TCompetitorsData.competitorId
    val isRemoved by TCompetitorsData.isRemoved
}

object TCheckpointsProtocolsToCompetitorsData : Table("CheckpointsProtocolsToCompetitorsData") {
    private val checkpointProtocolId = reference(
        "checkpointProtocolId",
        TCheckpointsProtocols,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    private val competitorDataId = reference(
        "competitorDataId",
        TCompetitorsData,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    override val primaryKey = PrimaryKey(checkpointProtocolId, competitorDataId)
}

object TCheckpointsProtocols : IntIdTable("checkpointsProtocols") {
    private const val timeMeasurementLength = 8
    val competitorId = reference(
        "competitorId",
        TCompetitors,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val checkpointId =
        reference("checkpointId", TCheckpoints, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val timeMeasurement = varchar("timeMeasurement", timeMeasurementLength)
}

class TCheckpointProtocol(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TCheckpointProtocol>(TCheckpointsProtocols)
    val competitionId by TCheckpointsProtocols.competitorId
    val checkpointId by TCheckpointsProtocols.checkpointId
    val timeMeasurement by TCheckpointsProtocols.timeMeasurement
}

object TResultsGroup : IntIdTable("resultsGroup") {
    private const val resultLength = 8
    private const val backlogLength = 9
    val competitorId = reference(
        "competitorId",
        TCompetitors,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val result = varchar("result", resultLength)
    val backlog = varchar("backlog", backlogLength)
    val place = integer("place")
}

class TResultGroup(id: EntityID<Int>) : IntEntity(id) {
    val competitorId by TResultsGroup.competitorId
    val result by TResultsGroup.result
    val backlog by TResultsGroup.backlog
    val place by TResultsGroup.place
}

object TDurationAtCheckpointsToResultsGroupSplit : IntIdTable("durationAtCheckpointsToResultsGroupSplit") {
    private const val timeMeasurementAtCheckpointLength = 8
    val checkpoint =
        reference("checkpointId", TCheckpoints, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val durationAtCheckpoint = varchar("timeMeasurementAtCheckpoint", timeMeasurementAtCheckpointLength)
    val splitsResultGroupId =
        reference(
            "splitsResultGroupId",
            TSplitsResultsGroup,
            onDelete = ReferenceOption.CASCADE,
            onUpdate = ReferenceOption.CASCADE
        )
}


object TSplitsResultsGroup : IntIdTable("splitsResultsGroup") {
    val resultsGroupId = reference(
        "resultsGroupId",
        TResultsGroup,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
}


object TResultsTeam : IntIdTable("resultsGroup") {
    val competitorId = reference(
        "competitorId",
        TCompetitors,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val place = integer("place")
    val score = integer("score")
}

