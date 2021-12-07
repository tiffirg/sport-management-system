package ru.emkn.kotlin.sms.services

import ru.emkn.kotlin.sms.EVENT_TIME
import ru.emkn.kotlin.sms.classes.*
import java.time.Duration

object CommandsHandler {

    // генерация стартовых протоколов

    fun startProtocolsGeneration(applications: List<Team>): List<CompetitorsGroup> {

        // формирование списков участников по группам
        val athleteGroups: Map<Group, List<Athlete>> =
            (applications.flatMap { team -> team.athletes }).groupBy { athlete -> athlete.group }

        // количество номеров, предусмотренных для участников из одной группы
        val maxGroupSize = ((athleteGroups.maxOf { it.value.size } / 10 + 1) * 10)

        // распределение времени старта между группами
        fun generateStartTimes(groupLists: Map<Group, List<Athlete>>): Map<Group, List<Competitor>> {

            var currentStartTime = EVENT_TIME
            var currentGroupIndex = 1

            // жеребьевка каждой группы: по списку группы атлетов
            // формируется список участников соревнований
            fun tossGroup(athletes: List<Athlete>): List<Competitor> {
                val shuffledAthletes = athletes.shuffled()
                val competitors = mutableListOf<Competitor>()
                shuffledAthletes.forEachIndexed { numberInGroup, athlete ->
                    val athleteNumber = currentGroupIndex * maxGroupSize + numberInGroup + 1
                    val startTime = currentStartTime
                    val competitor = Competitor(athleteNumber, startTime, athlete)
                    competitors.add(competitor)
                    currentStartTime = currentStartTime.plusMinutes(1)
                }
                return competitors
            }

            val result = mutableMapOf<Group, List<Competitor>>()
            groupLists.forEach { (group, athletes) ->
                val competitors = tossGroup(athletes)
                result[group] = competitors
                currentGroupIndex++
            }

            return result

        }

        val competitorGroups = generateStartTimes(athleteGroups)
        val startLists = competitorGroups.map { (group, competitors) -> CompetitorsGroup(group, competitors) }
        return startLists.sortedBy { athletesGroup -> athletesGroup.group.toString() }

    }

    // вычисление отставания от лидера

    private fun getBacklog(result: Duration?, leaderTime: Duration?): String {
        return if (result == null) {
            ""
        } else {
            val backlog = result - leaderTime
            "+${backlog}"
        }
    }

    // генерация результатов одного участника

    private fun getCompetitorResult(competitorData: CompetitorData): Duration? {
        return if (competitorData.removed) {
            null
        } else {
            val finishTime = competitorData.checkpoints.last().time
            val startTime = competitorData.competitor.startTime
            Duration.between(startTime, finishTime)
        }
    }

    // генерация результатов одной группы

    private fun generateResultsGroup(competitorsDataGroup: CompetitorsDataGroup): GroupResults {

        // Участники сортируются по времени результата
        // Если человек дисквалифицирован, то его результатом будет специальное значение
        val sortedCompetitorsData = competitorsDataGroup.competitorsData.sortedBy { competitorData ->
            val result = getCompetitorResult(competitorData)
            result?.seconds ?: Double.POSITIVE_INFINITY.toLong()
        }

        val protocols: List<CompetitorResultInGroup> = sortedCompetitorsData.mapIndexed { index, competitorData ->
            CompetitorResultInGroup(
                competitorData.competitor, index + 1,
                getCompetitorResult(competitorData), index + 1, ""
            )
        }

        // вычисление отставания от лидера
        val leaderTime = protocols.first().result
        protocols.forEach { competitorResult ->
            val result = competitorResult.result
            competitorResult.backlog = getBacklog(result, leaderTime)
        }

        return GroupResults(competitorsDataGroup.group, protocols)
    }

    // генерация результатов всех участников

    fun generateResults(data: List<CompetitorData>): List<GroupResults> {
        val competitorsGroups =
            (data.groupBy { competitorData -> competitorData.competitor.group }).map { (group, competitorsData) ->
                CompetitorsDataGroup(group, competitorsData)
            }

        val protocols = competitorsGroups.map { competitorsDataGroup ->
            generateResultsGroup(competitorsDataGroup)
        }

        return protocols.sortedBy { groupResults -> groupResults.group.groupName }
    }

