package com.hwj.ai.ui.global

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hwj.ai.global.cDeepLine
import com.hwj.ai.global.isDarkTxt
import com.hwj.ai.global.onlyDesktop
import com.hwj.ai.global.onlyMobile
import com.hwj.ai.ui.viewmodel.ChatViewModel
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import com.hwj.ai.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun AppScaffold(
    drawerState: DrawerState = rememberDrawerState(initialValue = Closed),
    onChatClicked: (String) -> Unit,
    onNewChatClicked: () -> Unit,
    onIconClicked: () -> Unit = {}, navigator: Navigator,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val settingsViewModel = koinViewModel(SettingsViewModel::class)
    scope.launch {
        settingsViewModel.initialize()
        conversationViewModel.initialize()
    }
    if (onlyMobile()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.background) {//hwj
                    Box {
                        AppDrawer(
                            onChatClicked = onChatClicked,
                            onNewChatClicked = onNewChatClicked,
                            conversationViewModel = conversationViewModel,
                            onIconClicked = onIconClicked, navigator
                        )
                    }
                }
            },
            content = content
        )
    } else { //桌面端抽屜和内容並存
        val chatViewModel = koinViewModel(ChatViewModel::class)
        var isCollapsed = chatViewModel.isCollapsedState.collectAsState().value
        PermanentNavigationDrawer(drawerContent = {
            PermanentDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.width(if (isCollapsed) 0.dp else 240.dp)
            ) {
                Box {
                    AppDrawer(
                        onChatClicked = onChatClicked,
                        onNewChatClicked = onNewChatClicked,
                        conversationViewModel = conversationViewModel,
                        onIconClicked = onIconClicked, navigator
                    )
                    VerticalDivider(
                        thickness = (0.5f).dp, color = cDeepLine(), modifier = Modifier.align(
                            Alignment.BottomEnd
                        ).fillMaxHeight()
                    )
                }
            }
        }) {
            content()
        }
    }
}