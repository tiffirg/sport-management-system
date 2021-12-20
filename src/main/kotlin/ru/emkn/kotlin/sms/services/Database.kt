package ru.emkn.kotlin.sms.services

import TAthlete
import TAthletes
import TCheckpoint
import TCheckpoints
import TCheckpointsProtocols
import TCheckpointsProtocolsToCompetitorsData
import TCompetition
import TCompetitions
import TCompetitorData
import TCompetitors
import TCompetitorsData
import TDistances
import TDistancesToCheckpoints
import TDurationAtCheckpointsToResultsGroupSplit
import TGroup
import TGroups
import TRank
import TRanks
import TResultsGroup
import TResultsTeam
import TSplitsResultsGroup
import TTeam
import TTeams
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.classes.*
import java.io.File


fun main() {
    val db = GeneralDatabase()
    initConfig("src/test/resources/config.yaml")
    db.insertConfigData()
    db.installConfigData(1)
}

data class CheckpointRecord(val competitorNumber: Int, val checkpoint: String, val timeMeasurement: String)

interface DatabaseInterface {

    // получить сущность соревнования по названию
    fun getCompetition(title: String): TCompetition?

    // получить сущности всех чекпоинтов
    fun getCheckpoints(): List<CheckpointRecord>?

    // загрузка данных конфигурационного файла в базу данных
    fun insertConfigData(): TCompetition

    // загрузка данных конфигурационного файла из базы данных
    fun installConfigData(competitionId: Int)

    // добавление одной дистанции в базу данных
    fun insertDistanceOf(
        title: String,
        distanceType: DistanceType,
        amountCheckpoints: Int,
        checkpoints: List<String>
    ): Boolean

    // добавление одной группы участников в базу данных
    fun insertGroupOf(title: String, distance: String): Boolean


    // удаление одной группы участников из базы данных
    fun deleteGroupOf(title: String): Boolean

    // изменение одной группы участников
    fun updateGroupOf(title: String, newDistance: String): Boolean

    // добавление атлетов и команд в базу данных
    fun insertApplications(competition: TCompetition, applications: List<Team>)

    // добавление одной команды
    fun insertTeamOf(title: String): Boolean

    // добавление одного спортсмена
    fun insertAthleteOf(athlete: Athlete): Boolean

    fun checkStartsProtocols(competitionId: Int): Boolean

    fun checkResultsGroup(competitionId: Int): Boolean

    fun checkTeamResults(competitionId: Int): Boolean

}

class GeneralDatabase : DatabaseInterface {
    private val dbPath = "database/competitions"
    private val db: Database

    // создание базы данных: подключение файла с базой данных и создание логгера
    init {
        db = connect()
        transaction {
            addLogger(StdOutSqlLogger)
        }
    }

