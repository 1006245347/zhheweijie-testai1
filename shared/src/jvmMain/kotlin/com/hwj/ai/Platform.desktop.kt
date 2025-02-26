package com.hwj.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ColorScheme
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
import com.halilibo.richtext.ui.RichTextScope
import com.halilibo.richtext.ui.RichTextStyle
import com.hwj.ai.data.local.SettingsFactory
import com.hwj.ai.global.ColorTextGPT
import com.hwj.ai.global.DarkColorScheme
import com.hwj.ai.global.LightColorScheme
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.global.printD
import com.hwj.ai.models.MessageModel
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.russhwolf.settings.coroutines.FlowSettings
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

actual fun createHttpClient(timeout: Long?): HttpClient {
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
        install(Logging) {
//                level = LogLevel.ALL
            level = LogLevel.INFO //接口日志屏蔽
//            logger = object : Logger {
//                override fun log(message: String) {
//                    println(message)
//                }
//            }
        }
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
//    testBotMsgCard(message)
    testBotMsgCard2(message)
//    testBotMsgCard3(message)
}

@Composable
fun testBotMsgCard(message: MessageModel) {

    //是因为一次性数据太多导致的蹦?
    val state = rememberRichTextState()
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

    state.setMarkdown(message.answer.trimIndent())
}

@Composable
fun testBotMsgCard2(message: MessageModel) {
//    printD("msg>${message.answer.trimIndent()}")
    var richTextStyle = RichTextStyle(
        codeBlockStyle = CodeBlockStyle(
            textStyle = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = ColorTextGPT
            ),
            wordWrap = true,
            modifier = Modifier.background(color = Color.Black, shape = RoundedCornerShape(6.dp))
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
fun testBotMsgCard3(message: MessageModel) {
    Text(
        text = message.answer, fontSize = 13.sp, color = ColorTextGPT,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
    )
}

