package ru.emkn.kotlin.sms.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TableForStartProtocols(surfaceGradient: Brush) {
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            val columnWeight = .5f
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "Athlete Number", weight = columnWeight)
                    TableHeaderCell(text = "Start Time", weight = columnWeight)
                    TableHeaderCell(text = "Surname", weight = columnWeight)
                    TableHeaderCell(text = "Name", weight = columnWeight)
                    TableHeaderCell(text = "Birth year", weight = columnWeight)
                    TableHeaderCell(text = "Rank", weight = columnWeight)
                    TableHeaderCell(text = "Group", weight = columnWeight)
                    TableHeaderCell(text = "Team", weight = columnWeight)
                }
            }
//            items(GROUP_DISTANCES.toList()) {
//                val (group, distance) = it
//                Row(Modifier.fillMaxWidth()) {
//                    TableCell(text = group, weight = columnWeight)
//                    TableCell(text = distance, weight = columnWeight)
//                }
//            }
        }
    }
}