    // подключить базу данных и загрузить еще не созданные таблицы
    private fun connect(): Database {
        val isExist = File(dbPath).exists()
        val database = Database.connect(url = "jdbc:h2:./${dbPath}", driver = "org.h2.Driver")
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

    // получить сущность соревнования по названию
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

    // получить сущности всех чекпоинтов
    override fun getCheckpoints(): List<CheckpointRecord>? {
        val res = mutableListOf<CheckpointRecord>()
        transaction {
            val competition = TCompetition.findById(COMPETITION_ID) ?: return@transaction
            TCheckpointProtocol.all().forEach { tCheckpointProtocol ->
                if (tCheckpointProtocol.competitionId == competition.id) {
                    val checkpointString = TCheckpoint.findById(tCheckpointProtocol.checkpointId)?.checkpoint
                        ?: throw IllegalStateException("getCheckpoints: no such checkpoint in the database")
                    val timeMeasurement = tCheckpointProtocol.timeMeasurement
                    val competitorData =
                        TCompetitorData.all().find { it.checkpointProtocol.contains(tCheckpointProtocol) }
                            ?: throw IllegalStateException("getCheckpoints: no such competitorData in the database")
                    val competitor = TCompetitor.findById(competitorData.competitorId)
                        ?: throw IllegalStateException("getCheckpoints: no such competitor in the database")
                    val record = CheckpointRecord(competitor.competitorNumber, checkpointString, timeMeasurement)
                    res.add(record)
                }
            }

        }
        return res.ifEmpty { null }
    }

    // загрузка данных конфигурационного файла в базу данных
    override fun insertConfigData(): TCompetition {
        lateinit var competition: TCompetition
        transaction {
            competition = TCompetition.new {
                eventName = EVENT_NAME
                sport = EVENT_SPORT
                date = EVENT_DATE_STRING
                time = EVENT_TIME_STRING
            }
            RANK_NAMES.forEach {
                TRank.new {
                    competitionId = competition.id
                    rank = it
                }
            }
            DISTANCE_CRITERIA.forEach { (it_distance, criteria) ->
                TDistance.new {
                    competitionId = competition.id
                    distance = it_distance
                    type = criteria.distanceType
                    checkpointsCount = criteria.checkpointsCount
                }
            }
            GROUP_DISTANCES.forEach { (it_group, it_distance) ->
                val distanceReference =
                    TDistance.find { (TDistances.distance eq it_distance) and (TDistances.competitionId eq competition.id) }
                        .limit(1).first()
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
            DISTANCE_CRITERIA.forEach { (distance, criteria) ->
                val distanceReference =
                    TDistance.find { (TDistances.distance eq distance) and (TDistances.competitionId eq competition.id) }
                        .limit(1).first()
                criteria.checkpointsOrder.forEach { checkpoint ->
                    if (checkpoint.isNotEmpty()) {
                        val checkpointReference =
                            TCheckpoint.find { (TCheckpoints.checkpoint eq checkpoint) and (TCheckpoints.competitionId eq competition.id) }
                                .limit(1)
                        TDistancesToCheckpoints.insert {
                            it[distanceId] = distanceReference.id
                            it[checkpointId] = checkpointReference.first().id
                        }
                    }
                }

            }
        }
        return competition
    }

    // загрузка данных конфигурационного файла из базы данных
    override fun installConfigData(competitionId: Int) {
        transaction {
            val dataGroupTable = TGroup.find { TGroups.competitionId eq competitionId }
            val dataDistanceGroup = TDistance.find { TDistances.competitionId eq competitionId }
            RANK_NAMES = TRank.find { TRanks.competitionId eq competitionId }.mapTo(mutableListOf()) { it.rank }
            GROUP_NAMES = dataGroupTable.mapTo(mutableListOf()) { it.group }
            CHECKPOINTS_LIST = TCheckpoint.find { TCheckpoints.competitionId eq competitionId }
                .mapTo(mutableListOf()) { it.checkpoint }
            GROUP_DISTANCES = dataGroupTable.associateTo(mutableMapOf()) {
                it.group to TDistance.find { (TDistances.id eq it.distanceId) and (TDistances.competitionId eq competitionId) }
                    .limit(1).first().distance
            }
            DISTANCE_CRITERIA = dataDistanceGroup.associateTo(mutableMapOf()) { distanceData ->
                val checkpoints = distanceData.checkpoints.map { it.checkpoint }
                distanceData.distance to when (distanceData.type) {
                    DistanceType.FIXED -> FixedRoute(checkpoints)
                    DistanceType.CHOICE -> ChoiceRoute(distanceData.checkpointsCount, checkpoints)
                }
            }
        }

    }

    // добавление одной дистанции в базу данных
    override fun insertDistanceOf(
        title: String,
        distanceType: DistanceType,
        amountCheckpoints: Int,
        checkpoints: List<String>
    ): Boolean {
        var result = true
        transaction {
            try {
                val tCheckpointsList = checkpoints.map {
                    TCheckpoint.find {
                        (TCheckpoints.checkpoint eq it) and (
                                TCheckpoints.competitionId eq COMPETITION_ID)
                    }.first()
                }
                val competition = TCompetition.findById(COMPETITION_ID) ?: return@transaction
                val tDistance = TDistance.new {
                    competitionId = competition.id
                    distance = title
                    type = distanceType
                    checkpointsCount = amountCheckpoints

                }
                tDistance.checkpoints = SizedCollection(tCheckpointsList)
            } catch (e: Exception) {
                LOGGER.debug { e }
                result = false
            }

        }
        return result
    }

    // добавление одной группы участников в базу данных
    override fun insertGroupOf(title: String, distance: String): Boolean {
        var result = false
        transaction {
            val query =
                TDistance.find { (TDistances.distance eq distance) and (TDistances.competitionId eq COMPETITION_ID) }
                    .limit(1)
            if (query.empty()) {
                return@transaction
            }
            val distanceData = query.first()
            val competition = TCompetition.findById(COMPETITION_ID) ?: return@transaction
            TGroup.new {
                competitionId = competition.id
                group = title
                distanceId = distanceData.id
            }
            result = true
        }
        LOGGER.debug { "Database: insertGroupOf | $result" }
        return result
    }

    // удаление одной группы участников из базы данных
    override fun deleteGroupOf(title: String): Boolean {
        var result = false
        transaction {
            val query =
                TGroup.find { (TGroups.group eq title) and (TGroups.competitionId eq COMPETITION_ID) }
                    .limit(1)
            if (query.empty()) {
                return@transaction
            }
            val group = query.first()
            group.delete()
            result = true
        }
        LOGGER.debug { "Database: deleteGroupOf | $result" }
        return result
    }

    // изменение одной группы участников
    override fun updateGroupOf(title: String, newDistance: String): Boolean {
        var result = false
        transaction {
            val distanceQuery =
                TDistance.find { (TDistances.distance eq newDistance) and (TDistances.competitionId eq COMPETITION_ID) }
                    .limit(1)
            if (distanceQuery.empty()) {
                return@transaction
            }
            val distanceData = distanceQuery.first()
            val groupQuery =
                TGroup.find { (TGroups.group eq title) and (TGroups.competitionId eq COMPETITION_ID) }
                    .limit(1)
            if (groupQuery.empty()) {
                return@transaction
            }
            result = true
            val groupData = groupQuery.first()
            groupData.distanceId = distanceData.id
        }
        LOGGER.debug { "Database: insertGroupOf | $result" }
        return result
    }

    override fun checkStartsProtocols(competitionId: Int): Boolean = false

    override fun checkResultsGroup(competitionId: Int): Boolean = false

    override fun checkTeamResults(competitionId: Int): Boolean = false

    // добавление атлетов и команд в базу данных
    override fun insertApplications(competition: TCompetition, applications: List<Team>) {
        transaction {
            applications.forEach { application ->
                TTeam.new {
                    competitionId = competition.id
                    team = application.teamName
                }
                application.athletes.forEach { athlete ->

                    val groupReference: TGroup =
                        TGroup.find { TGroups.group eq athlete.group.groupName }.limit(1).first()
                    val rankReference: TRank =
                        TRank.find { TRanks.rank eq (athlete.rank.rankName ?: "") }.limit(1).first()
                    val teamReference: TTeam =
                        TTeam.find { TTeams.team eq (athlete.teamName) }.limit(1).first()

                    TAthlete.new {
                        competitionId = competition.id
                        name = athlete.name
                        surname = athlete.surname
                        birthYear = athlete.birthYear
                        groupId = groupReference.id
                        rankId = rankReference.id
                        teamId = teamReference.id
                    }
                }
            }
        }
    }

    // добавление одной команды
    override fun insertTeamOf(title: String): Boolean {
        var result = false
        transaction {
            val query =
                TTeam.find { (TTeams.team eq title) and (TTeams.competitionId eq COMPETITION_ID) }
                    .limit(1)
            if (!query.empty()) {
                return@transaction
            }
            val competition = TCompetition.findById(COMPETITION_ID) ?: return@transaction
            TTeam.new {
                competitionId = competition.id
                team = title
            }
            result = true
        }
        LOGGER.debug { "Database: insertTeamOf | $result" }
        return result
    }

    // добавление одного спортсмена
    override fun insertAthleteOf(athlete: Athlete): Boolean {
        var result = false
        transaction {
            val athleteQuery =
                TAthlete.find { (TAthletes.name eq athlete.name) and (TAthletes.surname eq athlete.surname) and (TTeams.competitionId eq COMPETITION_ID) }
                    .limit(1)
            if (!athleteQuery.empty()) {
                return@transaction
            }
            val competition = TCompetition.findById(COMPETITION_ID) ?: return@transaction

            val groupQuery =
                TGroup.find { (TGroups.group eq athlete.group.groupName) and (TGroups.competitionId eq COMPETITION_ID) }
                    .limit(1)
            if (groupQuery.empty()) {
                return@transaction
            }
            val newGroup = groupQuery.first()

            val rankName = athlete.rank.rankName ?: ""
            val rankQuery =
                TRank.find { (TRanks.rank eq rankName) and (TGroups.competitionId eq COMPETITION_ID) }
                    .limit(1)
            if (rankQuery.empty()) {
                return@transaction
            }
            val newRank = rankQuery.first()

            val teamQuery =
                TTeam.find { (TTeams.team eq athlete.teamName) and (TTeams.competitionId eq COMPETITION_ID) }
                    .limit(1)
            if (teamQuery.empty()) {
                return@transaction
            }
            val newTeam = teamQuery.first()

            TAthlete.new {
                competitionId = competition.id
                name = athlete.name
                surname = athlete.surname
                groupId = newGroup.id
                rankId = newRank.id
                teamId = newTeam.id
            }
            result = true
        }
        LOGGER.debug { "Database: insertAthleteOf | $result" }
        return result
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
