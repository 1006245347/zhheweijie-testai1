package com.hwj.ai.ui.capture

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.hwj.ai.checkSystem
import com.hwj.ai.global.NotificationsManager
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.printD
import ir.mahozad.multiplatform.comshot.captureToImage
import kotlinx.coroutines.delay
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import org.jetbrains.compose.resources.painterResource
import testai1.shared.generated.resources.Res
import testai1.shared.generated.resources.ic_big_logo
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTimedValue

@Composable
fun CaptureFetch() {
    if (checkSystem() == OsStatus.WINDOWS) {
        testWindows()
    } else if (checkSystem() == OsStatus.MACOS) {
        testWindows()
    }else{
        printD("capture>null")
    }
}


@Composable
fun testWindows() {
    // To check UI responsiveness
    var counter by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            counter++
            delay(100.milliseconds)
        }
    }
    var time by remember { mutableStateOf<Duration?>(null) }
    var image by remember { mutableStateOf<ImageBitmap?>(null) }
    var padding by remember { mutableIntStateOf(1) }
    val composable: @Composable () -> Unit = remember {
        @Composable {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = ">".repeat(padding))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(Res.drawable.ic_big_logo),
                        modifier = Modifier.size(200.dp),
                        contentDescription = null
                    )
                    Text("Photo by hwj on Unsplash")
                    Button({}) { Text("Example button $padding") }
                }
            }
        }
    }
    Column {
        Text(text = "Counter to check responsiveness: $counter")
        Button(
            onClick = {
                val timedValue = measureTimedValue { captureToImage(composable) }
                time = timedValue.duration
                image = timedValue.value
                padding++
                // ImageIO.write(image.toAwtImage(), "PNG", Path("output.png").outputStream())
            }
        ) {
            Text(text = "Capture ${if (time != null) "(Last one took $time)" else ""}")
        }
        image?.let { Image(it, contentDescription = null) }
    }
}
