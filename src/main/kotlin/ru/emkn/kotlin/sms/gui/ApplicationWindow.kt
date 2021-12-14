package ru.emkn.kotlin.sms.gui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import ru.emkn.kotlin.sms.gui.TabType.*


@Composable
fun ApplicationWindow(state: ApplicationWindowState) {
    Window(
        state = state.window,
        title = LocalAppResources.current.titleApp,
        onCloseRequest = state.exit  // TODO("FIX")
    ) {
        val darkTheme = remember { mutableStateOf(true) }
        MaterialTheme(colors = if (darkTheme.value) DarkGreenColorPalette else LightGreenColorPalette) {  // TODO("Refactoring")
            WindowMenuBar(state)
            WindowLayout(state, darkTheme)

            if (state.openFileDialog.isAwaiting) {
                FileDialog(
                    onResult = {
                        state.openFileDialog.onResult(it)
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

    fun open() = scope.launch { state.open() }
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
        Menu("Help", mnemonic = 'H') {
            Item(
                text = "About",
                onClick = { TODO("Ссылка на README") }
            )
            Item(
                text = "Help",
                onClick = { TODO("Ссылка на DOCS") }
            )
        }
    }
}

@Composable
private fun WindowLayout(state: ApplicationWindowState, darkTheme: MutableState<Boolean>) {
    val tabItemState = remember { mutableStateOf(START_PROTOCOL) }
    Box {
        Row {
            SideBar(state, darkTheme, tabItemState)
//            BodyContent(tabItemState)
        }
    }
}

@Composable
fun SideBar(state: ApplicationWindowState, darkTheme: MutableState<Boolean>, tabItemState: MutableState<TabType>) {
    val selectedIndex = remember { mutableStateOf(-1) }

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
        SideBarNavItem(START_PROTOCOL.tabName, Icons.Default.Add, tabItemState.value == START_PROTOCOL) {
            tabItemState.value = START_PROTOCOL
            selectedIndex.value = -1
        }
        SideBarNavItem(GROUP_RESULTS.tabName, Icons.Default.Info, tabItemState.value == GROUP_RESULTS) {
            tabItemState.value = GROUP_RESULTS
            selectedIndex.value = -1
        }
        SideBarNavItem(TEAM_RESULTS.tabName, Icons.Default.Done, tabItemState.value == TEAM_RESULTS) {
            tabItemState.value = TEAM_RESULTS
            selectedIndex.value = -1
        }
        Spacer(modifier = Modifier.height(20.dp))
        PlayListsSideBar(selectedIndex.value) {
//            showAlbumDetailState.value = SpotifyDataProvider.albums[it]
            selectedIndex.value = it
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
fun PlayListsSideBar(selectedIndex: Int, onPlayListSelected: (Int) -> Unit) {
    Text(
        "Information",
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
        color = MaterialTheme.colors.onSurface
    )
    LazyColumn {
        itemsIndexed(DataProvider.informationList) { index, playlist ->
            Text(
                playlist,
                modifier = Modifier.padding(8.dp).clickable { TODO("onPlayListSelected.invoke(index)") },
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


//@Composable
//fun BodyContent(spotifyNavType: MutableState<TabType>) {
//    if (album.value != null) {
//        SpotifyDetailScreen(album.value!!) {
//            album.value = null
//        }
//    } else {
//        Crossfade(current = spotifyNavType) { spotifyNavType ->
//            when (spotifyNavType) {
//                SpotifyNavType.HOME -> SpotifyHome { onAlbumSelected ->
//                    album.value = onAlbumSelected
//                }
//                SpotifyNavType.SEARCH -> SpotifySearchScreen { onAlbumSelected ->
//                    album.value = onAlbumSelected
//                }
//                SpotifyNavType.LIBRARY -> SpotifyLibrary()
//            }
//        }
//    }
//}

