package com.hwj.ai

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.BasicRichText
import com.halilibo.richtext.ui.CodeBlockStyle
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.RichTextThemeProvider
import com.halilibo.richtext.ui.string.RichTextStringStyle
import com.hwj.ai.global.BackCodeGroundColor
import com.hwj.ai.global.BackCodeTxtColor
import com.hwj.ai.global.BackTxtColor1
import com.hwj.ai.global.BackTxtColor2
import com.hwj.ai.global.DarkColorScheme
import com.hwj.ai.global.LightColorScheme
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.baseHostUrl
import com.hwj.ai.global.isDarkPanel
import com.hwj.ai.global.isDarkTxt
import com.hwj.ai.global.isLightPanel
import com.hwj.ai.global.isLightTxt
import com.hwj.ai.global.printD
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.viewmodel.ChatViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.sse.SSE
import io.ktor.http.URLBuilder
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import moe.tlaster.precompose.koin.koinViewModel

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val os: OsStatus
        get() = OsStatus.ANDROID
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun createHttpClient(timeout: Long?): HttpClient {
    return HttpClient() {
        defaultRequest {
            url.takeFrom(URLBuilder().takeFrom(baseHostUrl))
        }

        install(HttpTimeout) {
            timeout?.let {
                requestTimeoutMillis = timeout
            }
        }
        install(SSE) {
            showCommentEvents()
            showRetryEvents()
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        install(Logging) {
            level = LogLevel.BODY
//            level=LogLevel.HEADERS
//            level= LogLevel.INFO
//            level = LogLevel.NONE //接口日志屏蔽
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    printD(message)
                }
            }
        }
        //允许分块处理
//        expectSuccess = true
    }
}

actual fun checkSystem(): OsStatus {
    return OsStatus.ANDROID
}

@Composable
actual fun setColorScheme(isDark: Boolean): ColorScheme {
    var colorScheme = LightColorScheme
//    printD("isDark=$isDark")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (isDark) {
            colorScheme = dynamicDarkColorScheme(context)
        } else {
            colorScheme = dynamicLightColorScheme(context)
        }
    } else {
        if (isDark) {
            colorScheme = DarkColorScheme
        } else {
            colorScheme = LightColorScheme
        }
    }
    colorScheme.apply {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                (view.context as ComponentActivity).window.statusBarColor =
                    colorScheme.background.toArgb()
                ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = isDark
            }
        }
    }
    return colorScheme
}

@Composable
actual fun BotMessageCard(message: MessageModel) {
//    BotCommonCard(message)    //默认
    TestBotMsgCard1(message)
}

@Composable
private fun TestBotMsgCard1(message: MessageModel) {
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val isDark = chatViewModel.darkState.collectAsState().value
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

    //第一种
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

    //第二
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

//第三
//    val parser =CommonmarkAstNodeParser()
//    RichText( modifier = Modifier.padding(
//        horizontal = 18.dp,
//        vertical = 12.dp
//    ).background(MaterialTheme.colorScheme.onPrimary),
//    style = richTextStyle,
//        ){
//        BasicMarkdown(astNode = parser.parse(message.answer.trimIndent()))
//    }


    //第四  追踪源码查看 RichTextMaterialTheme-》contentColorProvider 修改内部字体颜色，自定义代码颜色
    RichTextThemeProvider(
        contentColorProvider = {
            if (isDark) {
                isDarkTxt()
            } else {

                isLightTxt()
            }
        }
    ) {
        BasicRichText(
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 12.dp
            ).background( if (isDark) isDarkPanel() else isLightPanel()),
            style = richTextStyle,
        ) {
            Markdown(message.answer.trimIndent())
        }
    }
}
