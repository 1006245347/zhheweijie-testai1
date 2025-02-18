package com.hwj.ai.capture

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.hwj.ai.global.printD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.File
import javax.imageio.ImageIO

@Preview
@Composable
fun TestCapture2() {
    var subScope = rememberCoroutineScope()
    var isWindowVisble by remember { mutableStateOf(true) }
    var startPoint by remember { mutableStateOf(Offset(0f, 0f)) }
    var endPoint by remember { mutableStateOf(Offset(0f, 0f)) }

    //截图时窗口隐藏
    fun startScreenShot() {
        subScope.launch {
            isWindowVisble = false

            subScope.launch(Dispatchers.IO) {
                captureScreenshot2(startPoint, endPoint) {
                    isWindowVisble = true
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("点击并拖动以 选择裁剪区域")

        Button(onClick = {
            subScope.launch(Dispatchers.IO) {

            }
        }) {
            Text("截图")
        }
    }
}

@Composable
fun drawShot() {
    var isDrawing by remember { mutableStateOf(false) }
    var startPoint by remember { mutableStateOf(Offset(0f, 0f)) }
    var endPoint by remember { mutableStateOf(Offset(0f, 0f)) }

    Canvas(modifier = Modifier.fillMaxSize()
        .pointerInput(Unit) {
            detectDragGestures { change, drageAmount ->
                if (isDrawing) {
                    endPoint = change.position
                } else {
                    startPoint = change.position
                    isDrawing = true
                }
            }
        }) {
        val cropWidth = (endPoint.x - startPoint.x).coerceAtLeast(0f)
        val cropHeight = (endPoint.y - startPoint.y).coerceAtLeast(0f)

        // 绘制阴影区域
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            size = size,
            topLeft = Offset(0f, 0f),
            style = Stroke(0f)  // 不绘制边框
        )

        // 绘制裁剪区域外的阴影
        drawRect(
            color = Color.Black.copy(alpha = 0.7f),
            topLeft = Offset(0f, 0f),
            size = Size(size.width, startPoint.y)  // 上方阴影
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.7f),
            topLeft = Offset(0f, endPoint.y),
            size = Size(size.width, size.height - endPoint.y) // 下方阴影
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.7f),
            topLeft = Offset(0f, 0f),
            size = Size(startPoint.x, size.height) // 左侧阴影
        )
        drawRect(
            color = Color.Black.copy(alpha = 0.7f),
            topLeft = Offset(endPoint.x, 0f),
            size = Size(size.width - endPoint.x, size.height) // 右侧阴影
        )
        if (isDrawing) {
            drawRect(
                color = Color.Blue,
                topLeft = startPoint,
                size = Size(width = cropWidth, height = cropHeight),
                style = Stroke(width = 4f)
            )
        }
    }
}


fun captureScreenshot(startPoint: Offset, endPoint: Offset) {
    val robot = Robot()
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val rectangle = Rectangle(
        startPoint.x.toInt(), startPoint.y.toInt(),
        (endPoint.x - startPoint.x).toInt(), (endPoint.y - startPoint.y).toInt()
    )

    // 截取指定区域的屏幕截图
    val screenshot = robot.createScreenCapture(rectangle)

    // 获取保存路径
    val file = File("screenshot.png")
    ImageIO.write(screenshot, "PNG", file)

    printD("截图已保存到: ${file.absolutePath}")
}

fun captureScreenshot2(startPoint: Offset, endPoint: Offset, onScreenshotCompleted: () -> Unit) {
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