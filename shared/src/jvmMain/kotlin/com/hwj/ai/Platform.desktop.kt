package com.hwj.ai

import com.hwj.ai.global.OsStatus
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

fun checkSystem(): OsStatus {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("mac") -> OsStatus.MACOS
        os.contains("win") -> OsStatus.WINDOWS
        os.contains("nix") || os.contains("nux") || os.contains("ubu") -> OsStatus.LINUX
        else -> OsStatus.UNKNOWN
    }
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
            level = LogLevel.NONE //接口日志屏蔽
            logger = object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
        }
    }
}

