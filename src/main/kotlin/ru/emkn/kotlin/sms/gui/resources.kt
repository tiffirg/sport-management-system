package ru.emkn.kotlin.sms.gui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

fun getAppResources(): AppResources {
    return AppResources("Application \"Competition\" for Desktop")
}

val LocalAppResources = staticCompositionLocalOf<AppResources> {
    error("LocalNotepadResources isn't provided")
}

data class AppResources(val titleApp: String)  // TODO("Заглушка")


enum class TabType(val tabName: String) {
    START_PROTOCOL("Start protocols"), GROUP_RESULTS("Group results"), TEAM_RESULTS("Team results")
}

object DataProvider {

    fun appSurfaceGradient(isDark: Boolean) =
        if (isDark) listOf(graySurface, spotifyBlack) else listOf(Color.White, Color.LightGray)

    val informationList = listOf(
        "Участники: команды",
        "Участники: группы",
        "Список дистанций",
        "Критерии дистанций",
        "Информация..."
    )
}
