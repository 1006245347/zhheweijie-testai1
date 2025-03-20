package com.hwj.ai.capture

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberWindowState
import java.awt.Rectangle
import java.awt.Robot
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

// 新增截图窗口状态管理
class ScreenshotState5 {
    var selectionRect: Rect by mutableStateOf(Rect.Zero)
    var isSelecting by mutableStateOf(false)
}

@Composable
fun ScreenshotOverlay5(
    onCapture: (BufferedImage) -> Unit,
    onCancel: () -> Unit
) {
    val state = remember { ScreenshotState5() }
    val windowState = rememberWindowState(
        position = WindowPosition(0.dp, 0.dp),
        size = WindowSize(Dp.Unspecified, Dp.Unspecified)
    )

    // 全屏透明窗口
    Window(
        onCloseRequest = onCancel,
        state = windowState,
        transparent = true,
        undecorated = true,
        alwaysOnTop = true
    ) {
        Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    state.isSelecting = true
                    state.selectionRect = Rect(offset, offset)
                },
                onDrag = { change, _ ->
                    state.selectionRect = Rect(
                        state.selectionRect.topLeft,
                        change.position
                    )
                },
                onDragEnd = {
                    state.isSelecting = false
                    captureSelectedArea(state.selectionRect, onCapture)
                    onCancel()
                }
            )
        }) {
            // 绘制选区遮罩层
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(Color.Black.copy(alpha = 0.5f))
                drawRect(
                    color = Color.Transparent,
                    topLeft = state.selectionRect.topLeft,
                    size = state.selectionRect.size
                )
            }
        }
    }
}

private fun captureSelectedArea(rect: Rect, onSuccess: (BufferedImage) -> Unit) {
    val image = captureScreenRect(rect)
    image?.let {
        // 保存到剪贴板（参考ChatGPT客户端交互）
//        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
//        clipboard.setContents(ImageTransferable(it), null)
        onSuccess(it)
    }
}

fun captureScreenRect(rect: Rect): BufferedImage? {
    return try {
        val robot = Robot()
        val screenRect = Rectangle(
            rect.left.toInt(),
            rect.top.toInt(),
            rect.width.toInt(),
            rect.height.toInt()
        )
        robot.createScreenCapture(screenRect)
    } catch (e: Exception) {
        null
    }
}

 fun saveToFile(image: BufferedImage):Boolean {
     val file = File("screenshot_${System.currentTimeMillis()}.png")
    return ImageIO.write(image, "PNG", file)
}