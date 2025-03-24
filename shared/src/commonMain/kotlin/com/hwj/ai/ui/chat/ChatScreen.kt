package com.hwj.ai.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.hwj.ai.createPermission
import com.hwj.ai.data.local.PermissionPlatform
import com.hwj.ai.global.CODE_IS_DARK
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.global.ToastUtils
import com.hwj.ai.global.cDeepLine
import com.hwj.ai.global.getCacheBoolean
import com.hwj.ai.global.printD
import com.hwj.ai.global.saveBoolean
import com.hwj.ai.ui.global.AppBar
import com.hwj.ai.ui.global.AppScaffold
import com.hwj.ai.ui.global.GlobalIntent
import com.hwj.ai.ui.global.ToastHost
import com.hwj.ai.ui.viewmodel.ChatViewModel
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import com.hwj.ai.ui.viewmodel.ModelConfigIntent
import com.hwj.ai.ui.viewmodel.ModelConfigState
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.BackHandler
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun ChatScreen(navigator: Navigator) {

    val chatViewModel = koinViewModel(ChatViewModel::class)
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val configState by chatViewModel.configState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerOpen by chatViewModel.drawerShouldBeOpened.collectAsState()
    val curConversationId by conversationViewModel.currentConversationState.collectAsState()

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
            chatViewModel.processGlobal(GlobalIntent.CheckDarkTheme)
        }
        //主动获取数据
        chatViewModel.processConfig(ModelConfigIntent.LoadData)
    }

//    SideEffect {
    //更新数据?
//    }



//    printD("dark1>${darkTheme.value}")
    ThemeChatLite(isDark = darkTheme.value) {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppScaffold(drawerState = drawerState,
                onChatClicked = { chatId ->
                    scope.launch {//指向当前会话，如果不是当前要中断生成
                        if (curConversationId != chatId) {
                            conversationViewModel.stopReceivingResults()
                        }
                        drawerState.close()
                    }
                },
                onNewChatClicked = {
                    scope.launch {
                        //要中断正在的生成
                        conversationViewModel.stopReceivingResults()
                        drawerState.close()
                    }
                }, navigator = navigator,
                onIconClicked = {
                    scope.launch { //全是异步，好容易错
//                        printD("dark2>${darkTheme.value}")
                        darkTheme.value = !darkTheme.value
                        saveBoolean(CODE_IS_DARK, darkTheme.value)

                        chatViewModel.processGlobal(GlobalIntent.CheckDarkTheme)
//                        printD("t>${getCacheBoolean(CODE_IS_DARK)}")
                        drawerState.close()
                    }
                }) {
                Box(Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppBar(onClickMenu = { scope.launch { drawerState.open() } },
                            onNewChat = {
                                scope.launch {
                                    conversationViewModel.stopReceivingResults()
                                    drawerState.close()
                                    conversationViewModel.newConversation()

                                }
                            })
                        HorizontalDivider(thickness = (0.5f).dp, color = cDeepLine())
                        if (configState.isLoading || configState.error != null) {
                            ChatInit(configState)
                        } else {
//                        printList(configState.data) //大模型数据
                            Conversation(navigator)
                        }
                    }

                    ToastHost(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth()
                            .padding(bottom = 100.dp)
                            .wrapContentHeight()
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatInit(state: ModelConfigState) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (state.isLoading) {
                Text("加载中...", color = MaterialTheme.colorScheme.secondary)
            } else if (state.error != null) {
                Text(state.error)
            }
        }
    }
}