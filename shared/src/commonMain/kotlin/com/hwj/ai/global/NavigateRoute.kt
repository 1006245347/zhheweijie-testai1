package com.hwj.ai.global

import androidx.compose.runtime.Composable
import com.hwj.ai.App
import com.hwj.ai.ui.chat.ChatScreen
import com.hwj.ai.ui.chat.WelcomeScreen
import com.hwj.ai.ui.me.SettingsScreen
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator

/**
 * @author by jason-何伟杰，2025/2/12
 * des:路由表
 */
@Composable
fun NavigateRoute(navigator: Navigator) {
    NavHost(navigator = navigator, initialRoute = NavigationScene.App.path) {
        scene(NavigationScene.App.path) {
            App(navigator){}
        }
        scene(NavigationScene.Welcome.path) {
            WelcomeScreen(navigator)
        }

        scene(NavigationScene.Chat.path) { backStackEntry->
//            backStackEntry.path
            ChatScreen(navigator)
        }
        scene(NavigationScene.Setting.path) {
            SettingsScreen(navigator)
        }
    }
}

sealed class NavigationScene(val path: String, val title: String? = null) {
    object App:NavigationScene("/app","app")
    object Welcome : NavigationScene("/welcome", "welcome")
    object Chat : NavigationScene("/chatPage", "chat")
    object Setting : NavigationScene("/setting", "setting")
}