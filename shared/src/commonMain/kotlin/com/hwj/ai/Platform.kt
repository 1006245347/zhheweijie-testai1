package com.hwj.ai

import androidx.compose.runtime.Composable
import com.hwj.ai.global.OsStatus
import com.hwj.ai.models.MessageModel
import io.ktor.client.HttpClient

interface Platform {
    val name: String
    val os: OsStatus
}

expect fun getPlatform(): Platform

expect fun createHttpClient(timeout: Long?): HttpClient

expect fun createSSEClient(): HttpClient

@Composable
expect fun BotMessageCard(message: MessageModel)

fun checkSystem(): OsStatus {
//    val os = System.getProperty("os.name").lowercase()
    val os="windows test"
    return when {
        os.contains("mac") -> OsStatus.MACOS
        os.contains("win") -> OsStatus.WINDOWS
        os.contains("nix") || os.contains("nux") || os.contains("ubu") -> OsStatus.LINUX
        else -> OsStatus.UNKNOWN
    }
}