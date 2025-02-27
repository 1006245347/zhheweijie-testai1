package com.hwj.ai.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.hwj.ai.PlatformAppStart
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.global.iOSTheme

fun MainViewController() = ComposeUIViewController {

//    iOSTheme{}
    ThemeChatLite {
        Surface(modifier = Modifier.fillMaxSize()) {
            PlatformAppStart()
        }
    }
}