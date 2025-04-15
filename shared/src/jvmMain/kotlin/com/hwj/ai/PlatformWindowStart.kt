package com.hwj.ai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.hwj.ai.capture.LocalMainWindow
import com.hwj.ai.global.OsStatus
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
fun PlatformWindowStart(windowState: WindowState, onCloseRequest: () -> Unit) {

    return Window(
        onCloseRequest = {
            windowState.isMinimized = true
        }, title = "hwj-ai-chat", state = windowState,
    ) {
        val window = this.window
        window.minimumSize = Dimension(650, 450)

        ProvidePreComposeLocals {
            CompositionLocalProvider(
                LocalMainWindow provides window,
            ) {
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
