package ru.emkn.kotlin.sms

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import mu.KotlinLogging
import ru.emkn.kotlin.sms.gui.Application
import ru.emkn.kotlin.sms.utils.LocalAppResources
import ru.emkn.kotlin.sms.utils.getAppResources
import ru.emkn.kotlin.sms.gui.rememberApplicationState


val logger = KotlinLogging.logger {}


@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    CompositionLocalProvider(LocalAppResources provides getAppResources()) {
            Application(rememberApplicationState(this))
    }
}
