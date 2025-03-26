package com.hwj.ai.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.hwj.ai.checkSystem
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.global.printE
import com.hwj.ai.selection.captureSelectedTextUnderCursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.ProvidePreComposeLocals
import java.awt.Dimension

@Composable
fun WindowsSelectionTest(onCloseRequest: () -> Unit) {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        width = 700.dp,
        height = 500.dp,
    )

    val selectedText = remember { mutableStateOf("finding") }
    val subScope = rememberCoroutineScope()
    val isStart = remember { mutableStateOf(false) }
    LaunchedEffect(isStart.value) {
        subScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    val text = captureSelectedTextUnderCursor()
                    if (!text.isNullOrBlank()) {
                        withContext(Dispatchers.Main) {
                            selectedText.value = text
                        }
                    }

                } catch (e: Exception) {
                    printE(e)
                }
                delay(500) //轮询频率
            }
        }
    }

    return Window(
        onCloseRequest, title = "hwj-ai-chat", state = windowState
    ) {
        val window = this.window
        window.minimumSize = Dimension(600, 450)
        ProvidePreComposeLocals {
            ThemeChatLite {
                Surface(Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Button(onClick = {
                            if (checkSystem() == OsStatus.WINDOWS) {
                                isStart.value = !isStart.value
                            }
                        }) {
                            Text("windows划词>${selectedText.value}")
                        }
                    }
                }
            }
        }
    }

}