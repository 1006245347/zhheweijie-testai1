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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.BotMessageCard
import com.hwj.ai.global.BackCodeGroundColor
import com.hwj.ai.global.BackCodeTxtColor
import com.hwj.ai.global.printD
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.global.GlobalIntent
import com.hwj.ai.ui.viewmodel.ChatViewModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val subScope = rememberCoroutineScope()

    val state = rememberRichTextState()

    LaunchedEffect(Unit) {
        state.removeLink()
        state.config.codeSpanBackgroundColor = BackCodeGroundColor
        state.config.codeSpanColor = BackCodeTxtColor
//        state.config.linkColor = Color.Transparent
//        state.config.codeSpanStrokeColor = Color.Transparent
        chatViewModel.processGlobal(GlobalIntent.CheckDarkTheme)
        if (!state.isCodeSpan) {
            state.toggleCodeSpan()
        }
//        ```java //无法解析这个 只有  `Code span example` ,但是3点是代码块，一点是行内代码，
//        printD("isCode>${state.isCodeSpan}")
    }

    val answerState = remember { mutableStateOf("") }

    //用这种刷新会闪屏。。。
    LaunchedEffect(message.answer.trimIndent()) {
//        subScope.launch(Dispatchers.IO) { //貌似频繁IO
//            val newMsg = message.answer.trimIndent().replace("```java", "`")
//                .replace("```", "`")
//            answerState.value=newMsg
//            state.setMarkdown(message.answer.trimIndent())
//        }
    }

    RichText(
        state = state.apply {
            subScope.launch(Dispatchers.IO) {
//                val newMsg = message.answer.trimIndent().replace("```java", "`")
//                    .replace("```", "`") //两次转义报错？
                val newMsg = message.answer.trimIndent().replace("```", "`")
                withContext(Dispatchers.Main) {
                    state.setMarkdown(newMsg)
                }

//                state.setMarkdown(answerState.value)
            }
        },
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
            .background(MaterialTheme.colorScheme.onPrimary),
        color = MaterialTheme.colorScheme.onTertiary,
        style = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onTertiary
        )
    )

//        RichTextEditor( //这个是富文本编辑器
//            state = state.apply {
////               // setMarkdown(message.answer.trimIndent())
////                setText(message.answer.trimIndent())
//            }, modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
////                .background(MaterialTheme.colorScheme.onPrimary),//没效果
//            , textStyle = TextStyle(
//                fontFamily = FontFamily.Default,
//                fontWeight = FontWeight.Normal,
//                fontSize = 13.sp,
//                color = if (isDark) BackTxtColor2 else BackTxtColor1
//            ), colors = RichTextEditorDefaults.richTextEditorColors(
//                containerColor = if (isDark) { //主题色不兼容
//                    BackInnerColor2
//                } else {
//                    BackInnerColor1
//                }
//            )
//        )


    //不解析富文本
//    Text(text = message.answer.trimIndent(), color = Color.Blue)

}

