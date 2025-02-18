package com.hwj.ai.capture

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage

@Composable
fun testCapture4() {
    var isShot = remember { mutableStateOf(false) }
    var imageBmp by remember { mutableStateOf<ImageBitmap?>(null) }
    Column {
        Button(onClick = {
            isShot.value = true
        }) {
            Text("截图")
        }
        if (isShot.value) {
            ScreenshotOverlay(onCaptureComplete = { bmp ->

            }, onCancel = {
                isShot.value = false
                imageBmp = null
            })
        }
    }
}

@Composable
fun ScreenshotOverlay(onCaptureComplete: (ImageBitmap) -> Unit, onCancel: () -> Unit) {
    // 状态管理
    var startOffset by remember { mutableStateOf(Offset.Zero) }
    var currentOffset by remember { mutableStateOf(Offset.Zero) }
    var showSaveButtons by remember { mutableStateOf(false) }

    val screenSize= Toolkit.getDefaultToolkit().screenSize
    val screenWidth = screenSize.width.toFloat()
    val screenHeight = screenSize.height.toFloat()

    // 手势监听
    val dragModifier = Modifier.pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                startOffset = offset
                currentOffset = offset
                showSaveButtons = false // 开始拖动时隐藏按钮
            },
            onDrag = { change, _ ->
                // 限制坐标在屏幕范围内
                val x = change.position.x.coerceIn(0f, screenWidth)
                val y = change.position.y.coerceIn(0f, screenHeight)
                currentOffset = Offset(x, y)
            },
            onDragEnd = {
                showSaveButtons = true // 拖动结束显示按钮
            }
        )
    }

    // 窗口绘制
    Box(modifier = Modifier.fillMaxSize().then(dragModifier)) {
        // 半透明遮罩
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.Black.copy(alpha = 0.5f))

            // 计算矩形区域
            val (left, top) = listOf(startOffset.x, currentOffset.x).sorted()
                .let { it[0] to it[1] }
            val (bottom, right) = listOf(startOffset.y, currentOffset.y).sorted()
                .let { it[0] to it[1] }

            // 绘制阴影
            drawRect(
                brush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.3f)
                ),
                topLeft = Offset(left - 10, top - 10),
                size = Size(right - left + 20, bottom - top + 20),
                blendMode = BlendMode.Darken
            )

            // 绘制选中区域
            drawRect(
                color = Color.White,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(2f)
            )
        }

        // 保存按钮组
        if (showSaveButtons) {
            val buttonOffset = calculateButtonPosition(
                startOffset,
                currentOffset,
                screenWidth,screenHeight
            )

            Box(
                modifier = Modifier
                    .offset { IntOffset(buttonOffset.x.toInt(), buttonOffset.y.toInt()) }
                    .background(
                        color = Color(0xFF444444),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(8.dp)
            ) {
                Row {
                    Button(
                        onClick = {
                            // 截图保存逻辑
                            val rect = calculateSelectionRect(startOffset, currentOffset)
                            val image = captureScreen(rect)
                            onCaptureComplete(image.toComposeImageBitmap())
                        }
                    ) { Text("保存", color = Color.White) }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = onCancel,
//                            colors = ButtonDefaults.buttonColors(
//                                backgroundColor = Color.Red.copy(alpha = 0.8f)
//                            )
                    ) { Text("取消", color = Color.White) }
                }
            }
        }
    }

}

fun captureScreen(rect: Rect): BufferedImage {
    return Robot().createScreenCapture(
        Rectangle(
            rect.left.toInt(),
            rect.top.toInt(),
            (rect.right - rect.left).toInt(),
            (rect.bottom - rect.top).toInt()
        )
    )
}

fun calculateButtonPosition(start: Offset, end: Offset, screenWidth: Float,screenHeight:Float): Offset {
    val centerX = (start.x + end.x) / 2
    val y = maxOf(start.y, end.y) + 10 // 底部偏移10px

    // 防止按钮超出屏幕
    return Offset(
        x = centerX.coerceIn(60f, screenWidth - 60f), // 按钮宽度约120px
        y = y.coerceIn(0f, screenHeight - 100f) // 按钮高度约36px
    )
}

fun calculateSelectionRect(start: Offset, end: Offset): Rect {
    val left = minOf(start.x, end.x)
    val top = minOf(start.y, end.y)
    val right = maxOf(start.x, end.x)
    val bottom = maxOf(start.y, end.y)
    return Rect(left, top, right, bottom)
}