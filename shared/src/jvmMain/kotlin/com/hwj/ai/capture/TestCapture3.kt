package com.hwj.ai.capture

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.hwj.ai.global.printD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.File
import javax.imageio.ImageIO


@Composable
fun testCapture3() {
    ScreenshotWindow()
}

@Composable
fun ScreenshotApp(startScreenshot: () -> Unit) {

    Column(modifier = Modifier.fillMaxSize()) {
        Text("点击并拖动以选择裁剪区域")
        Button(onClick = { startScreenshot() }) {
            Text("截图")
        }
    }
}


@Composable
@Preview
fun ScreenshotWindow() {
    var isWindowVisible by remember { mutableStateOf(true) }
    var startPoint by remember { mutableStateOf(Offset(0f, 0f)) }
    var endPoint by remember { mutableStateOf(Offset(0f, 0f)) }
    var isDrawing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun startScreenshot() {
        // 隐藏窗口
        isWindowVisible = false
        isDrawing = true
    }

    fun onScreenshotCompleted() {   // 截图完成后恢复窗口   // 截图完成后恢复窗口
        isWindowVisible = true
        isDrawing = false
    }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isWindowVisible) { //正常显示
            ScreenshotApp(startScreenshot = { startScreenshot() })
        } else { //触发截图
            // 绘制裁剪框
            Canvas(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    if (isDrawing) {
                        endPoint = change.position
                    } else {
                        startPoint = change.position
                        isDrawing = true
                    }
                }
            }) {
                val width = (endPoint.x - startPoint.x).coerceAtLeast(0f)
                val height = (endPoint.y - startPoint.y).coerceAtLeast(0f)


                // 确保矩形的左上角和右下角坐标顺序正确，避免宽高为负
                val topLeft = Offset(
                    x = minOf(startPoint.x, endPoint.x),
                    y = minOf(startPoint.y, endPoint.y)
                )
                val rectWidth = width
                val rectHeight = height

                // 绘制阴影区域
                drawRect(
                    color = Color.Black.copy(alpha = 0.8f),
                    size = size,
                    topLeft = Offset(0f, 0f),
                    style = Stroke(0f)  // 不绘制边框
                )

                // 绘制蓝色边框的裁剪框
                drawRect(
                    color = Color.Blue,
                    topLeft = topLeft,
                    size = androidx.compose.ui.geometry.Size(rectWidth, rectHeight),
                    style = Stroke(width = 4f)
                )
            }
        }

    }
    if (isDrawing) {
        printD("capture33>$startPoint $endPoint")
//        captureScreenshot3(startPoint, endPoint, onScreenshotCompleted = onScreenshotCompleted())
    }
}

fun captureScreenshot3(startPoint: Offset, endPoint: Offset, onScreenshotCompleted: () -> Unit) {
    // 获取屏幕大小
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val robot = Robot()

    // 计算裁剪区域
    val cropWidth = (endPoint.x - startPoint.x).coerceAtLeast(0f)
    val cropHeight = (endPoint.y - startPoint.y).coerceAtLeast(0f)

    // 截取屏幕区域
    val rectangle = Rectangle(
        startPoint.x.toInt(), startPoint.y.toInt(),
        cropWidth.toInt(), cropHeight.toInt()
    )
    val screenshot = robot.createScreenCapture(rectangle)

    // 保存截图
    val file = File("screenshot.png")
    ImageIO.write(screenshot, "PNG", file)

    // 让截图完成后恢复窗口可见
    onScreenshotCompleted()
}

