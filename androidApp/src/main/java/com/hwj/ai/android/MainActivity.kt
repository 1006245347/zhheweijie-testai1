package com.hwj.ai.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.hwj.ai.PlatformAppStart
import com.hwj.ai.global.Greeting
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.ui.global.TestPage
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        FileKit.init(this)
        setContentView(ComposeView(this).apply {
            consumeWindowInsets=false

            //只要问答后，状态栏颜色无法修改了
            setContent {
//                ThemeChatLite {
                    Surface(modifier = Modifier.fillMaxSize()) { PlatformAppStart() }
//                }
            }
        })
    }
}
