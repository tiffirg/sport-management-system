package ru.emkn.kotlin.sms

import kotlinx.cli.ExperimentalCli
import ru.emkn.kotlin.sms.data.ExitCode.SUCCESS
import ru.emkn.kotlin.sms.services.ArgumentsHandler
import ru.emkn.kotlin.sms.utils.ExceptionWithExitCode
import kotlin.system.exitProcess

@ExperimentalCli
fun main(args: Array<String>) {
    try {
        val argsParsed = ArgumentsHandler.apply(args)
        val app = App(argsParsed.title, argsParsed.date)
        app.run(argsParsed.command)
    }
    catch (exception: ExceptionWithExitCode) {
        println(exception)
        exitProcess(exception.exitCode)
    }
    exitProcess(SUCCESS.exitCode)

}
