package com.hwj.ai

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLBuilder
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

 actual fun getPlatform(): Platform = AndroidPlatform()

 actual fun createHttpClient(timeout: Long?): HttpClient {
    return HttpClient {
        defaultRequest {
            url.takeFrom(URLBuilder().takeFrom("http://wfserver.gree.com/"))
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
            level= LogLevel.INFO
//            level = LogLevel.NONE //接口日志屏蔽
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
        }
    }
}

actual class MultiplatformSettingsWrapper(private val context: Context) {

    actual fun createSettings(): Settings {
        val delegate = context.getSharedPreferences("hwj_preference", Context.MODE_PRIVATE)
        return SharedPreferencesSettings(delegate)
    }
}

