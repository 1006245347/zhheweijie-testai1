package com.hwj.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.CodeBlockStyle
import com.halilibo.richtext.ui.RichTextStyle
import com.hwj.ai.global.ColorTextGPT
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.global.baseHostUrl
import com.hwj.ai.global.printD
import com.hwj.ai.models.MessageModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
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

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
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

@Composable
actual fun BotMessageCard(message: MessageModel) {
    //默认
//    BotCommonCard(message)
//    testBotMsgCard1(message)
//    testBotMsgCard2(message)
    testBotMsgCard3(message)
}

@Composable
fun testBotMsgCard1(message: MessageModel) {
    printD("answer>${message.answer.trimIndent()}")
    var richTextStyle = RichTextStyle(
        codeBlockStyle = CodeBlockStyle(
            textStyle = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = ColorTextGPT
            ),
            wordWrap = true,
            modifier = Modifier.background(
                color = Color.Black,
                shape = RoundedCornerShape(6.dp)
            )
        )
    )
//    var textState= rememberTextFieldState(message.answer.trimIndent())
    ThemeChatLite {
        com.halilibo.richtext.ui.material.RichText(
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 12.dp
            ),
            style = richTextStyle,
        ) {
//            Markdown(content = textState.text.toString())
            Markdown(message.answer.trimIndent())
        }
    }
}

@Composable
fun testBotMsgCard2(message: MessageModel) {

    val state = rememberRichTextState()
    ThemeChatLite {
        RichTextEditor(
            state = state,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            textStyle = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = ColorTextGPT
            ),
        )
    }
    state.setMarkdown(message.answer.trimIndent())
}

@Composable
fun testBotMsgCard3(message: MessageModel) {
    Text(
        text = message.answer, fontSize = 13.sp, color = ColorTextGPT,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
    )
}