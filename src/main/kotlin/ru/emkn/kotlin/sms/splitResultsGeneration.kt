package ru.emkn.kotlin.sms

import ru.emkn.kotlin.sms.classes.*


fun getAthleteSplit(athlete: Athlete): List<CheckpointTime>? {
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
                splits.add(CheckpointTime(firstCheckpoint.checkpoint, firstCheckpoint.time.minus(athlete.startTime)))
            } else {
                val currCheckpoint = athlete.checkpoints!![index]
                val prevCheckpoint = athlete.checkpoints!![index - 1]
                splits.add(CheckpointTime(currCheckpoint.checkpoint, currCheckpoint.time.minus(prevCheckpoint.time)))
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


fun generateSplitResults(dataCheckpoints: List<Athlete>): Map<Group, SplitResultsGroup> {
    val athletesGroups = dataCheckpoints.groupBy { athlete -> athlete.group }
    val splitProtocols = (athletesGroups.map { (group, athletesGroup) ->
        Pair(group, SplitResultsGroup(group, generateSplitResultsGroup(AthletesGroup(group, athletesGroup))))
    }).toMap()
    splitProtocols.toSortedMap(compareBy { it.groupName })
    return splitProtocols
}