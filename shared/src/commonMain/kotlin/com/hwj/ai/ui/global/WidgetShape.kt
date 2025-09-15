package com.hwj.ai.ui.global

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.hwj.ai.global.cBasic

fun Corner8() = RoundedCornerShape(8.dp)
fun Corner12() = RoundedCornerShape(12.dp)
fun CornerX(x:Int)= RoundedCornerShape(x.dp)

//通用样式加载态控件
@Composable
fun CircularLoadIng() {
    CircularProgressIndicator(color = cBasic())

}