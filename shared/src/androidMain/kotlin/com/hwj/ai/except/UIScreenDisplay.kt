package com.hwj.ai.except

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import com.hwj.ai.agent.QuickAgent
import com.hwj.ai.camera.PeekabooCameraView
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.global.printD
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.chat.BotCommonMsgMenu
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.compressImage
import kotlinx.coroutines.launch

/**
 * @author by jason-何伟杰，2025/3/18
 * des:照相机界面
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
actual fun OpenCameraScreen(isOpen: Boolean, onBack: (Boolean, ByteArray?) -> Unit) {
//    var images by remember { mutableStateOf(listOf<ImageBitmap>()) }
    var images by remember { mutableStateOf<ByteArray?>(null) }
    var frames by remember { mutableStateOf(listOf<ImageBitmap>()) }
    val snackBarHostState = remember { SnackbarHostState() }
    val subScope = rememberCoroutineScope()

    ThemeChatLite {
        Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) }) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                when {
                    isOpen -> {
                        PeekabooCameraView(
                            modifier = Modifier.weight(1f),
                            onBack = { onBack(true, null) },
                            onCapture = { byteArray ->
                                subScope.launch {
                                    byteArray?.let { //压缩下好大17->2.2
                                        images = FileKit.compressImage(it, quality = 70)
                                    }
//                                    images = listOf(it.toImageBitmap())
                                    //这里保存图片？
//                                    FileKit.compressImage(it, quality = 80)
                                    onBack(false, images) //是否finish
                                }
                            },
                            onFrame = { frame ->

//                                frames = frames + frame.toImageBitmap()
//                                frames = (frames + frame.toImageBitmap()).takeLast(2)//只要最新的5个
////                                printD("frames?${frames.size}")
//                                if (frames.size > 2) {
//                                    frames = frames.drop(1)
//                                }
                            },
                        )
//                        LazyRow(
//                            Modifier
//                                .heightIn(min = 50.dp)
//                                .fillMaxWidth()
//                        ) {
//                            items(frames) { image ->
//                                Box {
//                                    Image(
//                                        bitmap = image,
//                                        contentDescription = "Frame",
//                                        contentScale = ContentScale.Crop,
//                                        modifier = Modifier.size(50.dp),
//                                    )
//                                }
//                            }
//                        }
                    }

                    else -> {
                        AsyncImage(
                            images,
                            contentDescription = "Camera photo",
                            modifier = //Modifier.size(300.dp)
                            Modifier.wrapContentSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                }
            }
        }
    }
}

@Composable
actual fun BotMsgMenu(message: MessageModel) {
    BotCommonMsgMenu(message)
}

@Composable
actual fun ToolTipCase(modifier: Modifier?, tip: String, content: @Composable () -> Unit) {
    content()
}

actual fun isMainThread(): Boolean {
    return Looper.getMainLooper().thread == Thread.currentThread()
        .also { printD("thread=${it.name}") }
}

@Composable
actual fun ScreenShotPlatform(onSave: (String?) -> Unit) {
}

@Composable
actual fun HookSelection() {
}

@Composable
actual fun FloatWindow() {
}

@Composable
actual fun BringMainWindowFront(){

}

actual fun getShotCacheDir(): String? {
    return null
}

actual object EnvLoader {
    @SuppressLint("StaticFieldLeak")
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    actual fun loadEnv(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val inputStream = context.assets.open(".env")
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isNotEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
                    val (key, value) = trimmed.split("=", limit = 2)
                    map[key.trim()] = value.trim()
                }
            }
        }
        return map
    }
}

@Composable
actual fun switchUrlByBrowser(url: String) {
    if (Patterns.WEB_URL.matcher(url).matches()) {
        LocalContext.current.startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)))
    }
}

actual suspend fun AgentRunning(input:String){
    QuickAgent(input)
}