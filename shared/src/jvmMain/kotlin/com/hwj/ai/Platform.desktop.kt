package com.hwj.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.BasicRichText
import com.halilibo.richtext.ui.CodeBlockStyle
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.RichTextThemeProvider
import com.halilibo.richtext.ui.string.RichTextStringStyle
import com.hwj.ai.data.local.PermissionPlatform
import com.hwj.ai.global.BackCodeGroundColor
import com.hwj.ai.global.BackCodeTxtColor
import com.hwj.ai.global.DarkColorScheme
import com.hwj.ai.global.LightColorScheme
import com.hwj.ai.global.OsStatus
import com.hwj.ai.models.MessageModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.sse.SSE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class DesktopPlatform : Platform {
    override val name: String
        get() = "desktop> ${System.getProperty("os.name")}"
    override val os: OsStatus
        get() = checkSystem()
}


actual fun getPlatform(): Platform = DesktopPlatform()

actual fun createKtorHttpClient(timeout: Long?): HttpClient {
    return HttpClient {
        install(HttpTimeout) {
            timeout?.let {
                requestTimeoutMillis = it
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true

            })
        }
        install(SSE)
        install(Logging) {
//                level = LogLevel.ALL
            level = LogLevel.BODY //接口日志屏蔽
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
        }
    }
}


actual fun checkSystem(): OsStatus {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("mac") -> OsStatus.MACOS
        os.contains("win") -> OsStatus.WINDOWS
        os.contains("nix") || os.contains("nux") || os.contains("ubu") -> OsStatus.LINUX
        else -> OsStatus.UNKNOWN
    }
}

@Composable
actual fun setColorScheme(isDark: Boolean): ColorScheme {
    return if (!isDark) {
        LightColorScheme
    } else {
        DarkColorScheme
    }
}

@Composable
actual fun BotMessageCard(message: MessageModel) {
//    BotCommonCard(message)//默认
    TestBotMsgCard1(message)
}

@Composable
private fun TestBotMsgCard1(message: MessageModel) {
//    val chatViewModel = koinViewModel(ChatViewModel::class)
//    val isDark = chatViewModel.darkState.collectAsState().value
//    var pointCount by remember { mutableStateOf(1) }
//    val subScope = rememberCoroutineScope()
//
//    val animatedPointCount by animateIntAsState(
//        targetValue = if (message.answer == thinking) pointCount else 1,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 1000),
//            repeatMode = RepeatMode.Restart
//        )
//    )
//
//    LaunchedEffect(key1 = message.answer) {
//        if (message.answer == thinking) {
//            while (thinking == message.answer) {
//                subScope.launch {
//                    delay(1000) // 每隔1秒更新一次点数
//                    pointCount = when (pointCount) {
//                        1 -> 2
//                        2 -> 3
//                        else -> 1
//                    }
//                }
//            }
//        } else {
//            pointCount = 1 // 确保在非思考状态下点数为1
//        }
//    }

    val richTextStyle = RichTextStyle(
        codeBlockStyle = CodeBlockStyle(
            textStyle = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = BackCodeTxtColor,

                ),
            wordWrap = true,
            modifier = Modifier.background(
                color = BackCodeGroundColor,
                shape = RoundedCornerShape(6.dp)
            )
        ),
        stringStyle = RichTextStringStyle()
    )

//    com.halilibo.richtext.ui.material.RichText(
//        modifier = Modifier.padding(
//            horizontal = 18.dp,
//            vertical = 12.dp
//        ).background(MaterialTheme.colorScheme.onPrimary),
//        style = richTextStyle,
//
//        ) {
//        //字体颜色对了，但是没能解析富文本的符合
////            Text(message.answer.trimIndent(), color = MaterialTheme.colorScheme.onTertiary)
//
//        //没能改字体颜色
//        Markdown(message.answer.trimIndent())
//    }

//    val richTextState = rememberRichTextState()
//    richTextState.setMarkdown(message.answer.trimIndent())
//    richTextState.config.codeSpanBackgroundColor= BackCodeGroundColor
//    richTextState.config.codeSpanColor= BackCodeTxtColor
//    ThemeChatLite {
//        RichTextEditor(
//            modifier = Modifier.padding(
//                horizontal = 18.dp,
//                vertical = 12.dp
//            ).background(MaterialTheme.colorScheme.onPrimary), state = richTextState,
//            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onTertiary)
//        )


//    val parser =CommonmarkAstNodeParser()
//    RichText( modifier = Modifier.padding(
//        horizontal = 18.dp,
//        vertical = 12.dp
//    ).background(MaterialTheme.colorScheme.onPrimary),
//    style = richTextStyle,
//        ){
//        BasicMarkdown(astNode = parser.parse(message.answer.trimIndent()))
//    }

    //追踪源码查看 RichTextMaterialTheme-》contentColorProvider
    RichTextThemeProvider(
        contentColorProvider = { MaterialTheme.colorScheme.onTertiary }
    ) {
        BasicRichText(
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 12.dp
            ).background(MaterialTheme.colorScheme.onPrimary),
            style = richTextStyle,
        ) {
//            if (thinking == message.answer) {
//                val dots=".".repeat(animatedPointCount)
//                Markdown("思考中$dots")
//            } else {
//            }
                Markdown(message.answer.trimIndent())
        }
    }

}

@Composable
actual fun createPermission(
    permission: PermissionPlatform,
    grantedAction: () -> Unit,
    deniedAction: () -> Unit
) {
    grantedAction()
}

