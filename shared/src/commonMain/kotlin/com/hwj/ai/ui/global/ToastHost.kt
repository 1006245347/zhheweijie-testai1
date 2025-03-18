package com.hwj.ai.ui.global

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hwj.ai.global.ToastUtils
import com.hwj.ai.global.printD
import kotlinx.coroutines.delay

/**
 * @author by jason-何伟杰，2025/3/18
 * des:没反应？
 */
@Composable
fun ToastHost() {
    val messageState by ToastUtils.messageState.collectAsState()
    printD("toast>$messageState")
    messageState?.let {
        var visible by remember { mutableStateOf(true) }
        LaunchedEffect(it) {
            delay(2000)
            visible = false
            ToastUtils.dismiss()
        }

        AnimatedVisibility(visible = visible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 100.dp)
                    .wrapContentHeight()
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
//                    .align(Alignment.Center)
            ) {
                Text(text = it, color = Color.White)
            }
        }
    }
}