package ru.emkn.kotlin.sms.gui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import kotlinx.coroutines.launch
import ru.emkn.kotlin.sms.COMPETITION_ID
import ru.emkn.kotlin.sms.DB
import ru.emkn.kotlin.sms.services.CommandsHandler
import ru.emkn.kotlin.sms.utils.*
import ru.emkn.kotlin.sms.utils.TypeItemTab.*


@Composable
fun ApplicationWindow(state: ApplicationWindowState) {
    Window(
        state = state.window,
        title = LocalAppResources.current.titleApp,
        onCloseRequest = state.exit
    ) {
        val darkTheme = remember { mutableStateOf(false) }
        MaterialTheme(colors = if (darkTheme.value) DarkGreenColorPalette else LightGreenColorPalette) {
            WindowMenuBar(state)
            WindowLayout(state, darkTheme)

            if (state.stateOpenFileDialog.isAwaiting) {
                FileDialog(
                    onResult = {
                        state.stateOpenFileDialog.onResult(it)
                    }
                )
            }
            if (state.stateOpenWarningDialog.isAwaiting) {
                AlertDialog(
                    message = state.stateOpenWarningDialog.message,
                    onResult = {
                        state.stateOpenWarningDialog.onResult(it)
                    }
                )
            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun FrameWindowScope.WindowMenuBar(state: ApplicationWindowState) {
    val scope = rememberCoroutineScope()

    fun open() = scope.launch { state.openFileDialog() }
    MenuBar {
        Menu("File", mnemonic = 'F') {
            Item(
                text = "Open",
                onClick = { open() },
                shortcut = KeyShortcut(Key.A, ctrl = true)
            )
            Separator()
            Item(
                text = "Exit",
                onClick = state.exit,
                shortcut = KeyShortcut(Key.Escape, ctrl = true)
            )
        }
    }
}

@Composable
private fun WindowLayout(state: ApplicationWindowState, darkTheme: MutableState<Boolean>) {
    val itemTabState: MutableState<TypeItemTab?> = remember { mutableStateOf(START_PROTOCOLS) }
    val itemInformationListState: MutableState<TypeItemInformationList?> = remember { mutableStateOf(null) }
    Box {
        Row {
            SideBar(darkTheme, itemTabState, itemInformationListState)
            BodyContent(state, itemTabState, itemInformationListState)
        }
    }
}

@Composable
fun SideBar(
    darkTheme: MutableState<Boolean>,
    itemTabState: MutableState<TypeItemTab?>,
    itemInformationListState: MutableState<TypeItemInformationList?>
) {
    val selectedIndex: MutableState<Int?> = remember { mutableStateOf(null) }

    Column(
        modifier = Modifier.fillMaxHeight().requiredWidth(250.dp).background(MaterialTheme.colors.surface)
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable { darkTheme.value = !darkTheme.value }.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Toggle Theme",
                style = MaterialTheme.typography.h6.copy(fontSize = 14.sp),
                color = MaterialTheme.colors.onSurface
            )
            if (darkTheme.value) {
                Icon(imageVector = Icons.Default.Star, tint = Color.Yellow, contentDescription = "For theme")
            } else {
                Icon(
                    imageVector = Icons.Default.Star,
                    tint = MaterialTheme.colors.onSurface,
                    contentDescription = "For theme"
                )
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        for (tabType in TypeItemTab.values()) {
            SideBarNavItem(
                tabType.itemTab.title, tabType.itemTab.icon, itemTabState.value == tabType
            ) {
                itemTabState.value = tabType
                itemInformationListState.value = null
                selectedIndex.value = null
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        InformationListSideBar(selectedIndex.value) {
            itemInformationListState.value = TypeItemInformationList.values()[it]
            selectedIndex.value = it
            itemTabState.value = null
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SideBarNavItem(title: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    val animatedBackgroundColor =
        animateColorAsState(if (selected) MaterialTheme.colors.secondary else MaterialTheme.colors.surface)
    val animatedContentColor =
        animateColorAsState(if (selected) MaterialTheme.colors.onSurface else MaterialTheme.colors.onSecondary)
    Row(
        modifier = Modifier
            .fillMaxWidth().background(animatedBackgroundColor.value).clip(RoundedCornerShape(4.dp)).padding(16.dp)
            .clickable {
                onClick.invoke()
            }
    ) {
        Icon(imageVector = icon, tint = animatedContentColor.value, contentDescription = "For Tabs")
        Text(
            title, style = MaterialTheme.typography.body1,
            color = animatedContentColor.value,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun InformationListSideBar(selectedIndex: Int?, onPlayListSelected: (Int) -> Unit) {
    Text(
        "Information",
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
        color = MaterialTheme.colors.onSurface
    )
    LazyColumn {
        itemsIndexed(TypeItemInformationList.values()) { index, itemData ->
            Text(
                itemData.item.title,
                modifier = Modifier.padding(8.dp).clickable { onPlayListSelected.invoke(index) },
                color = animateColorAsState(
                    if (index == selectedIndex) MaterialTheme.colors.onSurface else MaterialTheme.colors.onSecondary.copy(
                        alpha = 0.7f
                    )
                ).value,
                style = if (index == selectedIndex) MaterialTheme.typography.h6 else MaterialTheme.typography.body1
            )
        }
    }
}


@Composable
fun BodyContent(
    state: ApplicationWindowState,
    itemTabState: MutableState<TypeItemTab?>,
    itemInformationListState: MutableState<TypeItemInformationList?>
) {
    if (state.stage == Stage.NO_CONFIG) {
        CurrentTabStatus("Select config")
    } else {
        val itemInformationList = itemInformationListState.value
        if (itemInformationList != null) {
            Crossfade(targetState = itemInformationListState) {
                ContentItemInformation(state, itemInformationList)
            }
        } else {
            Crossfade(targetState = itemTabState) {
                when {
                    state.stage == Stage.CONFIG -> CurrentTabStatus("Load Data")
                    itemTabState.value == START_PROTOCOLS -> ContentStartsProtocols(state)
                    itemTabState.value == GROUP_RESULTS -> ContentGroupResults(state)
                    itemTabState.value == TEAM_RESULTS -> ContentTeamResults(state)
                }
            }
        }
    }
}

@Composable
fun CurrentTabStatus(message: String) {
    val surfaceGradient =
        Brush.horizontalGradient(colors = listOf(MaterialTheme.colors.secondary, MaterialTheme.colors.surface))
    Box(
        modifier = Modifier.background(surfaceGradient).padding(8.dp).fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = MaterialTheme.colors.onBackground)
    }
}

@Composable
fun ContentStartsProtocols(state: ApplicationWindowState) {
    val teamsList = DB.getTeamsWithAthletes()
    if (teamsList == null) {
        state.stage = Stage.CONFIG
        CurrentTabStatus("Not Data")
    } else {
        state.stage = Stage.RESULTS
        val startProtocols = CommandsHandler.startProtocolsGeneration(teamsList)
        DB.insertCompetitions(startProtocols)
        val surfaceGradient =
            Brush.horizontalGradient(colors = listOf(MaterialTheme.colors.secondary, MaterialTheme.colors.surface))
        Column(Modifier.background(surfaceGradient)) {
            Scaffold(
                Modifier.background(surfaceGradient),
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Start Protocols") },
                        actions = {
                            Row(horizontalArrangement = Arrangement.End) {
                                Button(modifier = Modifier.padding(10.dp), onClick = {

                                }) {
                                    Text("TOSS")
                                }
                            }
                        }
                    )
                },
                content = {
                    TableForStartProtocols(
                        surfaceGradient = surfaceGradient
                    )
                }
            )
        }
    }

}


@Composable
fun ContentGroupResults(state: ApplicationWindowState) {
    if (DB.checkResultsGroup(COMPETITION_ID)) {
        CurrentTabStatus("Not Data")
    } else {
        state.stage = Stage.RESULTS
//        val results = CommandsHandler.generateResults(teamsList)
//        DB.insertCompetitions(startProtocols)
//        val surfaceGradient =
//            Brush.horizontalGradient(colors = listOf(MaterialTheme.colors.secondary, MaterialTheme.colors.surface))
//        Column(Modifier.background(surfaceGradient)) {
//            Scaffold(
//                Modifier.background(surfaceGradient),
//                topBar = {
//                    TopAppBar(
//                        title = { Text(text = "Start Protocols") },
//                        actions = {
//                            Row(horizontalArrangement = Arrangement.End) {
//                                Button(modifier = Modifier.padding(10.dp), onClick = {
//
//                                }) {
//                                    Text("TOSS")
//                                }
//                            }
//                        }
//                    )
//                },
//                content = {
//                    TableForStartProtocols(
//                        surfaceGradient = surfaceGradient
//                    )
//                }
//            )
//        }
    }
}

@Composable
fun ContentTeamResults(state: ApplicationWindowState) {

}

@Composable
fun ContentItemInformation(state: ApplicationWindowState, typeItem: TypeItemInformationList) {
    val surfaceGradient =
        Brush.horizontalGradient(colors = listOf(MaterialTheme.colors.secondary, MaterialTheme.colors.surface))
    val addButtonState = remember { mutableStateOf(false) }
    Column(Modifier.background(surfaceGradient)) {
        Scaffold(
            Modifier.background(surfaceGradient),
            topBar = {
                TopAppBar(
                    title = { Text(text = typeItem.item.title) },
                    actions = {
                        Row(horizontalArrangement = Arrangement.End) {
                            Button(modifier = Modifier.padding(10.dp), onClick = {

                            }) {
                                Text("SAVE")
                            }
                            Button(modifier = Modifier.padding(10.dp), onClick = {
                                addButtonState.value = true
                            }) {
                                Text("ADD")
                            }
                        }
                    }
                )
            },
            content = {
                TableForItemInformationList(
                    state,
                    addButtonState = addButtonState,
                    typeItem = typeItem,
                    surfaceGradient = surfaceGradient
                )
            }
        )
    }
}
