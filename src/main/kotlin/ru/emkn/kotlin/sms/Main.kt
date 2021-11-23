package ru.emkn.kotlin.sms

import kotlinx.cli.ExperimentalCli
import ru.emkn.kotlin.sms.services.ArgumentsHandler

@ExperimentalCli
fun main(args: Array<String>) {
    try {
        val argsParsed = ArgumentsHandler.apply(args)
        val app = App(argsParsed.title, argsParsed.date)
        app.run(argsParsed.command)
    } catch (exception: Exception) {
        println(exception)
    }
}
