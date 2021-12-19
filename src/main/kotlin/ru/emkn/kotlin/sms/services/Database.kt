package ru.emkn.kotlin.sms.services

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.classes.CompetitorsGroup
import ru.emkn.kotlin.sms.classes.Team
import java.io.File


fun main() {
    val db = GeneralDatabase()
    initConfig("src/test/resources/config.yaml")
    db.insertConfigData()
}

interface DatabaseInterface {
    val dbPath: String
    val db: Database

    fun getCompetition(title: String): TCompetition?

    fun insertConfigData()
}

class GeneralDatabase : DatabaseInterface {
    override val dbPath = "database/competitions"
    override val db: Database

    init {
        db = connect()
        transaction {
            addLogger(StdOutSqlLogger)
        }
    }

    override fun getCompetition(title: String): TCompetition? {
        var competition: TCompetition? = null
        transaction {
            val query = TCompetition.find { TCompetitions.eventName eq title }.limit(1)
            if (!query.empty()) {
                competition = query.first()
            }
        }
        return competition
    }

    private fun connect(): Database {
        val isExist = File(dbPath).exists()
        val database = Database.connect("jdbc:sqlite:Mydb.db" , "org.sqlite.JDBC")
        if (!isExist) {
            transaction {
                SchemaUtils.create(
                    TCompetitions,
                    TGroups,
                    TRanks,
                    TCheckpoints,
                    TDistances,
                    TDistancesToCheckpoints,
                    TTeams,
                    TAthletes,
                    TCompetitors,
                    TCompetitorsData,
                    TCheckpointsProtocols,
                    TCheckpointsProtocolsToCompetitorsData,
                    TResultsGroup,
                    TSplitsResultsGroup,
                    TDurationAtCheckpointsToResultsGroupSplit,
                    TResultsTeam
                )
            }
        }
        return database
    }

    override fun insertConfigData() {
        transaction {
            val competition = TCompetition.new {
                eventName = EVENT_NAME
                sport = EVENT_SPORT
                date = EVENT_DATE_STRING
                time = EVENT_TIME_STRING
            }
            RANKS.forEach {
                TRank.new {
                    competitionId = competition.id
                    rank = it
                }
            }
            DISTANCES.forEach { (it_distance, it_data) ->
                TDistance.new {
                    competitionId = competition.id
                    distance = it_distance
                    type = it_data.first
                    checkpointsCount = it_data.second
                }
            }
            GROUP_DISTANCES.forEach { (it_group, it_distance) ->
                val distanceReference: TDistance = TDistance.all().find { it.distance == it_distance }
                    ?: throw IllegalStateException("All distances must be stored in the database")
                TGroup.new {
                    competitionId = competition.id
                    group = it_group
                    distanceId = distanceReference.id
                }
            }
            CHECKPOINTS_LIST.forEach { it_checkpoint ->
                TCheckpoint.new {
                    competitionId = competition.id
                    checkpoint = it_checkpoint
                }
            }

        }
    }

    // добавление атлетов и команд в базу данных
    fun addApplications(competition: TCompetition, applications: List<Team>) {
        transaction {
            applications.forEach { application ->
                TTeam.new {
                    competitionId = competition.id
                    team = application.teamName
                }
                application.athletes.forEach { athlete ->

                    val groupReference: TGroup = TGroup.all().find { it.group == athlete.group.groupName }
                        ?: throw IllegalStateException("Group ${athlete.group.groupName} is not stored in database")
                    val rankReference: TRank = TRank.all().find { it.rank == athlete.rank.rankName }
                        ?: throw IllegalStateException("Rank ${athlete.rank.rankName} is not stored in database")
                    TAthlete.new {
                        competitionId = competition.id
                        name = athlete.name
                        surname = athlete.surname
                        birthYear = athlete.birthYear
                        groupId = groupReference.id
                        rankId = rankReference.id
                    }
                }
            }
        }
    }

    // добавление участников соревнований
    fun addProtocolsStart(competition: TCompetition, data: List<CompetitorsGroup>) {
        transaction {
            data.forEach { competitorsGroup ->
                competitorsGroup.competitors.forEach { competitor ->
                    val athleteReference: TAthlete = TAthlete.all().find {
                        it.competitionId == competition.id && it.surname == competitor.surname && it.name == competitor.name
                    } ?: throw IllegalStateException("Athlete ${competitor.surname} is not stored in database")
                    TCompetitor.new {
                        athleteId = athleteReference.id
                        competitorNumber = competitor.athleteNumber
                        startTime = competitor.startTime.toString()
                    }
                }
            }
        }
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

