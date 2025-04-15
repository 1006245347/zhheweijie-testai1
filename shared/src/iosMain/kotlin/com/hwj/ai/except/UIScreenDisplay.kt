package com.hwj.ai.except

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GeneratingTokens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.hwj.ai.camera.PeekabooCameraView
import com.hwj.ai.camera.PeekabooTheme
import com.hwj.ai.camera.toImageBitmap
import com.hwj.ai.global.PrimaryColor
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.global.printD
import com.hwj.ai.global.workInSub
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.chat.BotCommonMsgMenu
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.compressImage
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import platform.Foundation.NSThread

/**
 * @author by jason-何伟杰，2025/3/11
 * des:拍摄
 */
@Composable
actual fun OpenCameraScreen(isOpen: Boolean, onBack: (Boolean, ByteArray?) -> Unit) {
//    var images by remember { mutableStateOf(listOf<ImageBitmap>()) }
    var images by remember { mutableStateOf<ByteArray?>(null) }
    var frames by remember { mutableStateOf(listOf<ImageBitmap>()) }
    val snackBarHostState = remember { SnackbarHostState() }
//    var showCamera by rememberSaveable { mutableStateOf(isOpen) }
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
                                    byteArray?.let {
                                        images = FileKit.compressImage(it, quality = 70)
                                    }
                                    onBack(false, images) //拍照后返回
                                }
                            },
                            onFrame = { frame ->
//                                frames = frames + frame.toImageBitmap()
//                                if (frames.size > 10) {
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
//                                        contentDescription = "frame",
//                                        contentScale = ContentScale.Crop,
//                                        modifier = Modifier.size(50.dp),
//                                    )
//                                }
//                            }
//                        }
                    }

                    else -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(1) { photo ->
                                AsyncImage(
                                    images,
                                    contentDescription = "Camera photo",
                                    modifier = Modifier.size(300.dp)
                                        .clip(shape = RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
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
actual fun ToolTipCase(modifier: Modifier?,tip: String, content: @Composable () -> Unit) {
    content()
}

actual fun isMainThread(): Boolean {
    return NSThread.isMainThread
}

@Composable
actual fun ScreenShotPlatform(onSave: (String?) -> Unit) {
}

@Composable
actual fun HookSelection(){}

@Composable
actual fun FloatWindow(){}