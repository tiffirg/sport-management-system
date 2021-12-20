package ru.emkn.kotlin.sms.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.emkn.kotlin.sms.DB
import ru.emkn.kotlin.sms.TimeFormatter
import ru.emkn.kotlin.sms.classes.Competitor

@Composable
fun TableForStartProtocols(competitorsList: List<Pair<Int, Competitor>>, surfaceGradient: Brush) {
    val columnWeight = .12f
    val competitors = remember { competitorsList.toMutableStateList() }
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "Competition Number", weight = columnWeight)
                    TableHeaderCell(text = "Start Time", weight = columnWeight)
                    TableHeaderCell(text = "Surname", weight = columnWeight)
                    TableHeaderCell(text = "Name", weight = columnWeight)
                    TableHeaderCell(text = "Birth year", weight = columnWeight)
                    TableHeaderCell(text = "Rank", weight = columnWeight)
                    TableHeaderCell(text = "Group", weight = columnWeight)
                    TableHeaderCell(text = "Team", weight = columnWeight)
                }
            }
            items(competitors) {
                val (_, competitor) = it
                Row(Modifier.fillMaxWidth()) {
                    TableHeaderCell(text = competitor.athleteNumber.toString(), weight = columnWeight)
                    TableHeaderCell(text = competitor.startTime.format(TimeFormatter), weight = columnWeight)
                    TableHeaderCell(text = competitor.name, weight = columnWeight)
                    TableHeaderCell(text = competitor.surname, weight = columnWeight)
                    TableHeaderCell(text = competitor.birthYear.toString(), weight = columnWeight)
                    TableHeaderCell(text = competitor.rank.rankName ?: "", weight = columnWeight)
                    TableHeaderCell(text = competitor.group.groupName, weight = columnWeight)
                    TableHeaderCell(text = competitor.teamName, weight = columnWeight)
                }
            }
        }
    }
}

@Composable
fun TableForGroupResults(surfaceGradient: Brush) {
}

@Composable
fun TableForTeamResults(surfaceGradient: Brush) {
}