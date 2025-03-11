package com.hwj.ai.except

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.hwj.ai.camera.PeekabooCameraView
import com.hwj.ai.camera.PeekabooTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.hwj.ai.camera.toImageBitmap
import com.hwj.ai.global.printD

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
actual fun OpenCameraScreen(isOpen: Boolean, onBack: (Boolean) -> Unit) {
    var images by remember { mutableStateOf(listOf<ImageBitmap>()) }
    var frames by remember { mutableStateOf(listOf<ImageBitmap>()) }
    val snackBarHostState = remember { SnackbarHostState() }
//    var showCamera by rememberSaveable { mutableStateOf(false) }
    PeekabooTheme {
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
                            onBack = { onBack(true) },
                            onCapture = { byteArray ->
                                byteArray?.let {
                                    images = listOf(it.toImageBitmap())
                                }
                                onBack(false)
                            },
                            onFrame = { frame ->
                                frames = frames + frame.toImageBitmap()
                                if (frames.size > 10) {
                                    frames = frames.drop(1)
                                }
                            },
                        )
                        printD("size>${frames.size}")
                        LazyRow(
                            Modifier
                                .heightIn(min = 50.dp)
                                .fillMaxWidth()
                        ) {
                            items(frames) { image ->
                                Box {
                                    Image(
                                        bitmap = image,
                                        contentDescription = "frame",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(50.dp),
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            items(images) { photo ->
                                Image(
                                    bitmap = photo,
                                    contentDescription = "camera photo",
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