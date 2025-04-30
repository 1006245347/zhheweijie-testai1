package com.hwj.ai

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.hwj.ai.data.local.PermissionPlatform
import com.hwj.ai.global.DarkColorScheme
import com.hwj.ai.global.LightColorScheme
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.askPermission
import com.hwj.ai.global.thinking
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.chat.BotCommonCardApp
import com.hwj.ai.ui.global.LoadingThinking
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.camera.CAMERA
import dev.icerock.moko.permissions.gallery.GALLERY
import dev.icerock.moko.permissions.storage.STORAGE
import dev.icerock.moko.permissions.storage.WRITE_STORAGE
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val os: OsStatus
        get() = OsStatus.IOS
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun createKtorHttpClient(timeout: Long?): HttpClient {
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

actual fun checkSystem(): OsStatus {
    return OsStatus.IOS
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
//    BotCommonCard(message)
    if (message.answer == thinking) {
        LoadingThinking(thinking)
    } else {
        BotCommonCardApp(message) //可平稳运行
    }
}

@Composable
actual fun createPermission(
    permission: PermissionPlatform,
    grantedAction: () -> Unit,
    deniedAction: () -> Unit
) {
    val p = when (permission) {
        PermissionPlatform.CAMERA -> Permission.CAMERA
        PermissionPlatform.GALLERY -> Permission.GALLERY
        PermissionPlatform.STORAGE -> Permission.STORAGE
        PermissionPlatform.WRITE_STORAGE -> Permission.WRITE_STORAGE
        else -> Permission.STORAGE
    }
    askPermission(p, grantedAction, deniedAction)
}