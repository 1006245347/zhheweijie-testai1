package com.hwj.ai

import androidx.compose.foundation.ContextMenuDataProvider
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.singleWindowApplication
import com.hwj.ai.capture.testCapture2
import com.hwj.ai.capture.testCapture3
import com.hwj.ai.capture.testCapture4
import com.hwj.ai.capture.testShot
import com.hwj.ai.except.saveInt
import com.hwj.ai.ui.capture.CaptureFetch
import kotlinx.coroutines.launch
import moe.tlaster.precompose.ProvidePreComposeLocals

@Composable
fun PlatformWindowStart(onCloseRequest: () -> Unit) {
    return Window(
        onCloseRequest, title = "hwj-ai",
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = 1000.dp,
            height = 700.dp,
        )
    ) {
        ProvidePreComposeLocals {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                PlatformAppStart()
//                CaptureFetch()
//                testShot()
//                testCapture3()
//                testCapture4()
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
        ContextMenuDataProvider(items={
            listOf(ContextMenuItem("Ai Search"){
                //调接口
            })
        }){
            TextField(value = text.value, onValueChange = {text.value=it})
        }
    }
//    subScope.launch {
//        saveInt("e",3)
//    }
    SelectionContainer {
        Text("中国清朝几个皇帝?")
    }
}