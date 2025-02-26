package com.hwj.ai.ui.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.hwj.ai.global.CODE_IS_DARK
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.global.getCacheBoolean
import com.hwj.ai.global.printD
import com.hwj.ai.global.printList
import com.hwj.ai.global.saveBoolean
import com.hwj.ai.ui.global.AppBar
import com.hwj.ai.ui.global.AppScaffold
import com.hwj.ai.ui.viewmodel.ChatViewModel
import com.hwj.ai.ui.viewmodel.ModelConfigIntent
import com.hwj.ai.ui.viewmodel.ModelConfigState
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.BackHandler
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun ChatScreen(navigator: Navigator) {

    val chatViewModel = koinViewModel(ChatViewModel::class)
    val configState by chatViewModel.configState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerOpen by chatViewModel.drawerShouldBeOpened.collectAsState()
    if (drawerOpen) {
        LaunchedEffect(Unit) {
            try {
                drawerState.open()
            } finally {
                chatViewModel.resetOpenDrawerAction()
            }
        }
    }

    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    BackHandler {
        if (drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        } else {
            focusManager.clearFocus()
        }
    }

    val darkTheme = remember(key1 = "darkTheme") {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        scope.launch {
            darkTheme.value = getCacheBoolean(CODE_IS_DARK)
        }
        //主动获取数据
        chatViewModel.processIntent(ModelConfigIntent.LoadData)
    }

    SideEffect {
        //更新数据?
    }


    printD("dark1>${darkTheme.value}")
    ThemeChatLite {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppScaffold(drawerState = drawerState,
                onChatClicked = {
                    scope.launch { drawerState.close() }
                },
                onNewChatClicked = {
                    scope.launch { drawerState.close() }
                },
                onIconClicked = {
                    scope.launch { //全是异步，好容易错
                        printD("dark2>${darkTheme.value}")
                        darkTheme.value = !darkTheme.value
                        saveBoolean(CODE_IS_DARK, darkTheme.value)
                        printD("t>${getCacheBoolean(CODE_IS_DARK)}")
                    }
                }) {
                Column(modifier = Modifier.fillMaxSize()) {
                    AppBar(onClickMenu = { scope.launch { drawerState.open() } })
                    Divider()
                    if (configState.isLoading || configState.error != null) {
                        ChatInit(configState)
                    } else {
//                        printList(configState.data) //大模型数据
                        Conversation()
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInit(state: ModelConfigState) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                Text("加载中...")
            } else if (state.error != null) {
                Text(state.error)
            }
        }
    }
}