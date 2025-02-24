package com.hwj.ai.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
//            MyApplicationTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    GreetingView(Greeting().greet())
//                }
//            }

//            MyApplicationTheme {
//                Surface (modifier = Modifier.fillMaxSize()){
//                    PlatformAppStart()
//                }
//            }

            ThemeChatLite {
                Surface(modifier = Modifier.fillMaxSize()) { PlatformAppStart() }
            }
        }
    }
}

//@Composable
//fun GreetingView(text: String) {
////    Text(text = text)
//    TestPage()
//}
//
//@Preview
//@Composable
//fun DefaultPreview() {
//    MyApplicationTheme {
//        GreetingView("Hello, Android!")
//    }
//}
