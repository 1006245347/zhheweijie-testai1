package com.hwj.ai.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.hwj.ai.checkSystem
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.selection.GlobalMouseHook9
import moe.tlaster.precompose.ProvidePreComposeLocals
import java.awt.Dimension

/**
 * @author by jason-何伟杰，2025/4/9
 * des:JNA+ UIA
 */
@Composable
fun WindowsSelectionUIATest(onCloseRequest: () -> Unit) {

    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        width = 700.dp,
        height = 500.dp,
    )
    val subScope = rememberCoroutineScope()
    var selectedText = remember { mutableStateOf<String?>("find") }
    var appText = remember { mutableStateOf<String?>("") }
    val isStart = remember { mutableStateOf(false) }


    LaunchedEffect(isStart.value) {
        if (isStart.value) {
            GlobalMouseHook9.start(appBlock = { str ->
                appText.value = str
            }, contentBlock = { content ->
                selectedText.value = content
            })
        } else {
            GlobalMouseHook9.stop()
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
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                        Column {
                            Button(onClick = {
                                if (checkSystem() == OsStatus.WINDOWS) {
                                    isStart.value = !isStart.value
                                }
                            }) {
                                Text("windows划词 ${isStart.value}>")
                            }
                            Text(
                                text = "app> ${appText.value}",
                                fontFamily = FontFamily.Default,
                                maxLines = 5
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "find> ${selectedText.value}",
                                fontFamily = FontFamily.Default,
                                maxLines = 8
                            )
                        }
                    }
                }
            }
        }
    }


}