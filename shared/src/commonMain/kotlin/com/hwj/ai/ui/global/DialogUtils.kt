package com.hwj.ai.ui.global

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.global.cBlue014AA7
import com.hwj.ai.global.cBlue629DE8
import com.hwj.ai.global.cDark99000000
import com.hwj.ai.global.cGrey333333
import com.hwj.ai.global.cGrey666666
import com.hwj.ai.global.cTransparent
import com.hwj.ai.global.cWhite
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DialogUtils {

    /**通用提示*/
    @Composable
    fun GlobalTigDialog(
        isShow: MutableState<Boolean>,
        content: String,
        subScope: CoroutineScope, thread: CoroutineDispatcher = Dispatchers.Main,
        onAction: () -> Unit
    ) {
        CreateDialog(
            isShow,
            173.dp,
            13.dp,
            0.dp,
            13.dp,
            13.dp,
            RoundedCornerShape(15.dp),
            panelBackground = cTransparent()
        ) {
            val bgColor = MaterialTheme.colorScheme.background
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "提示",
                    color = cGrey333333(),
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.TopCenter)
                        .offset(0.dp, 23.dp)
                )
                Text(
                    text = content,
                    color = cGrey666666(),
                    fontSize = 15.sp,
                    modifier = Modifier.align(Alignment.TopCenter)
                        .offset(0.dp, 57.dp)
                )
                Card(
                    modifier = Modifier.size(140.dp, 37.dp)
                        .offset(22.dp, 120.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            isShow.value = false
                        },
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(1.dp, cBlue629DE8()),
                    colors = CardColors(bgColor, bgColor, bgColor, bgColor)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "取消",
                            color = cBlue014AA7(),
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                Card(
                    modifier = Modifier.size(140.dp, 37.dp)
                        .align(Alignment.TopEnd)
                        .offset((-22).dp, 120.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            subScope.launch(thread) {
                                isShow.value = false
                                onAction()
                            }
                        },
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(1.dp, cBlue014AA7()),
                    colors = CardColors(cBlue014AA7(), cBlue014AA7(), cBlue014AA7(), cBlue014AA7())
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "确定",
                            color = cWhite(),
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }

    //确保放在组件的最后以实现蒙层效果
    @Composable
    fun CreateDialog(
        openState: MutableState<Boolean>,
        dialogHeight: Dp = 300.dp,
        marginStart: Dp = 10.dp,
        marginTop: Dp = 0.dp,
        marginEnd: Dp = 10.dp,
        marginBottom: Dp = 10.dp,
        dialogRoundedCorner: RoundedCornerShape = RoundedCornerShape(7.dp),
        dialogAlignment: Alignment = Alignment.TopCenter,
        paddingStart: Dp = 0.dp,
        paddingTop: Dp = 0.dp,
        paddingEnd: Dp = 0.dp,
        paddingBottom: Dp = 0.dp,
        dialogDismiss: Boolean = true,
        dialogBackground: Color = MaterialTheme.colorScheme.background,
        panelBackground: Color = cDark99000000(),
        block: @Composable () -> Unit
    ) {
        var showDialog by openState
        if (showDialog) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(panelBackground)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    showDialog = !dialogDismiss
                }
                .padding(marginStart, marginTop, marginEnd, marginBottom)
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(dialogHeight)
                    .clip(dialogRoundedCorner)
                    .align(dialogAlignment)
                    .background(dialogBackground)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { }
                    .padding(paddingStart, paddingTop, paddingEnd, paddingBottom)
                ) {
                    block()
                }
            }
        }
    }
}