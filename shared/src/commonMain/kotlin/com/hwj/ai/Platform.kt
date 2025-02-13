package com.hwj.ai

import com.hwj.ai.global.OsStatus
import io.ktor.client.HttpClient

interface Platform {
    val name: String
    val os: OsStatus
}

expect fun getPlatform(): Platform

expect fun createHttpClient(timeout: Long?): HttpClient


