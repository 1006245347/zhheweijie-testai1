package com.hwj.ai.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.hwj.ai.capture.ScreenshotOverlay9
import com.hwj.ai.capture.saveToFile9
import com.hwj.ai.global.ThemeChatLite
import moe.tlaster.precompose.ProvidePreComposeLocals
import java.awt.Dimension

@Composable
fun PlatformWindowStartTest(onCloseRequest: () -> Unit) {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        width = 700.dp,
        height = 500.dp,
    )

    var showShot by remember { mutableStateOf(false) }
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
                            showShot = true
                        }) {
                            Text("截图")
                        }

                        if (showShot) {
                            ScreenshotOverlay9(onCapture = { pic ->
                                try {
                                    saveToFile9(pic)
                                } catch (e: Exception) {
                                }
                            }, onCancel = { showShot = false })
                        }
                    }
                }
            }
        }
    }
}