    // генерация сплита одного участника

    private fun getCompetitorSplit(competitorData: CompetitorData): List<CheckpointDuration>? {
        return if (competitorData.removed) {
            null
        } else {
            // генерация сплитов: время на 1 КП - разница между временем отсечки и временем старта
            // время на последующие КП - разница времен текущего и предыдущего КП
            val splits = mutableListOf<CheckpointDuration>()
            competitorData.checkpoints.forEachIndexed { index, _ ->
                if (index == 0) {
                    val firstCheckpoint = competitorData.checkpoints[0]
                    splits.add(
                        CheckpointDuration(
                            firstCheckpoint.checkpoint,
                            Duration.between(competitorData.competitor.startTime, firstCheckpoint.time)
                        )
                    )
                } else {
                    val currCheckpoint = competitorData.checkpoints[index]
                    val prevCheckpoint = competitorData.checkpoints[index - 1]
                    splits.add(
                        CheckpointDuration(
                            currCheckpoint.checkpoint,
                            Duration.between(prevCheckpoint.time, currCheckpoint.time)
                        )
                    )
                }
            }
            splits
        }
    }

    // генерация сплитов группы участников

    private fun generateSplitResultsGroup(competitorsDataGroup: CompetitorsDataGroup): GroupSplitResults {

        val mappedData: Map<Competitor, CompetitorData> =
            competitorsDataGroup.competitorsData.associateBy { competitorData ->
                competitorData.competitor
            }

        val protocols = generateResultsGroup(competitorsDataGroup)

        val splitProtocols: List<CompetitorSplitResultInGroup> =
            protocols.results.map { competitorResultInGroup ->
                val competitorData = mappedData[competitorResultInGroup.competitor]
                assert(competitorData != null) { "mapped data contains information about all competitors" }
                val splits = getCompetitorSplit(competitorData!!)
                CompetitorSplitResultInGroup(competitorResultInGroup, splits)
            }

        return GroupSplitResults(competitorsDataGroup.group, splitProtocols)
    }

    // генерация сплитов всех участников

    fun generateSplitResults(data: List<CompetitorData>): List<GroupSplitResults> {

        val competitorsGroups =
            data.groupBy { competitorData -> competitorData.competitor.group }.map { (group, competitorsData) ->
                CompetitorsDataGroup(group, competitorsData)
            }
        val splitProtocols = competitorsGroups.map { competitorsDataGroup ->
            generateSplitResultsGroup(competitorsDataGroup)
        }

        return splitProtocols.sortedBy { groupSplitResults -> groupSplitResults.group.groupName }
    }


    // генерация результатов команд

    fun generateTeamsResults(groupResultsList: List<GroupResults>): List<TeamResults> {


        val fullResultsList = groupResultsList.flatMap { groupResult -> groupResult.results }
        val teamsList = fullResultsList.groupBy { athleteResult -> athleteResult.competitor.teamName }


        // подсчет очков каждого участника

        val scoresByCompetitor: MutableMap<Competitor, Int> = mutableMapOf()
        groupResultsList.forEach { groupResults ->
            groupResults.results.forEach { competitorResultInGroup ->
                scoresByCompetitor[competitorResultInGroup.competitor] =
                    groupResults.getCompetitorScore(competitorResultInGroup.competitor.athleteNumber)
            }
        }

        // генерация результатов одной команды

        fun generateTeamResult(teamName: String, teamResults: List<CompetitorResultInGroup>): TeamResults {
            val data = teamResults.map { competitorResultInGroup ->
                val competitor = competitorResultInGroup.competitor
                val score =  scoresByCompetitor[competitor]
                assert(score != null) {"scoresByCompetitor contains information about all competitors"}
                CompetitorResultInTeam(
                    competitor, competitorResultInGroup.place,
                    score!!
                )
            }
            val teamScore = data.sumOf { it.score }
            return TeamResults(teamName, teamScore, data)
        }

        val temResults = teamsList.map { (teamName, teamResults) ->
            generateTeamResult(teamName, teamResults)
        }

        return temResults.sortedByDescending { teamResults -> teamResults.teamScore }

    }

}