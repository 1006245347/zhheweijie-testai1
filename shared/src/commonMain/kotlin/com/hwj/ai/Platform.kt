package com.hwj.ai

import androidx.compose.material3.ColorScheme
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

@Composable
expect fun setColorScheme(isDark:Boolean):ColorScheme

@Composable
expect fun BotMessageCard(message: MessageModel)

expect fun checkSystem(): OsStatus
