package ru.emkn.kotlin.sms.services

import ru.emkn.kotlin.sms.EVENT_TIME
import ru.emkn.kotlin.sms.TimeFormatter
import ru.emkn.kotlin.sms.classes.Athlete
import ru.emkn.kotlin.sms.classes.AthletesGroup
import ru.emkn.kotlin.sms.classes.Team
import ru.emkn.kotlin.sms.classes.Group
import ru.emkn.kotlin.sms.classes.ResultsGroup
import ru.emkn.kotlin.sms.classes.ResultsTeam
import ru.emkn.kotlin.sms.classes.SplitResultsGroup
import ru.emkn.kotlin.sms.classes.CheckpointTime
import ru.emkn.kotlin.sms.classes.ResultAthleteInGroup
import ru.emkn.kotlin.sms.classes.AthleteResultInTeam
import ru.emkn.kotlin.sms.classes.SplitResultAthleteGroup
import ru.emkn.kotlin.sms.minus
import java.time.LocalTime

object GenerationResultsOfCommands {

    fun startProtocolsGeneration(applications: List<Team>): List<AthletesGroup> {

        // формирование списков участников по группам
        val groupLists: Map<Group, MutableList<Athlete>> =
            (applications.flatMap { team -> team.athletes }).groupByTo(mutableMapOf()) { athlete -> athlete.group }

        // количество номеров, предусмотренных для участников из одной группы
        val maxGroupSize = ((groupLists.maxOf { it.value.size } / 10 + 1) * 10)

        // распределение времени старта между группами
        fun generateStartTimes() {

            var currentStartTime = EVENT_TIME
            var currentGroupIndex = 1

            // жеребьевка внутри каждой группы
            fun tossGroup(participants: MutableList<Athlete>) {
                participants.shuffle()
                participants.forEachIndexed { numberInGroup, athlete ->
                    athlete.athleteNumber = currentGroupIndex * maxGroupSize + numberInGroup + 1
                    athlete.startTime = currentStartTime
                    currentStartTime = currentStartTime.plusMinutes(1)
                }
            }

            groupLists.forEach { (_, participants) ->
                tossGroup(participants)
                currentGroupIndex++
            }

        }

        generateStartTimes()
        val startLists = groupLists.map { (group, athleteList) -> AthletesGroup(group, athleteList) }
        return startLists.sortedBy { athletesGroup -> athletesGroup.group.toString() }
    }


    fun generateResults(dataCheckpoints: List<Athlete>): Map<Group, ResultsGroup> {
        val athletesGroups = dataCheckpoints.groupBy { athlete -> athlete.group }
        val protocols = (athletesGroups.map { (group, athletesGroup) ->
            Pair(group, ResultsGroup(group, generateResultsGroup(AthletesGroup(group, athletesGroup))))
        }).toMap()
        protocols.toSortedMap(compareBy { it.groupName })
        return protocols
    }

    fun generateSplitResults(dataCheckpoints: List<Athlete>): Map<Group, SplitResultsGroup> {
        val athletesGroups = dataCheckpoints.groupBy { athlete -> athlete.group }
        val splitProtocols = (athletesGroups.map { (group, athletesGroup) ->
            Pair(group, SplitResultsGroup(group, generateSplitResultsGroup(AthletesGroup(group, athletesGroup))))
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

        fun generateTeamResult(teamName: String, teamResults: List<ResultAthleteInGroup>): ResultsTeam {
            val data = teamResults.map { (_, athleteNumber, surname, name, birthYear, rank, _, _, place, _) ->
                AthleteResultInTeam(
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

    private fun generateSplitResultsGroup(athletesGroup: AthletesGroup): List<SplitResultAthleteGroup> {
        val sortedAthletes = athletesGroup.athletes.sortedBy { athlete ->
            val resultTimeOrNull = getAthleteResult(athlete)
            resultTimeOrNull?.toSecondOfDay() ?: Double.POSITIVE_INFINITY.toInt()
        }

        val leaderTime = getAthleteResult(sortedAthletes.first())

        val splitProtocols: List<SplitResultAthleteGroup> = sortedAthletes.mapIndexed { index, athlete ->
            val split = getAthleteSplit(athlete)
            val result = getAthleteResult(athlete)
            SplitResultAthleteGroup(
                index + 1, athlete.athleteNumber!!,
                athlete.surname, athlete.name, athlete.birthYear,
                athlete.rank, athlete.teamName, split,
                index + 1, getBacklog(result, leaderTime)
            )
        }

        return splitProtocols
    }

    private fun getAthleteSplit(athlete: Athlete): List<CheckpointTime>? {
        athlete.checkCheckpoints()
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

    private fun getAthleteResult(athlete: Athlete): LocalTime? {
        athlete.checkCheckpoints()
        return if (athlete.removed) {
            null
        } else {
            val finishTime = athlete.checkpoints!!.last().time
            finishTime.minus(athlete.startTime)
        }
    }

    private fun generateResultsGroup(athletesGroup: AthletesGroup): List<ResultAthleteInGroup> {

        // TODO: присвоение разрядов

        // Атлеты сортируются по времени результата
        // Если человек дисквалифицирован, то его результатом буде специальное значение

        val sortedAthletes = athletesGroup.athletes.sortedBy { athlete ->
            val resultTimeOrNull = getAthleteResult(athlete)
            resultTimeOrNull?.toSecondOfDay() ?: Double.POSITIVE_INFINITY.toInt()
        }

        val protocols: List<ResultAthleteInGroup> = sortedAthletes.mapIndexed { index, athlete ->
            ResultAthleteInGroup(
                index + 1, athlete.athleteNumber!!,
                athlete.surname, athlete.name, athlete.birthYear,
                athlete.rank, athlete.teamName, getAthleteResult(athlete),
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
}