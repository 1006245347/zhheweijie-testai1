package com.hwj.ai.ui.global

import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.hwj.ai.global.BackGroundColor
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel


@Composable
fun AppScaffold(
    drawerState: DrawerState = rememberDrawerState(initialValue = Closed),
    onChatClicked: (String) -> Unit,
    onNewChatClicked: () -> Unit,
    onIconClicked: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    scope.launch {
        conversationViewModel.initialize()
    }

    val model = koinViewModel(ConversationViewModel::class)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = BackGroundColor) {
                AppDrawer(
                    onChatClicked = onChatClicked,
                    onNewChatClicked = onNewChatClicked,
                    model,
                    onIconClicked = onIconClicked,
                )
            }
        },
        content = content
    )

}