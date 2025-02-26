package com.hwj.ai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hwj.ai.global.NavigateRoute
import com.hwj.ai.global.printD
import com.hwj.ai.ui.chat.ChatScreen
import com.hwj.ai.ui.chat.WelcomeScreen
import com.hwj.ai.ui.viewmodel.AppUiState
import com.hwj.ai.ui.viewmodel.WelcomeScreenModel
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.rememberNavigator
import org.koin.compose.KoinContext

/**
 * @author by jason-何伟杰，2025/2/12
 * des:App首页界面
 */
@Composable
fun App(navigator: Navigator, setViews: @Composable () -> Unit = {}) {

    val welcomeScreenModel = koinViewModel(WelcomeScreenModel::class)

    Box(modifier = Modifier.fillMaxSize()) {
        printD("OS>${getPlatform()}")
        printD("model>$welcomeScreenModel")
        val screen = welcomeScreenModel.uiState.let { uiState ->
            when (uiState) {
                AppUiState.Loading -> null
                is AppUiState.Success -> when (uiState.isWelcomeShown) {
                    true -> ChatScreen(navigator)
                    false -> WelcomeScreen(navigator)
                }
            }
        }
//        ChatScreen(navigator)
    }


//    CompositionLocalProvider(){
//        ThemeChatLite {
//            Surface {
//                Crossfade (screen){ screen->
//                    when (screen){
//                        null-> Box(modifier = Modifier.fillMaxSize())
//                        else->
//                    }
//                }
//            }
//        }
//    }
}

/**
 * des:通过路由进入APP
 */
@Composable
fun PlatformAppStart() {
    PreComposeApp {
        KoinContext {
            val navigator = rememberNavigator()
            NavigateRoute(navigator) //进Welcome
        }
    }
}