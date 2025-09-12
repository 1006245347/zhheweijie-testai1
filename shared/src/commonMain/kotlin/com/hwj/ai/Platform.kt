package com.hwj.ai

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.aallam.openai.api.model.ModelPermission
import com.hwj.ai.data.local.PermissionPlatform
import com.hwj.ai.global.OsStatus
import com.hwj.ai.models.MessageModel
import io.ktor.client.HttpClient

interface Platform {
    val name: String
    val os: OsStatus
}

expect fun getPlatform(): Platform

expect fun createKtorHttpClient(timeout: Long?): HttpClient

@Composable
expect fun setColorScheme(isDark: Boolean): ColorScheme

@Composable
expect fun BotMessageCard(message: MessageModel)

expect fun checkSystem(): OsStatus

//不大对
@Composable
fun hideKeyBoard() {
    LocalSoftwareKeyboardController.current?.hide()
}

//手机权限
@Composable
expect fun createPermission(
    permission: PermissionPlatform,
    grantedAction: () -> Unit,
    deniedAction: () -> Unit
)

//为了令向量写入IO通用，定义个中介文件对象
expect class KFile{
    val name : String
    suspend fun readText(): String
    suspend fun readLines(): List<String>
    suspend fun writeText(text: String)
    suspend fun writeLines(lines: List<String>)
}