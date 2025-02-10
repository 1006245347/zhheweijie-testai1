package com.hwj.ai.capture

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import java.awt.image.BufferedImage
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.io.File
import javax.imageio.ImageIO

//鼠标拖拽绘制矩形
@Composable
fun ScreenshotOverlay(
    fullScreenshot: BufferedImage,
    onCapture: (BufferedImage) -> Unit
) {
    var startPoint by remember { mutableStateOf<Offset?>(null) }
    var endPoint by remember { mutableStateOf<Offset?>(null) }

    Canvas(
        modifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { startPoint = it },
                    onDrag = { change, _ -> endPoint = change.position },
                    onDragEnd = {
                        if (startPoint != null && endPoint != null) {
                            val rect = Rect(startPoint!!, endPoint!!)
                            val croppedImage = cropImage(fullScreenshot, rect)
                            onCapture(croppedImage)
                        }
                    }
                )
            }
    ) {
        if (startPoint != null && endPoint != null) {
            val size = Size(
                width = (endPoint!!.x - startPoint!!.x).coerceAtLeast(1f),
                height = (endPoint!!.y - startPoint!!.y).coerceAtLeast(1f)
            )
            drawRect(
                color = Color.Red.copy(alpha = 0.5f),
                topLeft = startPoint!!,
                size = size
            )
        }
    }
}



fun captureFullScreen(): BufferedImage {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val robot = Robot()
    return robot.createScreenCapture(Rectangle(screenSize))
}

//裁剪选区截图
fun cropImage(original: BufferedImage, rect: Rect): BufferedImage {
    val x = rect.left.toInt()
    val y = rect.top.toInt()
    val width = rect.width.toInt()
    val height = rect.height.toInt()

    return original.getSubimage(x, y, width, height)
}

//生成缩略图
fun createThumbnail(image: BufferedImage, width: Int, height: Int): BufferedImage {
    val resized = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val graphics = resized.createGraphics()
    graphics.drawImage(image, 0, 0, width, height, null)
    graphics.dispose()
    return resized
}

fun saveImage(image: BufferedImage, path: String) {
    ImageIO.write(image, "png", File(path))
}

@Composable
fun runCapture(){
    val screenshot= captureFullScreen()
    ScreenshotOverlay(fullScreenshot = screenshot,
        onCapture = { croppedImage ->
            saveImage(croppedImage,"shot.png")
        })
}



