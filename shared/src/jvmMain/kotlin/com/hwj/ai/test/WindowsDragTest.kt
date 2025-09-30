package com.hwj.ai.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.hwj.ai.capture.LocalMainWindow
import com.hwj.ai.global.ThemeChatLite
import moe.tlaster.precompose.ProvidePreComposeLocals
import java.awt.Dimension

@Composable
fun WindowsDragTest(onCloseRequest: () -> Unit) {

    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        width = 700.dp,
        height = 500.dp,
    )
    val subScope = rememberCoroutineScope()

    var inputStr by remember { mutableStateOf("") }

    return Window(onCloseRequest, title = "hwj-drag", state = windowState) {
        val window = this.window
        window.minimumSize = Dimension(600, 450)

        ProvidePreComposeLocals {
            CompositionLocalProvider(LocalMainWindow provides window) {

                ThemeChatLite {
                    Surface(Modifier.fillMaxSize()) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopStart
                        ) {

                            TextField(inputStr, onValueChange = { news ->
                                inputStr = news
                            })

                        }
                    }
                }
            }
        }
    }

}