package ru.emkn.kotlin.sms.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.DB
import ru.emkn.kotlin.sms.DISTANCE_CRITERIA
import ru.emkn.kotlin.sms.GROUP_DISTANCES
import ru.emkn.kotlin.sms.gui.ApplicationWindowState

@Composable
fun TableForItemInformationList(
    state: ApplicationWindowState,
    addButtonState: MutableState<Boolean>,
    typeItem: TypeItemInformationList,
    surfaceGradient: Brush
) {
    Column {
        when (typeItem) {
            TypeItemInformationList.ITEM_GROUPS -> showGroups(state, addButtonState, surfaceGradient)
            TypeItemInformationList.ITEM_DISTANCES -> showDistances()
            TypeItemInformationList.ITEM_TEAMS -> {}
            TypeItemInformationList.ITEM_COMPETITORS -> {}
            TypeItemInformationList.ITEM_CHECKPOINTS -> {}
        }
    }
}

@Composable
fun showGroups(state: ApplicationWindowState, addButtonState: MutableState<Boolean>, surfaceGradient: Brush) {
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
        DB.insertGroupOf(group.value, distance.value)
    }
}

@Composable
fun showDistances() {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        val distanceColumnWeight = .2f
        val columnWeight = .15f
        val checkpointsColumnWeight = .5f
        item {
            Row(Modifier.background(Color.Gray)) {
                TableHeaderCell(text = "Group", weight = distanceColumnWeight)
                TableHeaderCell(text = "Type", weight = columnWeight)
                TableHeaderCell(text = "Amount checkpoints", weight = columnWeight)
                TableHeaderCell(text = "Checkpoints", weight = checkpointsColumnWeight)
            }
        }
        items(DISTANCE_CRITERIA.toList()) {
            val (distance, criteria) = it
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = distance, weight = distanceColumnWeight)
                TableCell(text = criteria.distanceType.value, weight = columnWeight)
                TableCell(text = criteria.checkpointsCount.toString(), weight = columnWeight)
                TableCell(text = criteria.checkpointsOrder.joinToString(), weight = checkpointsColumnWeight)
            }
        }
    }
}


@Composable
fun LazyListScope.showTeams() {
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


@Composable
fun LazyListScope.showCompetitors() {
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

@Composable
fun LazyListScope.showCheckpoints() {
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