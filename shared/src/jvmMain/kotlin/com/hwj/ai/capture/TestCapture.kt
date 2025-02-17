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
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Composable
fun TestCapture() {
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

// 核心状态类
class ScreenshotState {
    var startPos by mutableStateOf(Offset.Zero)    // androidx.compose.ui.geometry.Offset
    var currentPos by mutableStateOf(Offset.Zero)  // androidx.compose.ui.geometry.Offset
    var isSelecting by mutableStateOf(false)       // androidx.compose.runtime.MutableState
    var selectedRect by mutableStateOf(Rect.Zero)  // androidx.compose.ui.geometry.Rect
    var screenshotBitmap by mutableStateOf<ImageBitmap?>(null) // androidx.compose.ui.graphics.ImageBitmap
}


// 窗口实现（包含桌面端特有类型）
@Composable
fun ScreenshotWindow(
    state: ScreenshotState = remember { ScreenshotState() },
    onDismiss: () -> Unit,
    onSave: (path: String, thumbnail: ImageBitmap) -> Unit
) {
    val screenBounds = rememberScreenBounds()
    val density = LocalDensity.current
    val subScope = rememberCoroutineScope()
    Window(
        onCloseRequest = onDismiss,
        state = rememberWindowState().apply {
            position = WindowPosition(0.dp, 0.dp)  // androidx.compose.ui.window.WindowPosition
            size = DpSize(
                screenBounds.width.dp,
                screenBounds.height.dp
            ) // androidx.compose.ui.unit.DpSize
        },
        undecorated = true,
        transparent = true
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f))) {
            // 选区绘制
            Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        state.startPos = offset
                        state.isSelecting = true
                    },
                    onDrag = { _, offset ->
                        state.currentPos = offset
                        state.selectedRect = Rect(state.startPos, offset)
                    },
                    onDragEnd = {
                        state.isSelecting = false
                        subScope.launch {
                            captureSelection(state, screenBounds)
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

// 获取屏幕边界（跨平台实现）
@Composable
fun rememberScreenBounds(): Rectangle {
    val bounds by remember { mutableStateOf(Toolkit.getDefaultToolkit().screenSize) }
    return Rectangle(bounds)
}

// 截图捕获逻辑
private suspend fun captureSelection(
    state: ScreenshotState,
    screenBounds: Rectangle
) {

    val rect = state.selectedRect
    val awtRect = Rectangle(
        rect.left.toInt(),
        rect.top.toInt(),
        rect.width.toInt(),
        rect.height.toInt()
    )

    withContext(Dispatchers.IO) {
        val robot = Robot()
        val capture = robot.createScreenCapture(awtRect)
        state.screenshotBitmap = capture.toComposeImageBitmap()
    }
}

// 保存截图实现
 fun saveScreenshot(
    state: ScreenshotState,
    onSave: (String, ImageBitmap) -> Unit
) {
    val fileDialog = java.awt.FileDialog(null as java.awt.Frame?).apply {
        mode = java.awt.FileDialog.SAVE
        title = "保存截图"
        isVisible = true
    }

    fileDialog.file?.let {
        val outputFile = File(fileDialog.directory, fileDialog.file)
        ImageIO.write(
            state.screenshotBitmap!!.toAwtImage(),
            "png",
            outputFile
        )
        onSave(outputFile.absolutePath, generateThumbnail(state.screenshotBitmap!!))
    }
    state.screenshotBitmap = null
}

// 缩略图生成
private fun generateThumbnail(bitmap: ImageBitmap): ImageBitmap {
    val thumbnailSize = 100.dp
    return bitmap // 实际应添加缩放逻辑
}

// 菜单位置计算
 fun calculateMenuPosition(
    rect: Rect,
    density: androidx.compose.ui.unit.Density
): Offset {
    val screenWidth = with(density) { Toolkit.getDefaultToolkit().screenSize.width.toDp().value }
    val menuWidth = 300.dp.value
    return Offset(
        x = (rect.left + rect.width / 2 - menuWidth / 2).coerceIn(0f, screenWidth - menuWidth),
        y = rect.bottom + 10.dp.value
    )
}

// 扩展函数：ImageBitmap 转 BufferedImage
fun ImageBitmap.toAwtImage(): BufferedImage {
    // 创建 BufferedImage 对象
    val bufferedImage = BufferedImage(
        this.width,
        this.height,
        BufferedImage.TYPE_INT_ARGB
    )

    // 读取像素数据并写入 BufferedImage
    val pixels = IntArray(this.width * this.height)
    this.readPixels(pixels) // 从 Compose ImageBitmap 读取像素

    // 设置像素到 BufferedImage
    bufferedImage.setRGB(0, 0, width, height, pixels, 0, width)
    return bufferedImage
}