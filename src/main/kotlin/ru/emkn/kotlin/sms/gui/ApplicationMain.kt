package ru.emkn.kotlin.sms.gui

import androidx.compose.runtime.Composable


@Composable
fun Application(applicationState: ApplicationState) {
    ApplicationWindow(applicationState.window)
}


