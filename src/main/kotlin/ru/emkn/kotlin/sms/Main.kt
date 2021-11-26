package ru.emkn.kotlin.sms

import kotlinx.cli.ExperimentalCli
import ru.emkn.kotlin.sms.services.ArgumentsHandler

@ExperimentalCli
fun main(args: Array<String>) {
    try {
        val argsParsed = ArgumentsHandler.apply(args)
        initConfig(argsParsed.pathConfig)
        println(RANKS)
        App.run(argsParsed.command)
    } catch (exception: Exception) {
        println(exception)
    }
}
