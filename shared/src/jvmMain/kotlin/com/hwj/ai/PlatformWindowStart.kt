package com.hwj.ai

import androidx.compose.foundation.ContextMenuDataProvider
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.singleWindowApplication
import com.hwj.ai.capture.ScreenshotOverlay6
import com.hwj.ai.capture.ScreenshotOverlay7
import com.hwj.ai.capture.ScreenshotOverlay8
import com.hwj.ai.capture.ScreenshotOverlay9
import com.hwj.ai.capture.saveToFile
import com.hwj.ai.capture.saveToFile6
import com.hwj.ai.capture.saveToFile7
import com.hwj.ai.capture.saveToFile8
import com.hwj.ai.capture.saveToFile9
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
@Composable
fun PlatformWindowStart(onCloseRequest: () -> Unit) {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        width = 700.dp,
        height = 500.dp,
    )

    return Window(
        onCloseRequest, title = "hwj-ai-chat", state = windowState
    ) {
        val window = this.window
        window.minimumSize = Dimension(600, 450)
        ProvidePreComposeLocals {
            ThemeChatLite {
                Surface(Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        PlatformAppStart()
                    }
                }
            }
        }
    }
}

//右键控件选中文本
@Composable
fun quickStart() = singleWindowApplication(title = "quick") {
    val text = remember { mutableStateOf("百度一下") }
    val subScope = rememberCoroutineScope()
    Column {
        ContextMenuDataProvider(items = {
            listOf(ContextMenuItem("Ai Search") {
                //调接口
            })
        }) {
            TextField(value = text.value, onValueChange = { text.value = it })
        }
    }
    SelectionContainer {
        Text("中国清朝几个皇帝?")
    }
}