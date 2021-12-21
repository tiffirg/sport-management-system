package ru.emkn.kotlin.sms.gui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ApplicationScope


@Composable
fun rememberApplicationState(application: ApplicationScope) = remember {
    ApplicationState(application).apply {
        createWindow()
    }
}

class ApplicationState(private val application: ApplicationScope) {
    private lateinit var _window: ApplicationWindowState
    val window get() = _window

    fun createWindow() {
        _window = ApplicationWindowState(this)
    }

    val exit = application::exitApplication
}