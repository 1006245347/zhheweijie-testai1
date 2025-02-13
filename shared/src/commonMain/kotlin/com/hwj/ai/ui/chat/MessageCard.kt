package com.chatgptlite.wanted.ui.conversations.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.global.BackGroundMessageGPT
import com.hwj.ai.global.BackGroundMessageHuman
import com.hwj.ai.global.ColorTextGPT
import com.hwj.ai.global.ColorTextHuman
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.models.MessageModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor

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
                    if (isHuman) BackGroundMessageHuman else BackGroundMessageGPT,
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
        color = ColorTextHuman,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
        textAlign = TextAlign.Justify,
    )
}

@Composable
fun BotMessageCard(message: MessageModel) {
    //在desktop存在崩溃
//    val state = rememberRichTextState()
//    ThemeChatLite {
//        RichTextEditor(
//            state = state,
//            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
//            textStyle = TextStyle(
//                fontFamily = FontFamily.Default,
//                fontWeight = FontWeight.Normal,
//                fontSize = 13.sp,
//                color = ColorTextGPT
//            ),
//        )
//    }
//    state.setMarkdown(message.answer.trimIndent())


    Text(text=message.answer, fontSize = 13.sp,color= ColorTextGPT,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp))

//    val state = rememberRichTextState()
//    ThemeChatLite {
//        RichTextEditor(
//            state = state.apply {
//                setText(message.answer.trimIndent())
//            }, modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
//            textStyle = TextStyle(
//                fontFamily = FontFamily.Default,
//                fontWeight = FontWeight.Normal,
//                fontSize = 13.sp,
//                color = ColorTextGPT
//            )
//        )
//    }
}

