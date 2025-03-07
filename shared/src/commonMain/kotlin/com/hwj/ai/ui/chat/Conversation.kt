package com.hwj.ai.ui.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.hwj.ai.global.conversationTestTag
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun Conversation() {
    val model = koinViewModel(ConversationViewModel::class)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background, //background根据主题自动变化
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                MessageList(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp), model
                )
                TextInput(model)
            }
        }
    }
}

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    conversationViewModel: ConversationViewModel
) {
    val listState = rememberLazyListState()

    val conversationId by conversationViewModel.currentConversationState.collectAsState()
    val messagesMap by conversationViewModel.messagesState.collectAsState()
    val isFabExpanded by conversationViewModel.isFabExpanded.collectAsState()

    val messages: List<MessageModel> =
        if (messagesMap[conversationId] == null) listOf() else messagesMap[conversationId]!!

    Box(modifier = modifier) {
        LazyColumn(
            contentPadding =
            WindowInsets.statusBars.add(WindowInsets(top = 90.dp)).asPaddingValues(),
            modifier = Modifier
                .testTag(conversationTestTag)
                .fillMaxSize(),
            reverseLayout = true,
            state = listState,
        ) {
            items(messages.size) { index ->
                Box(modifier = Modifier.padding(bottom = if (index == 0) 10.dp else 0.dp)) {
                    Column {
                        MessageCard(
                            message = messages[index],
                            isLast = index == messages.size - 1,
                            isHuman = true
                        )
                        MessageCard(message = messages[index])
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            text = {
                Text(text = "Stop Generating", color = Color.White)
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop Generating",
                    tint = Color.White,
                    modifier = Modifier
                        .size(25.dp)
                )
            },
            onClick = {
                conversationViewModel.stopReceivingResults()
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp)
                .size(50.dp)
                .animateContentSize(),
            expanded = isFabExpanded,
            containerColor = MaterialTheme.colorScheme.primary
        )
    }
}