package ru.emkn.kotlin.sms.gui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.ApplicationScope


@Composable
fun ApplicationScope.Application(applicationState: ApplicationState) {
    ApplicationWindow(applicationState.window)
}


