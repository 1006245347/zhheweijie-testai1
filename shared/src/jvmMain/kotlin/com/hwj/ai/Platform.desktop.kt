package com.hwj.ai

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.global.ColorTextGPT
import com.hwj.ai.global.OsStatus
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.chat.BotCommonCard
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
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
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
        }
    }
}

@Composable
actual fun BotMessageCard(message: MessageModel){
//    BotCommonCard(message)
    testBotMsgCard(message)
}

@Composable
fun testBotMsgCard(message: MessageModel){

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

