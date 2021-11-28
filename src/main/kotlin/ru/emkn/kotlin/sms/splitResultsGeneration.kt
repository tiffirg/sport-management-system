package ru.emkn.kotlin.sms
import ru.emkn.kotlin.sms.classes.*
import java.time.LocalTime



data class SplitResultAthleteGroup (val athleteNumberInGroup: Int, val athleteNumber: Int,
                                    val surname: String, val name: String, val birthYear: Int,
                                    val rank: Rank, val teamName: String, val splits: List<LocalTime>?,
                                    val place: Int, val backlog: String)

data class SplitResultsGroup(val group: Group, val results: List<SplitResultAthleteGroup>)


fun getAthleteSplit(athlete: Athlete): List<LocalTime>? {
    athlete.checkCheckpoints()
    return if (athlete.removed) {
        null
    } else {
        // генерация сплитов: время на 1 КП - разница между временем отсечки и временем старта
        // время на последующие КП - разница времен текущего и предыдущего КП
        val splits = mutableListOf<LocalTime>()
        athlete.checkpoints!!.forEachIndexed { index, _ ->
            if (index == 0) {
                splits.add(athlete.checkpoints!![0].time.minus(athlete.startTime))
            } else {
                splits.add(athlete.checkpoints!![index].time.minus(athlete.checkpoints!![index - 1].time))
            }
        }
        splits
    }
}


fun generateSplitResultsGroup(athletesGroup: AthletesGroup): List<SplitResultAthleteGroup> {
    val sortedAthletes = athletesGroup.athletes.sortedBy { athlete ->
        val resultTimeOrNull = getAthleteResult(athlete)
        resultTimeOrNull?.toSecondOfDay() ?: INF
    }
    val splitProtocols: List<SplitResultAthleteGroup> = sortedAthletes.mapIndexed { index, athlete ->
        SplitResultAthleteGroup(
            index + 1, athlete.athleteNumber!!,
            athlete.surname, athlete.name, athlete.birthYear,
            athlete.rank, athlete.teamName, getAthleteSplit(athlete),
            index + 1, ""
        )
    }
    return splitProtocols
}


fun generateSplitResults(dataCheckpoints: List<Athlete>): Map<Group, SplitResultsGroup> {
    val athletesGroups = dataCheckpoints.groupBy { athlete -> athlete.group }
    val splitProtocols = (athletesGroups.map { (group, athletesGroup) ->
        Pair(group, SplitResultsGroup(group, generateSplitResultsGroup(AthletesGroup(group, athletesGroup))))
    }).toMap()
    return splitProtocols
}