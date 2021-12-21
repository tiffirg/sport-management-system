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
import ru.emkn.kotlin.sms.classes.*

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
fun TableForGroupResults(resultsCompetitors: List<CompetitorResultInGroup>, surfaceGradient: Brush) {
    val columnWeight = .1f
    val competitors = remember { resultsCompetitors.toMutableStateList() }
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "Place", weight = columnWeight)
                    TableHeaderCell(text = "Number", weight = columnWeight)
                    TableHeaderCell(text = "Surname", weight = columnWeight)
                    TableHeaderCell(text = "Name", weight = columnWeight)
                    TableHeaderCell(text = "Birth year", weight = columnWeight)
                    TableHeaderCell(text = "Rank", weight = columnWeight)
                    TableHeaderCell(text = "Group", weight = columnWeight)
                    TableHeaderCell(text = "Team", weight = columnWeight)
                    TableHeaderCell(text = "Result", weight = columnWeight)
                    TableHeaderCell(text = "Backlog", weight = columnWeight)
                }
            }
            items(competitors) { resultsCompetitor ->
                Row(Modifier.fillMaxWidth()) {
                    TableHeaderCell(text = resultsCompetitor.place?.toString() ?: "-", weight = columnWeight)
                    TableHeaderCell(text = resultsCompetitor.competitor.athleteNumber.toString(), weight = columnWeight)
                    TableHeaderCell(text = resultsCompetitor.competitor.surname, weight = columnWeight)
                    TableHeaderCell(text = resultsCompetitor.competitor.name, weight = columnWeight)
                    TableHeaderCell(text = resultsCompetitor.competitor.birthYear.toString(), weight = columnWeight)
                    TableHeaderCell(text = resultsCompetitor.competitor.rank.rankName ?: "-", weight = columnWeight)
                    TableHeaderCell(text = resultsCompetitor.competitor.group.groupName, weight = columnWeight)
                    TableHeaderCell(text = resultsCompetitor.competitor.teamName, weight = columnWeight)
                    TableHeaderCell(text = resultsCompetitor.result?.toResultFormat() ?: "removed", weight = columnWeight)
                    TableHeaderCell(text = resultsCompetitor.backlog.toBacklogFormat(), weight = columnWeight)
                }
            }
        }
    }
}

@Composable
fun TableForGroupSplitResults(resultsCompetitors: List<CompetitorSplitResultInGroup>, surfaceGradient: Brush) {
    val columnWeight = .05f
    val checkpointsColumnWeight = .5f
    val competitors = remember { resultsCompetitors.toMutableStateList() }
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "Place", weight = columnWeight)
                    TableHeaderCell(text = "Competition Number", weight = columnWeight)
                    TableHeaderCell(text = "Surname", weight = columnWeight)
                    TableHeaderCell(text = "Name", weight = columnWeight)
                    TableHeaderCell(text = "Birth year", weight = columnWeight)
                    TableHeaderCell(text = "Rank", weight = columnWeight)
                    TableHeaderCell(text = "Group", weight = columnWeight)
                    TableHeaderCell(text = "Team", weight = columnWeight)
                    TableHeaderCell(text = "Result", weight = columnWeight)
                    TableHeaderCell(text = "Backlog", weight = columnWeight)
                    TableHeaderCell(text = "Checkpoints", weight = checkpointsColumnWeight)
                }
            }
            items(competitors) { res ->
                Row(Modifier.fillMaxWidth()) {
                    TableHeaderCell(text = res.competitorResultInGroup.place?.toString() ?: "-", weight = columnWeight)
                    TableHeaderCell(text = res.competitorResultInGroup.competitor.athleteNumber.toString(), weight = columnWeight)
                    TableHeaderCell(text = res.competitorResultInGroup.competitor.surname, weight = columnWeight)
                    TableHeaderCell(text = res.competitorResultInGroup.competitor.name, weight = columnWeight)
                    TableHeaderCell(text = res.competitorResultInGroup.competitor.birthYear.toString(), weight = columnWeight)
                    TableHeaderCell(text = res.competitorResultInGroup.competitor.rank.rankName ?: "-", weight = columnWeight)
                    TableHeaderCell(text = res.competitorResultInGroup.competitor.group.groupName, weight = columnWeight)
                    TableHeaderCell(text = res.competitorResultInGroup.competitor.teamName, weight = columnWeight)
                    TableHeaderCell(text = res.competitorResultInGroup.result?.toResultFormat() ?: "removed", weight = columnWeight)
                    TableHeaderCell(text = res.competitorResultInGroup.backlog.toBacklogFormat(), weight = columnWeight)
                    TableHeaderCell(text = res.splits?.joinToString { "${it.checkpoint} ${it.duration.toResultFormat() }" } ?: " ", weight = checkpointsColumnWeight)
                }
            }
        }
    }
}

@Composable
fun TableForTeamResults(resultsCompetitors: List<CompetitorResultInTeam>, surfaceGradient: Brush) {
    val columnWeight = .1f
    val competitors = remember { resultsCompetitors.toMutableStateList() }
    Box(Modifier.background(surfaceGradient)) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Row(Modifier.background(Color.Gray)) {
                    TableHeaderCell(text = "Place", weight = columnWeight)
                    TableHeaderCell(text = "Score", weight = columnWeight)
                    TableHeaderCell(text = "Competition number", weight = columnWeight)
                    TableHeaderCell(text = "Surname", weight = columnWeight)
                    TableHeaderCell(text = "Name", weight = columnWeight)
                    TableHeaderCell(text = "Birth year", weight = columnWeight)
                    TableHeaderCell(text = "Rank", weight = columnWeight)
                    TableHeaderCell(text = "Group", weight = columnWeight)
                    TableHeaderCell(text = "Team", weight = columnWeight)
                }
            }
            items(competitors) { CompetitorResults ->
                Row(Modifier.fillMaxWidth()) {
                    TableHeaderCell(text = CompetitorResults.place?.toString() ?: "-", weight = columnWeight)
                    TableHeaderCell(text = CompetitorResults.score.toString(), weight = columnWeight)
                    TableHeaderCell(text = CompetitorResults.competitor.athleteNumber.toString(), weight = columnWeight)
                    TableHeaderCell(text = CompetitorResults.competitor.surname, weight = columnWeight)
                    TableHeaderCell(text = CompetitorResults.competitor.name, weight = columnWeight)
                    TableHeaderCell(text = CompetitorResults.competitor.birthYear.toString(), weight = columnWeight)
                    TableHeaderCell(text = CompetitorResults.competitor.rank.rankName ?: "-", weight = columnWeight)
                    TableHeaderCell(text = CompetitorResults.competitor.group.groupName, weight = columnWeight)
                    TableHeaderCell(text = CompetitorResults.competitor.teamName, weight = columnWeight)
                }
            }
        }
    }
}