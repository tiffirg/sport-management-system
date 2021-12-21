package ru.emkn.kotlin.sms.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.*
import ru.emkn.kotlin.sms.classes.*
import ru.emkn.kotlin.sms.gui.ApplicationWindowState
import ru.emkn.kotlin.sms.gui.CurrentTabStatus
import ru.emkn.kotlin.sms.gui.Stage
import ru.emkn.kotlin.sms.services.CheckpointRecord
import java.time.LocalTime

@Composable
fun TableForItemInformationList(
    state: ApplicationWindowState,
    addButtonState: MutableState<Boolean>,
    typeItem: TypeItemInformationList,
    surfaceGradient: Brush
) {
    if (state.stage == Stage.CONFIG && DB.checkStartsProtocols(COMPETITION_ID) && state.stage != Stage.RESULTS) {
        state.stage = Stage.START_PROTOCOLS
    }
    Column {
        when (typeItem) {
            TypeItemInformationList.ITEM_GROUPS -> showGroups(addButtonState, surfaceGradient)
            TypeItemInformationList.ITEM_DISTANCES -> showDistances(addButtonState, surfaceGradient)
            TypeItemInformationList.ITEM_TEAMS -> showTeams(addButtonState, surfaceGradient)
            TypeItemInformationList.ITEM_COMPETITORS -> showCompetitors(addButtonState, surfaceGradient)
            TypeItemInformationList.ITEM_CHECKPOINTS -> when (state.stage) {
                Stage.START_PROTOCOLS, Stage.RESULTS -> showCheckpoints(addButtonState, surfaceGradient)
                else -> CurrentTabStatus("It is required to fill in data by columns: Team, Competitors")
            }
        }
    }
}

@Composable
fun showGroups(addButtonState: MutableState<Boolean>, surfaceGradient: Brush) {
    val columnWeight = .5f
    val group = remember { mutableStateOf("") }
    val distance = remember { mutableStateOf("") }
    Row(Modifier.fillMaxWidth()) {
        TableAddCell(group, weight = .5f)
        TableAddCell(distance, weight = .5f)
    }
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "Group", weight = columnWeight)
                    TableHeaderCell(text = "Distance", weight = columnWeight)
                }
            }
            items(GROUP_DISTANCES.toList()) {
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = it.first, weight = columnWeight)
                    TableCell(text = it.second, weight = columnWeight)
                }
            }
        }
    }
    if (addButtonState.value) {
        if (DB.insertGroupOf(group.value, distance.value)) {
            GROUP_NAMES.add(group.value)
            GROUP_DISTANCES[group.value] = distance.value
        }
        addButtonState.value = false
    }
}

@Composable
fun showDistances(addButtonState: MutableState<Boolean>, surfaceGradient: Brush) {
    val distanceColumnWeight = .2f
    val columnWeight = .15f
    val checkpointsColumnWeight = .5f

    val distance = remember { mutableStateOf("") }
    val distanceType = remember { mutableStateOf("") }
    val amountCheckpoints = remember { mutableStateOf("") }
    val checkpoints = remember { mutableStateOf("") }
    Row(Modifier.fillMaxWidth()) {
        TableAddCell(distance, weight = .5f)
        TableAddCell(distanceType, weight = .5f)
        TableAddCell(amountCheckpoints, weight = .5f)
        TableAddCell(checkpoints, weight = .5f)
    }
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {

            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "distance", weight = distanceColumnWeight)
                    TableHeaderCell(text = "Type", weight = columnWeight)
                    TableHeaderCell(text = "Amount checkpoints", weight = columnWeight)
                    TableHeaderCell(text = "Checkpoints", weight = checkpointsColumnWeight)
                }
            }
            items(DISTANCE_CRITERIA.toList()) { (distance, criteria) ->
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = distance, weight = distanceColumnWeight)
                    TableCell(text = criteria.distanceType.name, weight = columnWeight)
                    TableCell(text = criteria.checkpointsCount.toString(), weight = columnWeight)
                    TableCell(text = criteria.checkpointsOrder.joinToString(), weight = checkpointsColumnWeight)
                }
            }
        }
    }
    if (addButtonState.value) {
        val type = DistanceType.valueOf(distanceType.value)
        val checkpointsList = checkpoints.value.split(", ")
        if (DB.insertDistanceOf(distance.value, type, amountCheckpoints.value.toInt(), checkpointsList)) {
            DISTANCE_CRITERIA[distance.value] = when (DistanceType.valueOf(distanceType.value)) {
                DistanceType.FIXED -> FixedRoute(checkpointsList)
                DistanceType.CHOICE -> ChoiceRoute(amountCheckpoints.value.toInt(), checkpointsList)
            }

        }
        addButtonState.value = false
    }
}

