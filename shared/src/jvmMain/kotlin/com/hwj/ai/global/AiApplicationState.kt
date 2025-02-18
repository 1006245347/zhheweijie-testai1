package com.hwj.ai.global

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState


class AiApplicationState {
    val tray = TrayState()

    private val _windows = mutableStateListOf<AiWindowState>()
    val windows: List<AiWindowState> get() = _windows

    //创建新窗口
    fun newWindow() {
        _windows.add(
            AiWindowState(
                application = this,
                exit=_windows::remove
            )
        )
    }

    fun sendNotification(notification: Notification) {
        tray.sendNotification(notification)
    }

    //关闭托盘后关闭所有窗口
    suspend fun exit() {
        val windowsCopy = windows.reversed()
        for (window in windowsCopy) {

            if (!window.exit()) break
        }
    }

}

@Composable
fun rememberApplicationState() = remember {
    AiApplicationState().apply { newWindow() }
}

