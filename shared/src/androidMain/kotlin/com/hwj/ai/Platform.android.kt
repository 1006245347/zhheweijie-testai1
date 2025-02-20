package com.hwj.ai

import android.content.Context
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
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.global.baseHostUrl
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.chat.BotCommonCard
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
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
    return HttpClient (CIO){
        defaultRequest {
            url.takeFrom(URLBuilder().takeFrom(baseHostUrl))
        }

        install(HttpTimeout) {
            timeout?.let {
                requestTimeoutMillis = timeout
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        install(Logging) {
//                level = LogLevel.BODY
//            level=LogLevel.HEADERS
//            level= LogLevel.INFO
            level = LogLevel.NONE //接口日志屏蔽
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
        }
        //允许分块处理
        expectSuccess=true
    }
}

@Composable
actual fun BotMessageCard(message: MessageModel){
    //默认
//    BotCommonCard(message)

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

