package com.hwj.ai.except

import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GeneratingTokens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.global.PrimaryColor
import com.hwj.ai.global.printD
import com.hwj.ai.global.workInSub
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import moe.tlaster.precompose.koin.koinViewModel

@Composable
actual fun OpenCameraScreen(isOpen: Boolean, onBack: (Boolean, ByteArray?) -> Unit) {
}

@Composable
actual fun BotMsgMenu(message: MessageModel) {
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    Row {
        TooltipArea(
            tooltip = {
                Surface(modifier = Modifier.padding(2.dp)) {
                    Text(text = "复制", Modifier.padding(4.dp), fontSize = 12.sp)
                }
            },
            delayMillis = 100,
            tooltipPlacement = TooltipPlacement.CursorPoint(offset = DpOffset(5.dp, 5.dp))
        ) {
            IconButton(onClick = { //复制
                printD(message.answer)
            }, modifier = Modifier.padding(start = 15.dp, end = 10.dp)) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = PrimaryColor, modifier = Modifier.size(20.dp)
                )
            }
        }

        TooltipArea(
            tooltip = { //鼠标移动浮动指向提示
                Surface(modifier = Modifier.padding(2.dp)) {
                    Text(text = "重新生成", Modifier.padding(4.dp), fontSize = 12.sp)
                }
            },
            delayMillis = 100,
            tooltipPlacement = TooltipPlacement.CursorPoint(offset = DpOffset(5.dp, 5.dp))
        ) {
            IconButton(onClick = { //重新生成
                conversationViewModel.workInSub {
                    conversationViewModel.generateMsgAgain()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.GeneratingTokens,
                    contentDescription = "Generate Again",
                    tint = PrimaryColor, modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
actual fun ToolTipCase(tip: String, content: @Composable () -> Unit) {
    TooltipArea(
        tooltip = { //鼠标移动浮动指向提示
            Surface(modifier = Modifier.padding(2.dp)) {
                Text(text = tip, Modifier.padding(4.dp), fontSize = 12.sp)
            }
        },
        delayMillis = 100,
        tooltipPlacement = TooltipPlacement.CursorPoint(offset = DpOffset(5.dp, 5.dp))
    ) {
        content()
    }
}


actual fun isMainThread(): Boolean {
    return Thread.currentThread().name == "main"
}