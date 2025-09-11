package com.hwj.ai.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.global.NavigationScene
import com.hwj.ai.ui.viewmodel.WelcomeScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.NavOptions
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.PopUpTo

@Composable
fun WelcomeScreen(navigator: Navigator) {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
//        Image(
//            //资源图的应用必须run了之后才有引用方法
//            bitmap = imageResource(Res.drawable.ic_big_logo),
//            contentDescription = "welcome",
//            modifier = Modifier.wrapContentSize()
//                .absolutePadding(top = 80.dp)
//                .align(Alignment.CenterHorizontally)
//        )
        Text(
            text = "Chat bot v1", fontWeight = FontWeight.Bold, fontSize = 26.sp,
            color = Color.Black, modifier = Modifier.absolutePadding(top = 50.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
    startAction(navigator = navigator)
}

@Composable
fun startAction(navigator: Navigator) {
    val scope = rememberCoroutineScope()
    val welcomeScreenModel = koinViewModel(WelcomeScreenModel::class)
    welcomeScreenModel.setFirstWelcome() //设置首次欢迎
    scope.launch {
        delay(1500)
        navigator.navigate(
            NavigationScene.Chat.path, NavOptions(
                popUpTo = PopUpTo.First()
            )
        )
    }
}