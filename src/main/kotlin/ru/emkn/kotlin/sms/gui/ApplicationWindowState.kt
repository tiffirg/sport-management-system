package ru.emkn.kotlin.sms.gui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.CompletableDeferred
import ru.emkn.kotlin.sms.initConfig
import ru.emkn.kotlin.sms.logger
import java.nio.file.Path
import kotlin.io.path.pathString

enum class Stage {
    NO_CONFIG,
    CONFIG,
    START_PROTOCOLS,
    RESULTS_GROUP,
    RESULTS_TEAM
}

class ApplicationWindowState(
    private val application: ApplicationState
) {
    private var configPath: Path? = null

    val window = WindowState()

    val stateOpenFileDialog = DialogState<Path?>()
    val stateOpenWarningDialog = DialogState<Boolean>()

    var stage = Stage.NO_CONFIG
        private set

    private suspend fun openConfig(path: Path) {
        configPath = path
        logger.debug { "Path from dialog $path" }
        try {
            initConfig(path.pathString)
            stage = Stage.CONFIG
        } catch (exception: Exception) {
            exception.printStackTrace()
            logger.debug { exception }
            stateOpenWarningDialog.message = "Can not read $path. Repeat operation?"
            openWarningDialog()
        }
    }
    
    suspend fun openFileDialog() {
        val path = stateOpenFileDialog.awaitResult()
        if (path != null) {
            openConfig(path)
        } else {
            logger.debug { "Closed File Dialog before getting path" }
            stateOpenWarningDialog.message = "Invalid configuration file format. Repeat operation?"
            openWarningDialog()

        }
    }
    suspend fun openWarningDialog() {
        val result = stateOpenWarningDialog.awaitResult()
        if (result) {
            openFileDialog()
        }
    }

    val exit = application.exit  // Пока поддерживается лишь одно окно
}

class DialogState<T> {
    private var onResult: CompletableDeferred<T>? by mutableStateOf(null)
    var message = ""
    val isAwaiting get() = onResult != null

    suspend fun awaitResult(): T {
        onResult = CompletableDeferred()
        val result = onResult!!.await()
        onResult = null
        return result
    }

    fun onResult(result: T) = onResult!!.complete(result)
}
