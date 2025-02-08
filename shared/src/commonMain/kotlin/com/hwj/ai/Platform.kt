package com.hwj.ai

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import org.koin.core.module.Module

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun createHttpClient(timeout: Long?): HttpClient

expect class MultiplatformSettingsWrapper {
    fun createSettings(): Settings
}
