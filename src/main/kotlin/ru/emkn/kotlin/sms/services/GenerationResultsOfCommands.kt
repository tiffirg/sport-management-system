package ru.emkn.kotlin.sms.services

import ru.emkn.kotlin.sms.EVENT_TIME
import ru.emkn.kotlin.sms.TimeFormatter
import ru.emkn.kotlin.sms.classes.*
import ru.emkn.kotlin.sms.minus
import java.time.Duration
import java.time.LocalTime

object GenerationResultsOfCommands {

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

        // TODO: присвоение разрядов

        // Атлеты сортируются по времени результата
        // Если человек дисквалифицирован, то его результатом буде специальное значение

        val sortedCompetitorsData = competitorsDataGroup.competitorsData.sortedBy { competitorData ->
            val resultTimeOrNull = getCompetitorResult(competitorData)
            resultTimeOrNull?.toSecondOfDay() ?: Double.POSITIVE_INFINITY.toInt()
        }

        val protocols: List<CompetitorResultInGroup> = sortedCompetitorsData.mapIndexed { index, athlete ->
            CompetitorResultInGroup(
                index + 1, athlete.athleteNumber!!,
                athlete.surname, athlete.name, athlete.birthYear,
                athlete.rank, athlete.teamName, getCompetitorResult(athlete),
                index + 1, ""
            )
        }

        // вычисление отставания от лидера
        val leaderTime = protocols.first().result
        protocols.forEach { resultAthleteInGroup ->
            val result = resultAthleteInGroup.result
            resultAthleteInGroup.backlog = getBacklog(result, leaderTime)
        }

        return protocols
    }


    // генерация результатов всех участников

    fun generateResults(data: List<CompetitorData>): List<GroupResults> {
        val athletesGroups =
            (data.groupBy { competitorData -> competitorData.competitor.group }).map { (group, competitorsData) ->
                CompetitorsDataGroup(group, competitorsData)
            }

        val protocols = athletesGroups.map { competitorsDataGroup ->
            generateResultsGroup(competitorsDataGroup)
        }

        return protocols.sortedBy { groupResults -> groupResults.group.groupName }
    }



    fun generateSplitResults(dataCheckpoints: List<Competitor>): Map<Group, GroupSplitResults> {
        val athletesGroups = dataCheckpoints.groupBy { athlete -> athlete.group }
        val splitProtocols = (athletesGroups.map { (group, athletesGroup) ->
            Pair(group, GroupSplitResults(group, generateSplitResultsGroup(CompetitorsGroup(group, athletesGroup))))
        }).toMap()
        splitProtocols.toSortedMap(compareBy { it.groupName })
        return splitProtocols
    }

    fun teamResultsGeneration(listOfGroups: MutableList<ResultsGroup>): Map<String, ResultsTeam> {


        val resultsForGroups = listOfGroups.associate { resultsGroup -> Pair(resultsGroup.group, resultsGroup.results) }
        val athleteResults = resultsForGroups.flatMap { it.value }
        val teamsResults = athleteResults.groupBy { athleteResult -> athleteResult.teamName }

        val scoresByAthleteNumber: MutableMap<Int, Int> = mutableMapOf()
        val groupByAthleteNumber: MutableMap<Int, Group> = mutableMapOf()

        listOfGroups.forEach { resultsGroup ->
            resultsGroup.results.forEach { athleteResult ->
                scoresByAthleteNumber[athleteResult.athleteNumber] =
                    resultsGroup.getAthleteScore(athleteResult.athleteNumber)
                groupByAthleteNumber[athleteResult.athleteNumber] = resultsGroup.group
            }
        }

        fun generateTeamResult(teamName: String, teamResults: List<CompetitorResultInGroup>): ResultsTeam {
            val data = teamResults.map { (_, athleteNumber, surname, name, birthYear, rank, _, _, place, _) ->
                CompetitorResultInTeam(
                    athleteNumber, name, surname, birthYear, rank,
                    groupByAthleteNumber[athleteNumber]!!, place, scoresByAthleteNumber[athleteNumber]!!
                )
            }
            val teamScore = data.sumOf { it.score }
            return ResultsTeam(teamName, teamScore, data)
        }

        val res = teamsResults.map { (teamName, teamResults) ->
            Pair(teamName, generateTeamResult(teamName, teamResults))
        }.toMap()

        return res.toSortedMap(compareByDescending { res[it]!!.teamScore })

    }

    private fun generateSplitResultsGroup(athletesGroup: CompetitorsGroup): List<CompetitorSplitResultInGroup> {
        val sortedAthletes = athletesGroup.competitors.sortedBy { athlete ->
            val resultTimeOrNull = getCompetitorResult(athlete)
            resultTimeOrNull?.toSecondOfDay() ?: Double.POSITIVE_INFINITY.toInt()
        }

        val leaderTime = getCompetitorResult(sortedAthletes.first())

        val splitProtocols: List<CompetitorSplitResultInGroup> = sortedAthletes.mapIndexed { index, athlete ->
            val split = getAthleteSplit(athlete)
            val result = getCompetitorResult(athlete)
            CompetitorSplitResultInGroup(
                index + 1, athlete.athleteNumber!!,
                athlete.surname, athlete.name, athlete.birthYear,
                athlete.rank, athlete.teamName, split,
                index + 1, getBacklog(result, leaderTime)
            )
        }

        return splitProtocols
    }

    private fun getAthleteSplit(athlete: Competitor): List<CheckpointTime>? {
        return if (athlete.removed) {
            null
        } else {
            // генерация сплитов: время на 1 КП - разница между временем отсечки и временем старта
            // время на последующие КП - разница времен текущего и предыдущего КП
            val splits = mutableListOf<CheckpointTime>()
            athlete.checkpoints!!.forEachIndexed { index, _ ->
                if (index == 0) {
                    val firstCheckpoint = athlete.checkpoints!![0]
                    splits.add(
                        CheckpointTime(
                            firstCheckpoint.checkpoint,
                            firstCheckpoint.time.minus(athlete.startTime)
                        )
                    )
                } else {
                    val currCheckpoint = athlete.checkpoints!![index]
                    val prevCheckpoint = athlete.checkpoints!![index - 1]
                    splits.add(
                        CheckpointTime(
                            currCheckpoint.checkpoint,
                            currCheckpoint.time.minus(prevCheckpoint.time)
                        )
                    )
                }
            }
            splits
        }
    }

    // функция вычисляет отставание от лидера
    private fun getBacklog(result: LocalTime?, leaderTime: LocalTime?): String {
        return if (result == null) {
            ""
        } else {
            "+${result.minus(leaderTime).format(TimeFormatter)}"
        }
    }

}