package com.hwj.ai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
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

//编译运行命令 ./gradlew :desktop:run
//打包命令 ./gradlew packageDistributionForCurrentOS
//安装包路径 build/compose/binaries/main/
//build/compose/binaries/main/exe/
//build/compose/binaries/main/deb/
//Ubuntu/Debian: MyApp-1.0.0.deb

//control +  option +O       control + C 中断调试
//Tray 系统托盘
@Composable
fun PlatformWindowStart(onCloseRequest: () -> Unit) {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        width = 750.dp,
        height = 560.dp,
    )

    return Window(
        onCloseRequest, title = "hwj-ai-chat", state = windowState
    ) {
        val window = this.window
        window.minimumSize = Dimension(650, 450)
        ProvidePreComposeLocals {
            CompositionLocalProvider(LocalMainWindow provides window) {
                ThemeChatLite {
                    Surface(Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            PlatformAppStart()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrayApp() {
//    TrayIcon()
}