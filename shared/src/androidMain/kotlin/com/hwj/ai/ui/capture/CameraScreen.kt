package com.hwj.ai.ui.capture

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hwj.ai.except.OpenCameraScreen
import moe.tlaster.precompose.navigation.Navigator

/**
 * @author by jason-何伟杰，2025/3/11
 * des:拍摄界面，启动摄像头
 * 界面跟iOS代码一样的
 */
@Composable
actual fun CameraScreen(navigator: Navigator) {
    var showCamera by rememberSaveable { mutableStateOf(true) }

    Box(Modifier.fillMaxSize()) {
        OpenCameraScreen(showCamera, onBack = { isFinish ->
            showCamera = false
            if (isFinish)
                navigator.goBack()
        })
        if (!showCamera) {
            Row(modifier = Modifier.padding(top=50.dp).align(Alignment.TopCenter)) {
                Button(onClick = {
                    navigator.goBack()
                }, modifier = Modifier.padding(end = 10.dp)) {
                    Text("返回", color = Color.Yellow)
                }
                Button(onClick = {
                    navigator.goBack()
                }) {
                    Text("确定", color = Color.Yellow)
                }
            }
        }
    }
}