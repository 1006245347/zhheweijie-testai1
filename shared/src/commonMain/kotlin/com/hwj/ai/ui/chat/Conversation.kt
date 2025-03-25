package com.hwj.ai.ui.chat

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.hwj.ai.except.ToolTipCase
import com.hwj.ai.global.PrimaryColor
import com.hwj.ai.global.conversationTestTag
import com.hwj.ai.global.printD
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun Conversation(navigator: Navigator) {
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
                TextInput(model, navigator)
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
    val subScope = rememberCoroutineScope()
    val conversationId by conversationViewModel.currentConversationState.collectAsState()
    val messagesMap by conversationViewModel.messagesState.collectAsState()
    val isFabExpanded by conversationViewModel.isFabExpanded.collectAsState()

    val messages: List<MessageModel> =
        if (messagesMap[conversationId] == null) listOf() else messagesMap[conversationId]!!
    val isListBottom by remember {
        derivedStateOf {
            // 检查是否滚动到最后一项
            val visibleItemIndex = listState.firstVisibleItemIndex
            val visibleItemScrollOffset = listState.firstVisibleItemScrollOffset
            // 如果 reverseLayout = true，则 firstVisibleItemIndex 为 0 表示滚动到了最后一项
            visibleItemIndex != 0 || visibleItemScrollOffset > 0
        }
    }
    Box(modifier = modifier) {
        LazyColumn(
            contentPadding =
            WindowInsets.statusBars.add(WindowInsets(top = 90.dp)).asPaddingValues(),
            modifier = Modifier
                .testTag(conversationTestTag)
                .fillMaxSize(),
            reverseLayout = true, //反序的！！
            state = listState,
        ) {
            items(messages.size) { index ->
                Box(modifier = Modifier.padding(bottom = if (index == 0) 10.dp else 0.dp)) {
                    Column { //一轮对话，两条消息
                        MessageCard(
                            message = messages[index],
                            isHuman = true,
                            isLast = index == messages.size - 1 //最旧的一轮对话
                        )
                        MessageCard(
                            message = messages[index],
                            isHuman = false,
                            isLatest = index == 0
                        )
                    }
                }
            }
        }

        if (messages.size > 1 && isListBottom) {
            ToolTipCase(tip = "置底", content = {
                IconButton(onClick = {
                    subScope.launch {
                        listState.animateScrollToItem(0)
                    }
                }, modifier = Modifier.align(Alignment.BottomCenter)) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDownCircle,
                        contentDescription = "置底",
                        tint = PrimaryColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
            })
        }
    }
}