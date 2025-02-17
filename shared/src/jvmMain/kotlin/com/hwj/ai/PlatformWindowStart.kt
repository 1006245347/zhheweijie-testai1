package com.hwj.ai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.hwj.ai.capture.TestCapture
import com.hwj.ai.ui.capture.CaptureFetch
import moe.tlaster.precompose.ProvidePreComposeLocals

@Composable
fun PlatformWindowStart(onCloseRequest: () -> Unit) {
    return Window(
        onCloseRequest, title = "hwj-ai",
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = 1200.dp,
            height = 700.dp,
        )
    ) {
        ProvidePreComposeLocals {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                PlatformAppStart()
                CaptureFetch()
            }
        }
    }
}