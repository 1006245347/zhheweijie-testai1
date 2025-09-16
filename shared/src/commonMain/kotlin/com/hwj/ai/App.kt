package com.hwj.ai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hwj.ai.global.NavigateRoute
import com.hwj.ai.global.NavigationScene
import com.hwj.ai.global.ToastUtils
import com.hwj.ai.global.printD
import com.hwj.ai.ui.chat.ChatScreen
import com.hwj.ai.ui.chat.WelcomeScreen
import com.hwj.ai.ui.global.ToastHost
import com.hwj.ai.ui.viewmodel.AppUiState
import com.hwj.ai.ui.viewmodel.WelcomeScreenModel
import io.github.vinceglb.filekit.FileKit
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
        printD("OS>${getPlatform().name}")
         welcomeScreenModel.uiState.let { uiState ->
            when (uiState) {
                AppUiState.Loading -> null
                is AppUiState.Success -> when (uiState.isWelcomeShown) {
                    true -> navigator.navigate(NavigationScene.Chat.path)
                    false -> navigator.navigate(NavigationScene.Welcome.path)
                }
            }
        }
    }
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