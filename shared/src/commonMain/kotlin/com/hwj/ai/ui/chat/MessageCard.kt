package com.hwj.ai.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.BotMessageCard
import com.hwj.ai.global.BackCodeGroundColor
import com.hwj.ai.global.BackCodeTxtColor
import com.hwj.ai.global.BackInnerColor1
import com.hwj.ai.global.BackInnerColor2
import com.hwj.ai.global.BackTxtColor1
import com.hwj.ai.global.BackTxtColor2
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.global.GlobalIntent
import com.hwj.ai.ui.viewmodel.ChatViewModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun MessageCard(message: MessageModel, isHuman: Boolean = false, isLast: Boolean = false) {
    Column(
        horizontalAlignment = if (isHuman) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(top = if (isLast) 120.dp else 0.dp)
    ) {
        Box(
            modifier = Modifier
                .widthIn(0.dp, 260.dp) //mention max width here
                .wrapContentHeight()
                .background(
                    if (isHuman) MaterialTheme.colorScheme.onSecondary else
                        MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(12.dp)
                ),
        ) {
            if (isHuman) {//人类消息右对齐
                HumanMessageCard(message = message)
            } else { //机器人回复左对齐
                BotMessageCard(message = message)
            }
        }
    }
}

@Composable
fun HumanMessageCard(message: MessageModel) {
    Text(
        text = message.question,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onTertiary,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
        textAlign = TextAlign.Justify,
    )
}

@Composable
fun BotCommonCard(message: MessageModel) {
    //在desktop用这种方式存在崩溃
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val isDark = chatViewModel.darkState.collectAsState().value

    val state = rememberRichTextState()

    LaunchedEffect(Unit) {
        state.config.codeSpanBackgroundColor = BackCodeGroundColor
        state.config.codeSpanColor = BackCodeTxtColor
        chatViewModel.processGlobal(GlobalIntent.CheckDarkTheme)
    }

    ThemeChatLite {
        RichTextEditor(
            state = state.apply {
                setMarkdown(message.answer.trimIndent())
//                setText(message.answer.trimIndent())
            }, modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
//                .background(MaterialTheme.colorScheme.onPrimary),//没效果
            , textStyle = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = if (isDark) BackTxtColor2 else BackTxtColor1
            ), colors = RichTextEditorDefaults.richTextEditorColors(
                containerColor = if (isDark) { //主题色不兼容
                    BackInnerColor2
                } else {
                    BackInnerColor1
                }
            )
        )
    }
}

