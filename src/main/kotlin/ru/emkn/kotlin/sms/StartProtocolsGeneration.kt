package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.data.*
import java.time.LocalDateTime



fun startProtocolsGeneration(applications: List<Team>): List<AthletesGroup> {

    // формирование списков участников по группам
    val groupLists: Map<Group, MutableList<Athlete>> =
        (applications.flatMap { team -> team.athletes }).groupByTo(mutableMapOf()) { athlete -> athlete.group }

    // количество номеров, предусмотренных для участников из одной группы
    val maxGroupSize = (groupLists.maxOf { it.value.size } / 10) * 10

    // распределение времени старта между группами
    fun generateStartTimes() {

        var currentStartTime = LocalDateTime.parse("12:00:00")
        var currentGroupIndex = 1

        // жеребьевка внутри каждой группы
        fun tossGroup(participants: MutableList<Athlete>) {
            participants.shuffle()
            participants.forEachIndexed { numberInGroup, athlete ->
                athlete.athleteNumber = currentGroupIndex * maxGroupSize + numberInGroup
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