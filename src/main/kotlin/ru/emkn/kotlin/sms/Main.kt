package ru.emkn.kotlin.sms

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
