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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowSize
import androidx.compose.ui.window.rememberWindowState
import com.hwj.ai.global.NotificationsManager
import com.hwj.ai.global.printD
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

//UI 拖拽得到的坐标 * scaleFactor ≠ 实际屏幕坐标
//macOS 多屏/高DPI 下 Robot 截图区域
//效果suc,windows/macOS 主屏幕
class ScreenshotState10 {
    var startOffset by mutableStateOf(Offset.Zero)
    var endOffset by mutableStateOf(Offset.Zero)
    val selectionRect: Rect
        get() = Rect(startOffset, endOffset)
    var isSelecting by mutableStateOf(false)
}

@Composable
fun ScreenshotOverlay10(
    onCapture: (BufferedImage) -> Unit,
    onCancel: () -> Unit
) {
    val state = remember { ScreenshotState10() }
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowState = rememberWindowState(
        position = WindowPosition(0.dp, 0.dp),
//        size = WindowSize(screenSize.width.dp, screenSize.height.dp)
        size = DpSize(screenSize.width.dp, screenSize.height.dp)
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

private fun captureSelectedArea(rect: Rect, onSuccess: (BufferedImage) -> Unit) {
    val normalizedRect = rect.normalize()

    val screenDevices = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
    var targetDevice: java.awt.GraphicsDevice? = null

//    // 找到选区落在哪块屏幕上
    for (device in screenDevices) {
        val bounds = device.defaultConfiguration.bounds
        if (bounds.contains(normalizedRect.left.toInt(), normalizedRect.top.toInt())) {
            targetDevice = device
            break
        }
    }

    //多屏不让用
//    if (screenDevices.size>1){
//        NotificationsManager().showNotification("不支持多屏截图！","不支持多屏截图")
//        return
//    }

//    targetDevice=screenDevices[0]

    if (targetDevice == null) {
        targetDevice =
            java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice
    }

    val config = targetDevice!!.defaultConfiguration
    val screenBounds = config.bounds // 屏幕偏移（多屏布局下重要）
    val transform = config.defaultTransform
    val scaleX = transform.scaleX
    val scaleY = transform.scaleY
//    printD("屏幕 bounds: $screenBounds, scaleX: $scaleX, scaleY: $scaleY")

    // 关键：Compose 逻辑坐标 → 物理像素坐标 ,超级大坑，用chatgpt写代码一直反馈是乘scaleX,实际是除以，不然容易黑屏
    val captureX = (normalizedRect.left / scaleX).toInt()
    val captureY = (normalizedRect.top / scaleY).toInt()
    val captureW = (normalizedRect.width / scaleX).toInt()
    val captureH = (normalizedRect.height / scaleY).toInt()

//    printD("最终截图区域 (物理像素): x=$captureX, y=$captureY, w=$captureW, h=$captureH")

    if (captureW <= 0 || captureH <= 0) return

    try {
        // 隐藏截图窗口，防止蒙层被截进去
        val awtWindow = java.awt.KeyboardFocusManager.getCurrentKeyboardFocusManager().activeWindow
        awtWindow?.isVisible = false
        Thread.sleep(100) // 等隐藏生效

        val robot = Robot(targetDevice)
        val screenRect = Rectangle(captureX, captureY, captureW, captureH)
        val image = robot.createScreenCapture(screenRect)

        onSuccess(image)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun saveToFile10(image: BufferedImage): Boolean {

//     val desktopPath = System.getProperty("user.home") + File.separator + "Desktop"
//     val file = File(desktopPath, "screenshot_${System.currentTimeMillis()}.png")

    val cacheDir = getPlatformCacheImgDir()
    if (!cacheDir.exists()) cacheDir.mkdirs()

    val file = File(cacheDir, "screenshot_${System.currentTimeMillis()}.png")

    ImageIO.write(image, "PNG", file)
    println("截图已保存到：${file.absolutePath}")
    return ImageIO.write(image, "PNG", file)
}

//截图已保存到缓存目录：/Users/你的用户名/Library/Caches/com.hwj.ai.capture/screenshot_1710918988888.png
//截图已保存到缓存目录：C:\Users\你的用户名\AppData\Local\com.hwj.ai.capture\cache\screenshot_1710918988888.png
//截图已保存到缓存目录：/home/你的用户名/.cache/com.hwj.ai.capture/screenshot_1710918988888.png
private fun getPlatformCacheImgDir(): File {
    val osName = System.getProperty("os.name").lowercase()
    return if (osName.contains("mac")) {
        File(System.getProperty("user.home"), "Library/Caches/com.hwj.ai.capture")
    } else if (osName.contains("win")) {
        File(System.getenv("LOCALAPPDATA"), "com.hwj.ai.capture/cache")
    } else {
        File(System.getProperty("user.home"), ".cache/com.hwj.ai.capture")
    }
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