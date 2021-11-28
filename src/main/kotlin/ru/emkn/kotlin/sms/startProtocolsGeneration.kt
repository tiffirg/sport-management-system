package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.Athlete
import ru.emkn.kotlin.sms.classes.Group
import ru.emkn.kotlin.sms.utils.InvalidTimeException
import ru.emkn.kotlin.sms.utils.toLocalTime



data class Team(val teamName: String, val athletes: List<Athlete>)

data class AthletesGroup(val group: Group, val athletes: List<Athlete>)


fun startProtocolsGeneration(applications: List<Team>): List<AthletesGroup> {

    // формирование списков участников по группам
    val groupLists: Map<Group, MutableList<Athlete>> =
        (applications.flatMap { team -> team.athletes }).groupByTo(mutableMapOf()) { athlete -> athlete.group }

    // количество номеров, предусмотренных для участников из одной группы
    val maxGroupSize = ((groupLists.maxOf { it.value.size } / 10 + 1) * 10)

    // распределение времени старта между группами
    fun generateStartTimes() {

        var currentStartTime = EVENT_TIME.toLocalTime() ?: throw InvalidTimeException(EVENT_TIME) // TODO(Перенести в init)
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
    return groupLists.map { (group, athleteList) -> AthletesGroup(group, athleteList) }
}