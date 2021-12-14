package ru.emkn.kotlin.sms.gui

import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.CompletableDeferred
import ru.emkn.kotlin.sms.logger
import java.nio.file.Path
import kotlin.io.path.readText

class ApplicationWindowState(
    private val application: ApplicationState
) {
    private var configPath: Path? = null   // TODO("Решить проблему")


    val window = WindowState()

    val openFileDialog = DialogState<Path?>()

    private fun open(path: Path) {
        configPath = path
        logger.debug { "Path from dialog $path" }
        try {
            configPath?.readText() ?: error("ConfigPath not null")
            TODO("Проверка файла конфига")
        } catch (e: Exception) {
            e.printStackTrace()
            TODO("Cannot read $path")
        }
    }

    suspend fun open() {
        val path = openFileDialog.awaitResult()
        if (path != null) {
            open(path)
        }
        else {
            logger.debug { "Closed File Dialog before getting path" }
            TODO("Добавить окно с предупреждением")
            if (configPath == null) {
                open()
            }
        }
    }

    val exit = application.exit  // Пока поддерживается лишь одно окно
}

class DialogState<T> {
    private var onResult: CompletableDeferred<T>? by mutableStateOf(null)

    val isAwaiting get() = onResult != null

    suspend fun awaitResult(): T {
        onResult = CompletableDeferred()
        val result = onResult!!.await()
        onResult = null
        return result
    }

    fun onResult(result: T) = onResult!!.complete(result)
}
