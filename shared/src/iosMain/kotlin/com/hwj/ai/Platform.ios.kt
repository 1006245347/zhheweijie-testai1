package com.hwj.ai

import androidx.compose.runtime.Composable
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.printD
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.chat.BotCommonCard
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.sse.SSE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val os: OsStatus
        get() = OsStatus.IOS
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun createHttpClient(timeout: Long?): HttpClient {
    return HttpClient(Darwin) {
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
            level = LogLevel.NONE //接口日志屏蔽
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
        }
    }
}

actual fun createSSEClient(): HttpClient {
    return HttpClient {
        install(SSE) {
            showCommentEvents()
            showRetryEvents()
        }

//        install(Logging) {
//            level = LogLevel.BODY
////            level=LogLevel.HEADERS
////            level= LogLevel.INFO
////            level = LogLevel.NONE //接口日志屏蔽
//            logger = object : io.ktor.client.plugins.logging.Logger {
//                override fun log(message: String) {
//                    printD(message)
//                }
//            }
//        }
    }
}

@Composable
actual fun BotMessageCard(message: MessageModel) {
    BotCommonCard(message)
}
