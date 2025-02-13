package com.hwj.ai.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.hwj.ai.ui.global.AppBar
import com.hwj.ai.ui.global.AppScaffold
import com.hwj.ai.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.BackHandler
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun ChatScreen(navigator: Navigator) {

    val mainViewModel = koinViewModel(MainViewModel::class)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerOpen by mainViewModel.drawerShouldBeOpened.collectAsState()
    if (drawerOpen) {
        LaunchedEffect(Unit) {
            try {
                drawerState.open()
            } finally {
                mainViewModel.resetOpenDrawerAction()
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
        }else{
            focusManager.clearFocus()
        }
    }

    Surface(color= MaterialTheme.colorScheme.background) {
        AppScaffold(drawerState=drawerState,
            onChatClicked = {},
            onNewChatClicked = {},
            onIconClicked = {}){
            Column (modifier = Modifier.fillMaxSize()){
                AppBar(onClickMenu = { scope.launch { drawerState.open() }})
                Divider()
                Conversation()
            }
        }
    }
}