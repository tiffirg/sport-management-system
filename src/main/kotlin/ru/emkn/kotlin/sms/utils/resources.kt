package ru.emkn.kotlin.sms.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import ru.emkn.kotlin.sms.gui.ApplicationWindowState

fun getAppResources(): AppResources {
    return AppResources("Application \"Competition\" for Desktop")
}

val LocalAppResources = staticCompositionLocalOf<AppResources> {
    error("LocalAppResources isn't provided")
}

data class AppResources(val titleApp: String)


enum class TypeItemTab(val itemTab: ItemTab) {
    START_PROTOCOLS(ItemTabStartProtocols()), GROUP_RESULTS(ItemTabGroupResults()), TEAM_RESULTS(ItemTabTeamResults())
}

// Function References of @Composable functions are not currently supported
abstract class ItemTab {
    abstract val title: String
    abstract val icon: ImageVector
}

abstract class ItemInformationList {
    abstract val title: String
}

class ItemTabStartProtocols : ItemTab() {
    override val title = "Start protocols"
    override val icon = Icons.Default.Add
}

class ItemTabGroupResults : ItemTab() {
    override val title = "Group results"
    override val icon = Icons.Default.Add
}

class ItemTabTeamResults : ItemTab() {
    override val title = "Team results"
    override val icon = Icons.Default.Add
}

class ItemGroups : ItemInformationList() {
    override val title = "Группы"
}

class ItemDistances : ItemInformationList() {
    override val title = "Дистанции"
}

class ItemTeams : ItemInformationList() {
    override val title = "Команды"
}

class ItemCompetitors : ItemInformationList() {
    override val title = "Участники"
}

class ItemCheckpoints : ItemInformationList() {
    override val title = "Контрольные пункты"
}

enum class TypeItemInformationList(val item: ItemInformationList) {
    ITEM_DISTANCES(ItemDistances()),
    ITEM_GROUPS(ItemGroups()),
    ITEM_TEAMS(ItemTeams()),
    ITEM_COMPETITORS(ItemCompetitors()),
    ITEM_CHECKPOINTS(ItemCheckpoints())
}
