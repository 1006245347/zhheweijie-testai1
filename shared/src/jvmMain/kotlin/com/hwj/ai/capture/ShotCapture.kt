package com.hwj.ai.capture

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import com.hwj.ai.global.cHalfGrey80717171
import com.hwj.ai.global.printD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.measureTimedValue

@Composable
fun ShotCapture(
    state: ScreenshotState = remember { ScreenshotState() },
    onDismiss: () -> Unit,
    onSave: (path: String, thumbnail: ImageBitmap) -> Unit
) {
    var start by remember { mutableStateOf(Offset.Zero) }
    var end by remember { mutableStateOf(Offset.Zero) }
    val subScope = rememberCoroutineScope()
    var dragEnd by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    //newBox一直指向截图区域，并构建控件
    val newBox: @Composable () -> Unit = remember {
        @Composable {
            createBox(state.selectedRect)
        }
    }
    Window(onCloseRequest = onDismiss, undecorated = true, transparent = true) {

    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))) {
        Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            detectDragGestures(
                onDragStart = {
                    start = it
                    state.startPos = it
                    state.isSelecting = true
                    dragEnd = false
                },
                onDrag = { _, delta ->
                    end += delta
                    state.currentPos = delta
                    state.selectedRect = Rect(state.startPos, delta)
                    dragEnd = false
                },
                onDragEnd = {
                    dragEnd = true
                    subScope.launch(Dispatchers.Default) {
                        state.isSelecting = false
                        /* 触发截图 */
                        val timeValue = measureTimedValue {
//                            captureToImage { newBox }
                        }
//                        printD("cost>${timeValue.duration}")
//                        state.screenshotBitmap = timeValue.value
                    }
                }
            )
        }) {
            // 绘制半透明遮罩
            drawRect(Color.Black.copy(alpha = 0.5f))
            // 绘制选中区域
            drawRect(
                Color.White,
                topLeft = state.selectedRect.topLeft,
                size = state.selectedRect.size
            )
        }
        // 功能菜单
        if (state.screenshotBitmap != null) {
            val menuPosition = calculateMenuPosition(state.selectedRect, density)
            Row(
                modifier = Modifier.offset {
                    IntOffset(
                        menuPosition.x.toInt(),
                        menuPosition.y.toInt()
                    )
                }
                    .background(Color.White)
            ) {
                Button(onClick = { /* 编辑逻辑 */ }) {
                    Text("编辑")
                }
                Button(onClick = { saveScreenshot(state, onSave) }) {
                    Text("保存")
                }
                Button(onClick = { state.screenshotBitmap = null }) {
                    Text("取消")
                }
            }
        }
    }

}
}

//动态生成一个composable控件
@Composable
fun createBox(rect: Rect?) {
    rect?.let {

        val width = rect.right - rect.left
        val height = rect.bottom - rect.top

        Box(
            modifier = Modifier.size(width.dp, height.dp)
                .padding(start = rect.left.dp, top = rect.top.dp)
                .background(cHalfGrey80717171(), shape = RectangleShape)
        ) {
        }
    }
}


@Composable
fun testShot(){
    var showScreenshot by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { showScreenshot = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            )
        ) {
            Text("开始截图")
        }
    }

    if (showScreenshot) {
//        ScreenshotWindow(
        ShotCapture(
            onDismiss = { showScreenshot = false },
            onSave = { path, thumbnail ->
                // 处理保存后的回调（可选）
            }
        )
    }
}

@Composable
fun testCapture2(){
    TestCapture2()
}