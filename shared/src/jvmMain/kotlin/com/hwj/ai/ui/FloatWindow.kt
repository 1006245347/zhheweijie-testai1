package com.hwj.ai.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.hwj.ai.capture.LocalMainWindow
import com.hwj.ai.global.cBlackTxt
import com.hwj.ai.global.cWhite
import com.hwj.ai.selection.GlobalMouseHook9
import com.hwj.ai.ui.global.AISelectIntent
import com.hwj.ai.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import java.awt.Frame
import java.awt.Toolkit

/**
 * @author by jason-何伟杰，2025/4/15
 * des:浮窗小工具
 */
@Composable
fun FloatWindowInside() {
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val subScope = rememberCoroutineScope()
//    val mousePos = MouseInfo.getPointerInfo().location //当前鼠标键位坐标
    var x1 = GlobalMouseHook9.mousePressedPos.x //左键初始点击
    var y1 = GlobalMouseHook9.mousePressedPos.y
    if (GlobalMouseHook9.mousePressedPos.x - GlobalMouseHook9.endPressPos.x > 0) {
        x1 = GlobalMouseHook9.endPressPos.x
    }
    if (GlobalMouseHook9.mousePressedPos.y - GlobalMouseHook9.endPressPos.y > 0) {
        y1 = GlobalMouseHook9.endPressPos.y
    }

    val mainWindow = LocalMainWindow.current
    fun window2Front() {
        subScope.launch(Dispatchers.Main) {
            mainWindow.isVisible = true
            mainWindow.toFront()
            mainWindow.state = Frame.NORMAL //6，加了这个可以置顶
        }
    }

    val w1 = Toolkit.getDefaultToolkit().screenSize.width
    val h1 = Toolkit.getDefaultToolkit().screenSize.height

    DisposableEffect(Unit) {
        onDispose {
            mainWindow.toFront()
            println("main to Front>")
        }
    }


    Window( //不要加Surface,会造成一层白色背景
        resizable = false,
        onCloseRequest = {

        },
        undecorated = true,
        focusable = false, //防止浮窗影响主窗口的点击
        alwaysOnTop = true,
        transparent = true,
        title = "window",
        state = rememberWindowState(size = DpSize(w1.dp, h1.dp))
    ) {
        Box(modifier = Modifier.background(Color.Transparent).fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    chatViewModel.preWindow(false)
                })
            }) {

            Row(
                modifier = Modifier.offset(
                    x = (x1 - 45).coerceAtLeast(0).coerceAtMost(w1 - 45).dp,
                    y = (y1 - 100).coerceAtLeast(0).dp
                ).background(cWhite(), shape = RoundedCornerShape(4.dp))
                    .shadow(elevation = 1.dp, ambientColor = Color.Gray, spotColor = Color.Gray)
            ) {//modifier = Modifier.offset(x = mousePos.x.dp, y = mousePos.y.dp)

                Image(
                    Icons.Default.Description,
                    "AI Chat",
                    modifier = Modifier.size(25.dp).padding(start = 3.dp).clickable {

                        window2Front()

                        chatViewModel.preWindow(false)
                    })
                Text(
                    "AI搜索",
                    color = cBlackTxt(),
                    fontSize = 13.sp,
                    modifier = Modifier.clickable {
                        chatViewModel.processAiSelect(AISelectIntent.SearchData)
                        window2Front()
                        chatViewModel.preWindow(false)
                    }.padding(horizontal = 4.dp)
                )
                Text("总结", color = cBlackTxt(), fontSize = 13.sp, modifier = Modifier.clickable {
                    chatViewModel.processAiSelect(AISelectIntent.SummaryData)
                    window2Front()
                    chatViewModel.preWindow(false)
                }.padding(horizontal = 4.dp))
                Text("复制", color = cBlackTxt(), fontSize = 13.sp, modifier = Modifier.clickable {
                    chatViewModel.processAiSelect(AISelectIntent.CopyData)
                    window2Front()
                    chatViewModel.preWindow(false)
                }.padding(horizontal = 4.dp))
                Image(
                    Icons.Default.Close,
                    "取消",
                    modifier = Modifier.size(30.dp).padding(horizontal = 4.dp)
                        .clickable { chatViewModel.preWindow(false) }
                )
            }
        }
    }
}


@Composable
fun showMainWindow(flag: Boolean) {
        println("showM>$flag")
    if (flag) {
        LocalMainWindow.current.apply {
            isVisible = true
            toFront()
            state = Frame.NORMAL
        }
    } else {
        LocalMainWindow.current.isVisible = false
    }

}
