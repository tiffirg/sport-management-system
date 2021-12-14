package ru.emkn.kotlin.sms

//import androidx.compose.runtime.CompositionLocalProvider
//import androidx.compose.runtime.remember
//import androidx.compose.ui.ExperimentalComposeUiApi
//import androidx.compose.ui.window.application
//import mu.KotlinLogging
//import ru.emkn.kotlin.sms.gui.*
//
//
//val logger = KotlinLogging.logger {}
//
//
//@OptIn(ExperimentalComposeUiApi::class)
//fun main() = application {
//    CompositionLocalProvider(LocalAppResources provides getAppResources()) {
//        Application(rememberApplicationState(this))
//    }
//}
import kotlinx.cli.ExperimentalCli
import mu.KotlinLogging
import ru.emkn.kotlin.sms.services.ArgumentsHandler

val logger = KotlinLogging.logger { }

@ExperimentalCli
fun main(args: Array<String>) {
    try {
        val argsParsed = ArgumentsHandler.apply(args)
        initConfig(argsParsed.pathConfig)
        App.run(argsParsed.command)
    } catch (exception: Exception) {
        logger.error { exception.message }
    }
}