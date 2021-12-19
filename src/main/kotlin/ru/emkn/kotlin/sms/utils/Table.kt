package ru.emkn.kotlin.sms.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.DISTANCE_CRITERIA
import ru.emkn.kotlin.sms.GROUP_DISTANCES

@Composable
fun TableForItemInformationList(typeItem: TypeItemInformationList, surfaceGradient: Brush) {
    //
    Box(Modifier.background(surfaceGradient)) {
        when (typeItem) {
            TypeItemInformationList.ITEM_GROUPS -> showGroups()
            TypeItemInformationList.ITEM_DISTANCES -> showDistances()
        }
    }

}

@Composable
fun showGroups() {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        val columnWeight = .5f
        item {
            Row(Modifier.background(Color.Gray)) {
                TableCell(text = "Group", weight = columnWeight)
                TableCell(text = "Distance", weight = columnWeight)
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
fun showDistances() {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        val columnWeight = .25f
        item {
            Row(Modifier.background(Color.Gray)) {
                TableCell(text = "Group", weight = columnWeight)
                TableCell(text = "Type", weight = columnWeight)
                TableCell(text = "Amount checkpoints", weight = columnWeight)
                TableCell(text = "Checkpoints", weight = columnWeight)
            }
        }
        items(DISTANCE_CRITERIA.toList()) {
            val (distance, criteria) = it
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = distance, weight = columnWeight)
                TableCell(text = criteria.distanceType.name, weight = columnWeight)
                TableCell(text = criteria.distanceType.name, weight = columnWeight)
                TableCell(text = criteria.distanceType.name, weight = columnWeight)
            }
        }
    }
}
//
//
//@Composable
//fun LazyListScope.showTeams() {
//    item {
//        Row(Modifier.background(Color.Gray)) {
//            TableCell(text = "Column 1", weight = column1Weight)
//            TableCell(text = "Column 2", weight = column2Weight)
//        }
//    }
//    items(tableData) {
//        val (id, text) = it
//        Row(Modifier.fillMaxWidth()) {
//            TableCell(text = id.toString(), weight = column1Weight)
//            TableCell(text = text, weight = column2Weight)
//        }
//    }
//}
//
//
//@Composable
//fun LazyListScope.showCompetitors() {
//    item {
//        Row(Modifier.background(Color.Gray)) {
//            TableCell(text = "Column 1", weight = column1Weight)
//            TableCell(text = "Column 2", weight = column2Weight)
//        }
//    }
//    items(tableData) {
//        val (id, text) = it
//        Row(Modifier.fillMaxWidth()) {
//            TableCell(text = id.toString(), weight = column1Weight)
//            TableCell(text = text, weight = column2Weight)
//        }
//    }
//}
//
//@Composable
//fun LazyListScope.showCheckpoints() {
//    item {
//        Row(Modifier.background(Color.Gray)) {
//            TableCell(text = "Column 1", weight = column1Weight)
//            TableCell(text = "Column 2", weight = column2Weight)
//        }
//    }
//    items(tableData) {
//        val (id, text) = it
//        Row(Modifier.fillMaxWidth()) {
//            TableCell(text = id.toString(), weight = column1Weight)
//            TableCell(text = text, weight = column2Weight)
//        }
//    }
//}


@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier.border(1.dp, Color.Black).weight(weight).padding(8.dp)
    )
}