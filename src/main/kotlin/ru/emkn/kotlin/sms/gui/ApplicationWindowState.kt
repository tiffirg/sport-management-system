package ru.emkn.kotlin.sms.gui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.CompletableDeferred
import ru.emkn.kotlin.sms.*
import java.nio.file.Path
import kotlin.io.path.pathString

enum class Stage {
    NO_CONFIG,
    CONFIG,
    START_PROTOCOLS,
    RESULTS,
}

class ApplicationWindowState(
    application: ApplicationState
) {
    private var configPath: Path? = null

    val window = WindowState()

    init {
        window.placement = WindowPlacement.Maximized
    }

    val stateOpenFileDialog = DialogState<Path?>()
    val stateOpenWarningDialog = DialogState<Boolean>()

    var stage = Stage.NO_CONFIG

    private suspend fun openConfig(path: Path) {
        configPath = path
        LOGGER.debug { "Path from dialog $path" }
        try {
            initConfig(path.pathString)
            stage = Stage.CONFIG
            val competitionDb = DB.getCompetition(EVENT_NAME)
            if (competitionDb != null) {
                COMPETITION_ID = competitionDb.id.value
                DB.installConfigData(competitionDb.id.value)
                LOGGER.debug { "checkStartsProtocols | ${DB.checkStartsProtocols(competitionDb.id.value)}" }
                stage = when {
                    DB.checkStartsProtocols(competitionDb.id.value) -> Stage.START_PROTOCOLS
                    DB.checkResultsGroup(competitionDb.id.value) -> Stage.RESULTS
                    else -> Stage.CONFIG
                }
            } else {
                COMPETITION_ID = DB.insertConfigData().id.value
            }

        } catch (exception: Exception) {
            exception.printStackTrace()
            LOGGER.debug { exception }
            stateOpenWarningDialog.message = "Can not read $path. Repeat operation?"
            openWarningDialog()
        }
    }

    suspend fun openFileDialog() {
        val path = stateOpenFileDialog.awaitResult()
        if (path != null) {
            openConfig(path)
        } else {
            LOGGER.debug { "Closed File Dialog before getting path" }
            stateOpenWarningDialog.message = "Invalid configuration file format. Repeat operation?"
            openWarningDialog()

        }
    }

    private suspend fun openWarningDialog() {
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
