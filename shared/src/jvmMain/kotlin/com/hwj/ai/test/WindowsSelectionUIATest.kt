package com.hwj.ai.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.hwj.ai.capture.LocalMainWindow
import com.hwj.ai.checkSystem
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.selection.GlobalMouseHook9
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.ProvidePreComposeLocals
import java.awt.Dimension

/**
 * @author by jason-何伟杰，2025/4/9
 * des:JNA+ UIA
 */
@Composable
fun WindowsSelectionUIATest(windowState: WindowState, onCloseRequest: () -> Unit) {

    val subScope = rememberCoroutineScope()
    var selectedText = remember { mutableStateOf<String?>("find") }
    var appText = remember { mutableStateOf<String?>("") }
    val isStart = remember { mutableStateOf(false) }

    LaunchedEffect(isStart.value) {
        if (isStart.value) {
            subScope.launch(Dispatchers.IO) {
                GlobalMouseHook9.start(true,appBlock = { str ->
                    appText.value = str
                }, contentBlock = { content -> //获取到选中数据
                    selectedText.value = content
                })
            }

            //不能根上面的一起不然会卡
            subScope.launch(Dispatchers.IO) {
                while (isStart.value) {
                    delay(50)
                    if (GlobalMouseHook9.isDragging) {
                        GlobalMouseHook9.handleMouseAct()
                        GlobalMouseHook9.isDragging = false
                    }
                }
            }
        } else {
            GlobalMouseHook9.stop()
        }
    }

    return Window(
        onCloseRequest = {
//            GlobalMouseHook9.stop()// 让托盘事件处理，这里只隐藏
//            onCloseRequest()
            windowState.isMinimized = true
        }, title = "hwj-ai-chat", state = windowState
    ) {

        val window = this.window
        window.minimumSize = Dimension(600, 450)
        ProvidePreComposeLocals {
            CompositionLocalProvider(LocalMainWindow provides window) {

//                val mainWindow = LocalMainWindow.current //不能放外面，构造器CompositionLocalProvider

                ThemeChatLite {
                    Surface(Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                Button(onClick = {
                                    if (checkSystem() == OsStatus.WINDOWS) {
                                        isStart.value = !isStart.value
                                    }
                                }) {
                                    Text("windows划词 ${isStart.value}>")
                                }
                                SelectionContainer {
                                    Text(
                                        text = "app> ${appText.value}",
                                        fontFamily = FontFamily.Default,
                                        maxLines = 5
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                SelectionContainer {
                                    Text(
                                        text = "find> ${selectedText.value}",
                                        fontFamily = FontFamily.Default
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}