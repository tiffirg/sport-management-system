package ru.emkn.kotlin.sms.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.DB
import ru.emkn.kotlin.sms.DISTANCE_CRITERIA
import ru.emkn.kotlin.sms.GROUP_DISTANCES
import ru.emkn.kotlin.sms.GROUP_NAMES
import ru.emkn.kotlin.sms.classes.Athlete
import ru.emkn.kotlin.sms.classes.ChoiceRoute
import ru.emkn.kotlin.sms.classes.DistanceType
import ru.emkn.kotlin.sms.classes.FixedRoute

@Composable
fun TableForItemInformationList(
    addButtonState: MutableState<Boolean>,
    typeItem: TypeItemInformationList,
    surfaceGradient: Brush
) {
    Column {
        when (typeItem) {
            TypeItemInformationList.ITEM_GROUPS -> showGroups(addButtonState, surfaceGradient)
            TypeItemInformationList.ITEM_DISTANCES -> showDistances(addButtonState, surfaceGradient)
            TypeItemInformationList.ITEM_TEAMS -> showTeams(addButtonState, surfaceGradient)
            TypeItemInformationList.ITEM_COMPETITORS -> showCompetitors(addButtonState, surfaceGradient)
            TypeItemInformationList.ITEM_CHECKPOINTS -> showCheckpoints(addButtonState, surfaceGradient)
        }
    }
}

@Composable
fun showGroups(addButtonState: MutableState<Boolean>, surfaceGradient: Brush) {
    val group = remember { mutableStateOf("") }
    val distance = remember { mutableStateOf("") }
    Row(Modifier.fillMaxWidth()) {
        TableAddCell(group, weight = .5f)
        TableAddCell(distance, weight = .5f)
    }
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            val columnWeight = .5f
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "Group", weight = columnWeight)
                    TableHeaderCell(text = "Distance", weight = columnWeight)
                }
            }
            items(GROUP_DISTANCES.toList()) {
                val (group, distance) = it
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = group, weight = columnWeight)
                    TableCell(text = distance, weight = columnWeight)
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
            items(DISTANCE_CRITERIA.toList()) {
                val (distance, criteria) = it
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
    val teams = remember { mutableStateListOf<String>() }
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
            TODO()
        }
        addButtonState.value = false
    }
}


@Composable
fun showCompetitors(addButtonState: MutableState<Boolean>, surfaceGradient: Brush) {
    val columnWeight = .15f
    val competitors = remember { mutableStateListOf<Athlete>() }
    val name = remember { mutableStateOf("") }
    val surname = remember { mutableStateOf("") }
    val birthYear = remember { mutableStateOf("") }
    val group = remember { mutableStateOf("") }
    val rank = remember { mutableStateOf("") }
    val team = remember { mutableStateOf("") }
    Row(Modifier.fillMaxWidth()) {
        TableAddCell(name, weight = columnWeight)
        TableAddCell(surname, weight = columnWeight)
        TableAddCell(birthYear, weight = columnWeight)
        TableAddCell(group, weight = columnWeight)
        TableAddCell(rank, weight = columnWeight)
        TableAddCell(team, weight = columnWeight)
    }
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {

            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "Name", weight = columnWeight)
                    TableHeaderCell(text = "Surname", weight = columnWeight)
                    TableHeaderCell(text = "Birth Year", weight = columnWeight)
                    TableHeaderCell(text = "Group", weight = columnWeight)
                    TableHeaderCell(text = "Rank", weight = columnWeight)
                    TableHeaderCell(text = "Team", weight = columnWeight)
                }
            }
            items(competitors) { competitor ->
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = competitor.name, weight = columnWeight)
                    TableCell(text = competitor.surname, weight = columnWeight)
                    TableCell(text = competitor.birthYear.toString(), weight = columnWeight)
                    TableCell(text = competitor.group.groupName, weight = columnWeight)
                    TableCell(text = competitor.rank.rankName?: "null", weight = columnWeight)
                    TableCell(text = competitor.teamName, weight = columnWeight)
                }
            }
        }
    }
    if (addButtonState.value) {
        if (DB.insertTeamOf(team.value)) {
            TODO()
        }
        addButtonState.value = false
    }
}

@Composable
fun showCheckpoints(addButtonState: MutableState<Boolean>, surfaceGradient: Brush) {
    val columnWeight = .15f
    val competitors = remember { mutableStateListOf<Athlete>() }
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
                    TableHeaderCell(text = "Name", weight = columnWeight)
                    TableHeaderCell(text = "Surname", weight = columnWeight)
                    TableHeaderCell(text = "Birth Year", weight = columnWeight)
                    TableHeaderCell(text = "Group", weight = columnWeight)
                    TableHeaderCell(text = "Rank", weight = columnWeight)
                    TableHeaderCell(text = "Team", weight = columnWeight)
                }
            }
            items(competitors) { competitor ->
                Row(Modifier.fillMaxWidth()) {
                    TableCell(text = competitor.name, weight = columnWeight)
                    TableCell(text = competitor.surname, weight = columnWeight)
                    TableCell(text = competitor.birthYear.toString(), weight = columnWeight)
                    TableCell(text = competitor.group.groupName, weight = columnWeight)
                    TableCell(text = competitor.rank.rankName?: "null", weight = columnWeight)
                    TableCell(text = competitor.teamName, weight = columnWeight)
                }
            }
        }
    }
    if (addButtonState.value) {
        if (DB.insertTeamOf(team.value)) {
            TODO()
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