@Composable
fun showTeams(addButtonState: MutableState<Boolean>, surfaceGradient: Brush) {
    val columnWeight = .5f
    val teams = remember { DB.getTeams()?.map { it.team }?.toMutableStateList() ?: mutableStateListOf() }
    val team = remember { mutableStateOf("") }
    val info = remember { mutableStateOf("") }
    Row(Modifier.fillMaxWidth()) {
        TableAddCell(team, weight = columnWeight)
        TableAddCell(info, weight = columnWeight)
    }
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "Team", weight = columnWeight)
                    TableHeaderCell(text = "Info", weight = columnWeight)
                }
            }
            items(teams) { team ->
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = team, weight = columnWeight)
                    TableCell(text = "null", weight = columnWeight)
                }
            }
        }
    }
    if (addButtonState.value) {
        if (DB.insertTeamOf(team.value)) {
            teams.add(team.value)
        }
        addButtonState.value = false
    }
}


@Composable
fun showCompetitors(addButtonState: MutableState<Boolean>, surfaceGradient: Brush) {
    val columnWeight = .15f
    val competitors = remember { DB.getAthletes()?.toMutableStateList() ?: mutableStateListOf() }
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val birthYear = remember { mutableStateOf("") }
    val group = remember { mutableStateOf("") }
    val rank = remember { mutableStateOf("") }
    val team = remember { mutableStateOf("") }
    Row(Modifier.fillMaxWidth()) {
        TableAddCell(surname, weight = columnWeight)
        TableAddCell(name, weight = columnWeight)
        TableAddCell(birthYear, weight = columnWeight)
        TableAddCell(group, weight = columnWeight)
        TableAddCell(rank, weight = columnWeight)
        TableAddCell(team, weight = columnWeight)
    }
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {

            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "Surname", weight = columnWeight)
                    TableHeaderCell(text = "Name", weight = columnWeight)
                    TableHeaderCell(text = "Birth year", weight = columnWeight)
                    TableHeaderCell(text = "Group", weight = columnWeight)
                    TableHeaderCell(text = "Rank", weight = columnWeight)
                    TableHeaderCell(text = "Team", weight = columnWeight)
                }
            }
            items(competitors) { (_, competitor) ->
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = competitor.surname, weight = columnWeight)
                    TableCell(text = competitor.name, weight = columnWeight)
                    TableCell(text = competitor.birthYear.toString(), weight = columnWeight)
                    TableCell(text = competitor.group.groupName, weight = columnWeight)
                    TableCell(text = competitor.rank.rankName ?: "null", weight = columnWeight)
                    TableCell(text = competitor.teamName, weight = columnWeight)
                }
            }
        }
    }
    if (addButtonState.value) {
        DB.deleteCompetitors()
        val athlete = Athlete(
            surname.value,
            name.value,
            birthYear.value.toInt(),
            Group(group.value),
            Rank(rank.value),
            team.value
        )
        val athleteId = DB.insertAthleteOf(athlete)
        if (athleteId != null) {
            competitors.add(athleteId to athlete)
        }
        addButtonState.value = false
    }
}

@Composable
fun showCheckpoints(addButtonState: MutableState<Boolean>, surfaceGradient: Brush) {
    val columnWeight = .33f
    LOGGER.debug { "Use getCheckpoints | ${DB.getCheckpoints()}" }
    val checkpoints = remember { DB.getCheckpoints()?.toMutableStateList() ?: mutableStateListOf() }
    val competitionNumber = remember { mutableStateOf("") }
    val checkpoint = remember { mutableStateOf("") }
    val timeMeasurement = remember { mutableStateOf("") }
    Row(Modifier.fillMaxWidth()) {
        TableAddCell(competitionNumber, weight = columnWeight)
        TableAddCell(checkpoint, weight = columnWeight)
        TableAddCell(timeMeasurement, weight = columnWeight)
    }
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {

            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "Competitor Number", weight = columnWeight)
                    TableHeaderCell(text = "Checkpoint", weight = columnWeight)
                    TableHeaderCell(text = "Time Measurement", weight = columnWeight)
                }
            }
            items(checkpoints) {
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = it.competitorNumber.toString(), weight = columnWeight)
                    TableCell(text = it.checkpoint, weight = columnWeight)
                    TableCell(text = it.timeMeasurement.format(TimeFormatter), weight = columnWeight)
                }
            }
        }
    }
    if (addButtonState.value) {
        val checkpointRecord = CheckpointRecord(
            competitionNumber.value.toInt(),
            checkpoint.value,
            LocalTime.parse(timeMeasurement.value, TimeFormatter)
        )
        val result = DB.insertCheckpointOf(checkpointRecord)
        LOGGER.debug { "Use insertCheckpointOf | $result" }
        if (result) {
            checkpoints.add(checkpointRecord)
        }
        addButtonState.value = false
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    val cellText = remember { mutableStateOf(text) }
    TextField(
        value = cellText.value,
        onValueChange = {
            cellText.value = it
        },
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(8.dp)
    )
}

@Composable
fun RowScope.TableAddCell(
    text: MutableState<String>,
    weight: Float

) {
    TextField(
        value = text.value,
        onValueChange = {
            text.value = it
        },
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(8.dp)
    )
}

@Composable
fun RowScope.TableHeaderCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        modifier = Modifier.border(1.dp, Color.Black).weight(weight).padding(8.dp)
    )
}