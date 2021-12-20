import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import ru.emkn.kotlin.sms.classes.DistanceType

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

    var eventName by TCompetitions.eventName
    var sport by TCompetitions.sport
    var date by TCompetitions.date
    var time by TCompetitions.time
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

    var competitionId by TAthletes.competitionId
    var name by TAthletes.name
    var surname by TAthletes.surname
    var birthYear by TAthletes.birthYear
    var groupId by TAthletes.groupId
    var rankId by TAthletes.rankId
    var teamId by TAthletes.teamId
}

object TTeams : IntIdTableWithCompetitionId("team") {
    private const val teamLength = 65
    val team = varchar("team", teamLength)
}

class TTeam(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TTeam>(TTeams)

    var competitionId by TTeams.competitionId
    var team by TTeams.team
}

object TGroups : IntIdTableWithCompetitionId("group") {
    private const val groupLength = 65
    val group = varchar("group", groupLength)
    val distanceId =
        reference("distanceId", TDistances, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

class TGroup(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TGroup>(TGroups)

    var competitionId by TGroups.competitionId
    var group by TGroups.group
    var distanceId by TGroups.distanceId
}

object TRanks : IntIdTableWithCompetitionId("rank") {
    private const val rankLength = 32
    val rank = varchar("rank", rankLength)
}

class TRank(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TRank>(TRanks)

    var competitionId by TRanks.competitionId
    var rank by TRanks.rank
}

object TCheckpoints : IntIdTableWithCompetitionId("checkpoints") {
    private const val checkpointLength = 16
    val checkpoint = varchar("checkpoint", checkpointLength)
}

class TCheckpoint(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TCheckpoint>(TCheckpoints)
    var competitionId by TCheckpoints.competitionId
    var checkpoint by TCheckpoints.checkpoint
}

object TDistancesToCheckpoints : IntIdTable("distancesToCheckpoints") {
    val distanceId =
        reference("distanceId", TDistances, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val checkpointId =
        reference("checkpointId", TCheckpoints, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
}

object TDistances : IntIdTableWithCompetitionId("distances") {
    private const val distanceLength = 64
    private const val typeLength = 64
    val distance = varchar(name = "distance", length = distanceLength)
    val type = customEnumeration(
        "type",
        "ENUM('FIXED', 'CHOICE')",
        { value ->
            DistanceType.values().find { it.name == value }
                ?: throw IllegalArgumentException("Unknown Distance Type  value")
        },
        { it.name })
    val checkpointsCount = integer("amountCheckpoints")
}

class TDistance(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TDistance>(TDistances)

    var competitionId by TDistances.competitionId
    var distance by TDistances.distance
    var type by TDistances.type
    var checkpointsCount by TDistances.checkpointsCount
    var checkpoints by TCheckpoint via TDistancesToCheckpoints // many-to-many reference
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

    var athleteId by TCompetitors.athleteId
    var competitorNumber by TCompetitors.competitorNumber
    var startTime by TCompetitors.startTime
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
    var competitorId by TCompetitorsData.competitorId
    var isRemoved by TCompetitorsData.isRemoved
    var checkpointProtocol by TCheckpointProtocol via TCheckpointsProtocolsToCompetitorsData // many-to-many reference
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