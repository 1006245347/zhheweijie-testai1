package com.hwj.ai.except

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
import com.preat.peekaboo.image.picker.toImageBitmap
import com.preat.peekaboo.ui.camera.PeekabooCamera
import androidx.compose.foundation.Image

@Composable
actual  fun OpenCameraScreen(isOpen:Boolean) {
    val scope = rememberCoroutineScope()
    var images by remember { mutableStateOf(listOf<ImageBitmap>()) }
    var frames by remember { mutableStateOf(listOf<ImageBitmap>()) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showCamera by rememberSaveable { mutableStateOf(false) }
    var showGallery by rememberSaveable { mutableStateOf(false) }

    PeekabooTheme {
        Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                when {
                    showCamera -> {
                        PeekabooCameraView(
                            modifier = Modifier.weight(1f),
                            onBack = { showCamera = false },
                            onCapture = { byteArray ->
                                byteArray?.let {
                                    images = listOf(it.toImageBitmap())
                                }
                                showCamera = false
                            },
                            onFrame = { frame ->
                                frames = frames + frame.toImageBitmap()
                                if (frames.size > 15) {
                                    frames = frames.drop(1)
                                }
                            },
                        )
                        LazyRow(
                            Modifier
                                .heightIn(min = 50.dp)
                                .fillMaxWidth(),
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

                    }
                }
            }
        }
    }
}