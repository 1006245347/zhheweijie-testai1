package com.hwj.ai.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberWindowState
import coil3.compose.LocalPlatformContext
import com.hwj.ai.capture.LocalMainWindow
import com.hwj.ai.global.cBlackTxt
import com.hwj.ai.global.cWhite
import com.hwj.ai.ui.global.AISelectIntent
import com.hwj.ai.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.LocalWindow
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import java.awt.MouseInfo
import java.awt.Toolkit

/**
 * @author by jason-何伟杰，2025/4/15
 * des:浮窗小工具
 */
@Composable
fun FloatWindowInside() {
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val subScope = rememberCoroutineScope()
    val mousePos = MouseInfo.getPointerInfo().location

    DisposableEffect(Unit) {
        onDispose { }
    }
    val mainWindow = LocalMainWindow.current
    val w1 = Toolkit.getDefaultToolkit().screenSize.width
    val h1 = Toolkit.getDefaultToolkit().screenSize.height


    val ws = rememberWindowState()

    DialogWindow( //不要加Surface,会造成一层白色背景
        resizable = false,
        onCloseRequest = {

        }, undecorated = true,
        focusable = false, //防止浮窗影响主窗口的点击
        alwaysOnTop = true,
        transparent = true, title = "window", state = rememberDialogState(
            size = DpSize(w1.dp, h1.dp)
        )
    ) {
        Box(modifier = Modifier.background(Color.Transparent).fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    chatViewModel.preWindow(false)
                })
            }) {

            Row(
                modifier = Modifier.offset(
                    x = (mousePos.x - 40).coerceAtLeast(0).coerceAtMost(w1 - 100).dp,
                    y = (mousePos.y - 40).coerceAtLeast(0).dp
                ).background(cWhite(), shape = RoundedCornerShape(16.dp))
            ) {//modifier = Modifier.offset(x = mousePos.x.dp, y = mousePos.y.dp)

                Image(
                    Icons.Default.Description,
                    "AI Chat",
                    modifier = Modifier.size(30.dp).clickable {
                        subScope.launch(Dispatchers.Main) {
//                            mainWindow.isMinimized = false
                            mainWindow.toFront()
                            mainWindow.isVisible = true
                            ws.isMinimized=false
                        }
                        chatViewModel.preWindow(false)
                    })
                Text(
                    "AI搜索",
                    color = cBlackTxt(),
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        chatViewModel.processAiSelect(AISelectIntent.SearchData)
                        chatViewModel.preWindow(false)
                    }.padding(horizontal = 4.dp)
                )
                Text("总结", color = cBlackTxt(), fontSize = 12.sp, modifier = Modifier.clickable {
                    chatViewModel.processAiSelect(AISelectIntent.SummaryData)
                    chatViewModel.preWindow(false)
                }.padding(horizontal = 4.dp))
                Text("复制", color = cBlackTxt(), fontSize = 12.sp, modifier = Modifier.clickable {
                    chatViewModel.processAiSelect(AISelectIntent.CopyData)
                    chatViewModel.preWindow(false)
                }.padding(horizontal = 4.dp))
                Image(
                    Icons.Default.Close,
                    "取消",
                    modifier = Modifier.size(30.dp).padding(horizontal = 4.dp)
                        .clickable { chatViewModel.preWindow(false) }
                )
                Button(onClick = {
                    chatViewModel.preWindow(false)
                    chatViewModel.viewModelScope.launch {
                        delay(5000)
                        chatViewModel.preWindow(true)
                    }
                }) {
                    Text("x1")
                }

                Spacer(Modifier.width(10.dp))
                Button(onClick = {
                    chatViewModel.preWindow(false)
                }) {
                    Text("x2")
                }
            }
        }
    }
}

