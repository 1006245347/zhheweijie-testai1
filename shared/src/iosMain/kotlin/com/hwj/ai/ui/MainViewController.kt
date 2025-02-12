package com.hwj.ai.ui

import androidx.compose.ui.window.ComposeUIViewController
import com.hwj.ai.PlatformAppStart
import com.hwj.ai.global.iOSTheme

fun MainViewController ()= ComposeUIViewController{

    iOSTheme {
        PlatformAppStart()
    }
}