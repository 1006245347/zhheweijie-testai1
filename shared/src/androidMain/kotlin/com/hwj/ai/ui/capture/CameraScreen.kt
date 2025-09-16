package com.hwj.ai.ui.capture

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hwj.ai.except.OpenCameraScreen
import com.hwj.ai.global.PrimaryColor
import com.hwj.ai.global.createImageName
import com.hwj.ai.global.printD
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.saveImageToGallery
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator

/**
 * @author by jason-何伟杰，2025/3/11
 * des:Android拍摄界面，启动摄像头
 * 界面跟iOS代码一样的
 */
@Composable
actual fun CameraScreen(navigator: Navigator) {
    var showCamera by rememberSaveable { mutableStateOf(true) }
//    val conversationViewModel= koinViewModel(ConversationViewModel::class)
    val subScope = rememberCoroutineScope()
    var image by remember { mutableStateOf<ByteArray?>(null) }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
        OpenCameraScreen(showCamera, onBack = { isFinish, imageArray ->
            showCamera = false
            image = imageArray
            if (isFinish)
                navigator.goBack()
        })
        if (!showCamera) {
            Row(modifier = Modifier.padding(bottom = 50.dp).align(Alignment.BottomCenter)) {
                Button(
                    onClick = {
                        navigator.goBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    modifier = Modifier.padding(end = 20.dp)
                ) {
                    Text("返回", color = Color.White)
                }
                Button(onClick = {
                    if (image != null) {
                        subScope.launch { //保存图片
                            val pic = PlatformFile(FileKit.filesDir, createImageName())
                            pic.write(image!!)
                            FileKit.saveImageToGallery(file = pic)//存到图册
                            printD("pic>${pic.path}") //私有目录 /data/user/0/com.hwj.ai.android/files/ai_1742345072737.jpg
//                            conversationViewModel.addCameraImage(pic) //这里viewModel必须是单例
                            navigator.goBackWith(pic.path)
                        }
                    } else {
                        navigator.goBack()
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) {
                    Text("确定", color = Color.White)
                }
            }
        }
    }
}