package ru.emkn.kotlin.sms.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.awt.FileDialog
import java.io.File
import java.nio.file.Path

@Composable
fun FrameWindowScope.FileDialog(
    onResult: (result: Path?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(window, "Choose a file", LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    if (file != null) {
                        onResult(File(directory).resolve(file).toPath())
                    } else {
                        onResult(null)
                    }
                }
            }
        }
    },
    dispose = FileDialog::dispose
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FrameWindowScope.AlertDialog(
    message: String,
    onResult: (result: Boolean) -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                onResult(false)
                openDialog.value = false
            },
            title = {
                Text(text = "Warning")
            },
            text = {
                Text(
                    message
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResult(true)
                        openDialog.value = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onResult(false)
                        openDialog.value = false
                    }
                ) {
                    Text("No")
                }
            },
        )
    }
}

//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun AlertDialog() {
//    val openDialog = remember { mutableStateOf(true) }
//    if (openDialog.value) {
//        AlertDialog(
//            onDismissRequest = {
//                openDialog.value = false
//            },
//            title = {
//                Text(text = "Title")
//            },
//            text = {
//                Text(
//                    "Invalid configuration file format"
//                )
//            },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        openDialog.value = false
//                    }
//                ) {
//                    Text("Confirm")
//                }
//            },
//            dismissButton = {
//                TextButton(
//                    onClick = {
//                        openDialog.value = false
//                    }
//                ) {
//                    Text("Dismiss")
//                }
//            }
//        )
//    }
//}


