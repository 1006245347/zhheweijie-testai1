package com.hwj.ai.capture

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberWindowState
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ScreenshotState7 {
    var startOffset by mutableStateOf(Offset.Zero)
    var endOffset by mutableStateOf(Offset.Zero)
    val selectionRect: Rect
        get() = Rect(startOffset, endOffset)
    var isSelecting by mutableStateOf(false)
}

@Composable
fun ScreenshotOverlay7(
    onCapture: (BufferedImage) -> Unit,
    onCancel: () -> Unit
) {
    val state = remember { ScreenshotState7() }
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowState = rememberWindowState(
        position = WindowPosition(0.dp, 0.dp),
        size = WindowSize(screenSize.width.dp, screenSize.height.dp)
    )

    Window(
        onCloseRequest = onCancel,
        state = windowState,
        transparent = true,
        undecorated = true,
        alwaysOnTop = true,
        resizable = false
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        state.isSelecting = true
                        state.startOffset = offset
                        state.endOffset = offset
                    },
                    onDrag = { change, _ ->
                        state.endOffset = change.position
                    },
                    onDragEnd = {
                        state.isSelecting = false
                        captureSelectedArea(state.selectionRect, onCapture)
                        onCancel()
                    }
                )
            }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 背景遮罩
                drawRect(Color.Black.copy(alpha = 0.5f))

                // 选区范围和尺寸
                if (state.isSelecting) {
                    val rect = state.selectionRect.normalize()
                    // 半透明填充
                    drawRect(
                        color = Color.White.copy(alpha = 0.3f),
                        topLeft = rect.topLeft,
                        size = rect.size
                    )
                    // 白色描边
                    drawRect(
                        color = Color.White,
                        topLeft = rect.topLeft,
                        size = rect.size,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }
    }
}

/**
 * 截图核心：做坐标归一化、避免负数，防止崩溃
 */
private fun captureSelectedArea(rect: Rect, onSuccess: (BufferedImage) -> Unit) {
    val normalizedRect = rect.normalize()

    val width = normalizedRect.width.toInt()
    val height = normalizedRect.height.toInt()

    if (width <= 0 || height <= 0) return

    // 先获取当前窗口（Compose Window），然后隐藏
    val awtWindow = java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow
    awtWindow?.isVisible = false
    Thread.sleep(100)

    val screenRect = Rectangle(
        normalizedRect.left.toInt(),
        normalizedRect.top.toInt(),
        width,
        height
    )

    try {
        val robot = Robot()
        val image = robot.createScreenCapture(screenRect)
        onSuccess(image)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

 fun saveToFile7(image: BufferedImage) :Boolean{

     val desktopPath = System.getProperty("user.home") + File.separator + "Desktop"
     val file = File(desktopPath, "screenshot_${System.currentTimeMillis()}.png")
     ImageIO.write(image, "PNG", file)
     println("截图已保存到：${file.absolutePath}")
  return   ImageIO.write(image, "PNG", file)
}

/**
 * 扩展方法：统一 start/end，避免负数尺寸
 */
private fun Rect.normalize(): Rect {
    val left = minOf(this.left, this.right)
    val top = minOf(this.top, this.bottom)
    val right = maxOf(this.left, this.right)
    val bottom = maxOf(this.top, this.bottom)
    return Rect(left, top, right, bottom)
}
