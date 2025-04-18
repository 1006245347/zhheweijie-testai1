package com.hwj.ai.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hwj.ai.except.ToolTipCase
import com.hwj.ai.global.PrimaryColor
import com.hwj.ai.global.conversationTestTag
import com.hwj.ai.global.printD
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import kotlinx.coroutines.delay
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
    val isAutoScroll by conversationViewModel.isAutoScroll.collectAsState()
    var showScrollBar by remember { mutableStateOf(false) }
    var isScrolling by remember { mutableStateOf(false) }
    var boxHeight by remember { mutableStateOf(0) }

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

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            showScrollBar = true
            isScrolling = true
        } else {
            isScrolling = false
            launch {
                delay(500)
                showScrollBar = false
            }
        }
    }


    Box(modifier = modifier.onSizeChanged { size -> boxHeight = size.height }) {
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

        // 滚动条
        AnimatedVisibility(
            visible = showScrollBar && listState.layoutInfo.totalItemsCount > 0,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 1.dp)
        ) {
            val barHeight = 48.dp
            val topPaddingPx = with(LocalDensity.current) { 90.dp.toPx() }
            val totalItems = listState.layoutInfo.totalItemsCount
            val progress =
                remember(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
                    val firstItemSize =
                        listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 1
                    val scrollProgress =
                        (listState.firstVisibleItemIndex + listState.firstVisibleItemScrollOffset.toFloat()
                                / firstItemSize).coerceIn(0f, totalItems.toFloat())
                    scrollProgress / totalItems.toFloat()
                }

            val adjustProgress = 1 - progress //倒序
            //算的有点诡异
            val maxOffsetPx =
                (boxHeight - topPaddingPx * 2 - with(LocalDensity.current) { barHeight.toPx() }).coerceAtLeast(
                    0f
                )
            val offsetY = (adjustProgress * maxOffsetPx).toInt()

            Box(
                modifier = Modifier
                    .size(4.dp, barHeight)
                    .offset { IntOffset(x = 0, y = offsetY) }
                    .background(
                        color = Color.Gray.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        if (isAutoScroll) { //貌似没效果
            subScope.launch {
                listState.animateScrollToItem(0)
            }
        }

        if (messages.size > 1 && isListBottom) {
            ToolTipCase(modifier = Modifier.align(Alignment.BottomCenter), tip = "置底", content = {
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