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
import androidx.compose.ui.unit.sp
import com.hwj.ai.global.ToastUtils
import com.hwj.ai.global.printD
import kotlinx.coroutines.delay

/**
 * @author by jason-何伟杰，2025/3/18
 * des:没反应？
 */
@Composable
fun ToastHost(modifier: Modifier) {
    val messageState by ToastUtils.messageState.collectAsState()
//    printD("toast>$messageState")
    messageState?.let {
        var visible by remember { mutableStateOf(true) }
        LaunchedEffect(it) {
            delay(1500)
            visible = false
            ToastUtils.dismiss()
        }

//        AnimatedVisibility(visible = visible) {
            Box(
                modifier =modifier
            ) {
                Text(text = it, color = Color.White, fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Center))
//            }
        }
    }
}