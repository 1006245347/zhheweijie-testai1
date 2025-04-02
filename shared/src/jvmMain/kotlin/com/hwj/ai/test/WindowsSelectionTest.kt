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
import com.hwj.ai.global.printD
import com.hwj.ai.selection.GlobalMouseHook8
import com.hwj.ai.selection.GlobalMouseHook8.robot
import com.hwj.ai.selection.GlobalMouseHook9
import com.hwj.ai.selection.clearClip
import com.hwj.ai.selection.isValidSelection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.ProvidePreComposeLocals
import java.awt.Dimension
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.event.KeyEvent

@Composable
fun WindowsSelectionTest(onCloseRequest: () -> Unit) {
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
            subScope.launch(Dispatchers.IO) {
                GlobalMouseHook9.start(
                    appBlock = { str ->
                        appText.value = str
                    }, contentBlock = { content ->
                        selectedText.value = content
                    }
                )
            //start()最后有个循环，不能再执行后续代码 ，另起一个
            }
//            subScope.launch {
//                setClipboardContent("xxdddI")
//                delay(5000)
////                clearClip()
//                clearWindowClip()
//            }
            return@LaunchedEffect
            subScope.launch(Dispatchers.IO) {
//                robot = Robot()
//                printD("running>>${isStart.value} ${GlobalMouseHook8.state.isDragging}")
                while (isStart.value) {
                    //记录当前鼠标位置

                    delay(100) //要同步才对
                    if (
                        GlobalMouseHook8.state.isDragging
                        //加上isContent判断吧
//                     &&  selectedText.value != GlobalMouseHook8.state.lastClipboardText
                        ) {

                        robot?.let {
                            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                            it.keyPress(KeyEvent.VK_CONTROL)
                            it.keyPress(KeyEvent.VK_C)
                            it.keyRelease(KeyEvent.VK_C)
                            it.keyRelease(KeyEvent.VK_CONTROL)

                            val startTime = System.currentTimeMillis()
                            var copiedText: String? = null
                            while (System.currentTimeMillis() - startTime < 500) {
                                Thread.sleep(50)
                                println("thread>${Thread.currentThread().name}")
                                copiedText = clipboard.getData(DataFlavor.stringFlavor) as String?
                                if (!copiedText.isNullOrEmpty() && isValidSelection(copiedText)) {
                                    GlobalMouseHook8.state.lastClipboardText = copiedText
                                    GlobalMouseHook8.state.isDragging = false
                                    selectedText.value = copiedText
                                    clearClip()
                                    break
                                }
                            }
                            println("text>$copiedText")
                        }
                    }
                }
            }
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
//                        ClipMonitor(isStart.value, GlobalMouseHook8.state)
                    }
                }
            }
        }
    }
}