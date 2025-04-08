package com.hwj.ai.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GeneratingTokens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hwj.ai.BotMessageCard
import com.hwj.ai.except.BotMsgMenu
import com.hwj.ai.except.ClipboardHelper
import com.hwj.ai.getPlatform
import com.hwj.ai.global.BackCodeGroundColor
import com.hwj.ai.global.BackCodeTxtColor
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.PrimaryColor
import com.hwj.ai.global.ToastUtils
import com.hwj.ai.global.isDarkBg
import com.hwj.ai.global.isDarkPanel
import com.hwj.ai.global.isDarkTxt
import com.hwj.ai.global.isLightBg
import com.hwj.ai.global.isLightPanel
import com.hwj.ai.global.isLightTxt
import com.hwj.ai.global.printD
import com.hwj.ai.global.thinking
import com.hwj.ai.global.workInSub
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.global.GlobalIntent
import com.hwj.ai.ui.viewmodel.ChatViewModel
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

@Composable
fun MessageCard(
    message: MessageModel,
    isHuman: Boolean = false,
    isLast: Boolean = false,
    isLatest: Boolean = false
) {
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val isDark = chatViewModel.darkState.collectAsState().value
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val isFabExpanded by conversationViewModel.isFabExpanded.collectAsState()
    var maxWidth = 260.dp
    if (getPlatform().os == OsStatus.ANDROID || getPlatform().os == OsStatus.IOS) {
        maxWidth = 260.dp
    } else {
        maxWidth = 450.dp
    }

    Column(
        horizontalAlignment = if (isHuman) Alignment.End else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .padding(top = if (isLast) 120.dp else 0.dp)
    ) {
        Box(
            modifier = Modifier
                .widthIn(0.dp, maxWidth) //mention max width here
                .wrapContentHeight()
                .background(
//                    printD("theme>$isDark")
                    if (isHuman) {
                        if (isDark) isDarkBg() else isLightBg()
                    } else {
                        if (isDark) isDarkPanel() else isLightPanel()
                    },
//                    if (isHuman) MaterialTheme.colorScheme.onSecondary else
//                        MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(8.dp)
                ),
        ) {
            if (isHuman) {//人类消息右对齐
                HumanMessageCard(message = message)
            } else { //机器人回复左对齐
                BotMessageCard(message = message)
            }
        }
        if (!isHuman && isLatest && message.answer != thinking && !isFabExpanded) { //最后一条
            BotMsgMenu(message)
        }
    }
}

@Composable
fun HumanMessageCard(message: MessageModel) {
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val isDark = chatViewModel.darkState.collectAsState().value
    Column {
        message.imagePath?.let { imgList ->
            LazyRow {
                items(imgList) { imgS ->
                    val img= PlatformFile(imgS)
                    Box(modifier = Modifier.padding(5.dp).size(60.dp, 50.dp)) {
                        AsyncImage(
                            img.path, contentDescription = img.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        Text(
            text = message.question,
            fontSize = 14.sp,
            color = if (isDark) isDarkTxt() else isLightTxt(),
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
            textAlign = TextAlign.Justify,
        )
    }
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
//        subScope.launch(Dispatchers.Default) { //貌似频繁IO
        val newMsg = message.answer.trimIndent().replace("```java", "`")
            .replace("```", "`")
        answerState.value = newMsg
//            state.setMarkdown(message.answer.trimIndent())
//        state.setMarkdown(newMsg)
//        }
    }

    RichText(
        state = state.apply {
//            subScope.launch(Dispatchers.Default) {
//            val newMsg = message.answer.trimIndent().replace("```java", "`")
//                .replace("```", "`") //两次转义报错？
//                val newMsg = message.answer.trimIndent().replace("```", "`")
//                withContext(Dispatchers.Main) {
//            state.setMarkdown(newMsg)
//                }

            state.setMarkdown(answerState.value)
//            }
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

//不会屏闪，也可现实代码，但是没有代码框，iOS端只要遇到代码就有线程报错日志
@Composable
fun BotCommonCardApp(message: MessageModel) {
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val isDark = chatViewModel.darkState.collectAsState().value
    val subScope = rememberCoroutineScope()

    val state = rememberRichTextState()

    LaunchedEffect(Unit) {
        state.removeLink()
        state.config.codeSpanBackgroundColor = BackCodeGroundColor
        state.config.codeSpanColor = BackCodeTxtColor
        chatViewModel.processGlobal(GlobalIntent.CheckDarkTheme)
        if (!state.isCodeSpan) {
            state.toggleCodeSpan()
        }
//        ```java //无法解析这个 只有  `Code span example` ,但是3点是代码块，一点是行内代码，
    }

    val answerState = remember { mutableStateOf("") }

    LaunchedEffect(message.answer.trimIndent()) {
        subScope.launch(Dispatchers.Default) { //貌似频==================================
            // IO
            val newMsg = message.answer.trimIndent().replace("```java", "`")
                .replace("```", "`")
            answerState.value = newMsg
        }
    }

    RichText(
        state = state.apply {
            state.setMarkdown(answerState.value)
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
}


@Composable
fun BotCommonMsgMenu(message: MessageModel) {
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val subScope = rememberCoroutineScope()
    Row {
        IconButton(onClick = { //复制
//            printD(message.answer)
            conversationViewModel.copyToClipboard(message.answer)
            conversationViewModel.viewModelScope.launch(Dispatchers.Main) {
                ToastUtils.show("复制成功")
            }
        }, modifier = Modifier.padding(start = 15.dp, end = 10.dp)) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = PrimaryColor, modifier = Modifier.size(20.dp)
            )
        }
        IconButton(onClick = { //重新生成
            printD("clickSend>")

//            conversationViewModel.workInSub {
//            subScope.launch {

            conversationViewModel.generateMsgAgain()
//            }
//            }
        }) {
            Icon(
                imageVector = Icons.Default.GeneratingTokens,
                contentDescription = "Generate Again",
                tint = PrimaryColor, modifier = Modifier.size(20.dp)
            )
        }
    }
}
