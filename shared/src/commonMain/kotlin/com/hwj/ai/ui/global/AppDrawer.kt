package com.hwj.ai.ui.global

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.hwj.ai.global.NavigationScene
import com.hwj.ai.global.PrimaryColor
import com.hwj.ai.global.getMills
import com.hwj.ai.global.isDarkPanel
import com.hwj.ai.global.isDarkTxt
import com.hwj.ai.global.isLightPanel
import com.hwj.ai.global.isLightTxt
import com.hwj.ai.global.urlToImageAppIcon
import com.hwj.ai.global.urlToImageAuthor
import com.hwj.ai.models.ConversationModel
import com.hwj.ai.ui.viewmodel.ChatViewModel
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun AppDrawer(
    onChatClicked: (String) -> Unit,
    onNewChatClicked: () -> Unit,
    conversationViewModel: ConversationViewModel,
    onIconClicked: () -> Unit = {}, navigator: Navigator
) {
    val coroutineScope = rememberCoroutineScope()
    AppDrawerIn(
        onChatClicked = onChatClicked,
        onNewChatClicked = onNewChatClicked,
        onIconClicked = onIconClicked,
        conversationChat = { conversationViewModel.newConversation() },
        deleteConversation = { conversationId ->
            coroutineScope.launch {
                conversationViewModel.deleteConversation(conversationId)
            }
        },
        onConversation = { conversationModel: ConversationModel ->
            coroutineScope.launch { conversationViewModel.onConversation(conversationModel) }
        },
        currentConversationState = conversationViewModel.currentConversationState.collectAsState().value,
        conversationState = conversationViewModel.conversationsState.collectAsState().value,
        navigator
    )
}


@Composable
fun AppDrawerIn(
    onChatClicked: (String) -> Unit,
    onNewChatClicked: () -> Unit,
    onIconClicked: () -> Unit,
    conversationChat: () -> Unit,
    deleteConversation: (String) -> Unit,
    onConversation: (ConversationModel) -> Unit,
    currentConversationState: String,
    conversationState: MutableList<ConversationModel>,
    navigator: Navigator? = null
) {
    val context = LocalPlatformContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))//影响键盘？
        DrawerHeader(clickAction = onIconClicked)

        DividerItem(modifier = Modifier.padding(horizontal = 28.dp))
        DrawerItemHeader("Settings")
        ChatItem("Settings", Icons.Filled.Settings, false) { onChatClicked("Settings") }
        ProfileItem(
            " author zhheweijie",
            urlToImageAuthor,
        ) {
//            UrlLauncher().openUrl(context = context, urlToGithub)
            navigator?.navigate(NavigationScene.SettingLLM.path)
        }
        DrawerItemHeader("Chats")
        ChatItem("New Chat", Icons.Outlined.AddComment, false) {
            onNewChatClicked()
            conversationChat()
        }
        HistoryConversations(
            onChatClicked,
            deleteConversation,
            onConversation,
            currentConversationState,
            conversationState
        )
    }
}

@Composable
private fun DrawerHeader(
    clickAction: () -> Unit = {}
) {
    val paddingSizeModifier = Modifier
        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
        .size(34.dp)
    Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f), verticalAlignment = CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current).data(urlToImageAppIcon)
                    .crossfade(true).build(),
                modifier = paddingSizeModifier.then(Modifier.clip(RoundedCornerShape(6.dp))),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    "Chat Lite",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
                Text(
                    "Powered by DeepSeek",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = PrimaryColor,
                )
            }

        }

        IconButton(
            onClick = {
                clickAction.invoke()
            }, modifier = Modifier.padding(bottom = 40.dp)
        ) {
            Icon(
                Icons.Filled.WbSunny,
                "backIcon",
                modifier = Modifier.size(26.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ColumnScope.HistoryConversations(
    onChatClicked: (String) -> Unit,
    deleteConversation: (String) -> Unit,
    onConversation: (ConversationModel) -> Unit,
    currentConversationState: String,
    conversationState: List<ConversationModel>
) {
    val scope = rememberCoroutineScope()
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val isDark = chatViewModel.darkState.collectAsState()
    val listState = rememberLazyListState()
    var listSize by remember { mutableStateOf(IntSize.Zero) }
    var isScrolling by remember { mutableStateOf(false) }
    var lastScrollTime by remember { mutableStateOf(0L) }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            isScrolling = true
            lastScrollTime = getMills()
        } else {
            delay(500)
            if (getMills() - lastScrollTime >= 500) {
                isScrolling = false
            }
        }
    }
    Box(Modifier.fillMaxWidth().weight(1f, false)) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(end = 10.dp)
                .onGloballyPositioned { listSize = it.size }, state = listState
        ) {//数据要倒序集合
            items(conversationState.size) { index ->
                RecycleChatItem(
                    text = conversationState[index].title,
                    Icons.AutoMirrored.Filled.Message, //id?匹配不对
                    selected = conversationState[index].id == currentConversationState,
                    onChatClicked = {
//                        println("id> $index ,${conversationState[index].id}")
                        onChatClicked(conversationState[index].id)
                        scope.launch {
                            onConversation(conversationState[index])
                        }
                    },
                    onDeleteClicked = {
                        scope.launch {
                            //删除正在接收的？
                            deleteConversation(conversationState[index].id)
                        }
                    }
                )
            }
        }

        androidx.compose.animation.AnimatedVisibility(visible = isScrolling, enter = fadeIn(), exit = fadeOut()) {
            //滚动条
            if (listState.layoutInfo.totalItemsCount > 0) {
                val proportion =
                    1f / listState.layoutInfo.totalItemsCount
                val scrollOffset =
                    (listState.firstVisibleItemIndex + listState.firstVisibleItemScrollOffset / 1000f) * proportion

                val scrollbarHeight = (listSize.height * 0.3f).coerceAtLeast(30f) //滚动条高度固定位列表30%
                val scrollbarOffsetY = (listSize.height - scrollbarHeight) * scrollOffset
                Box(modifier = Modifier.fillMaxSize().padding(end = 2.dp)) {
                    Box(Modifier.width(4.dp)
                        .height(with(LocalDensity.current) { scrollbarHeight.toDp() })
                        .offset { IntOffset(x = 0, y = scrollbarOffsetY.toInt()) }
                        .background(
//                            Color.Cyan.copy(alpha = 0.7f),
                            if(isDark.value) isDarkTxt() else isLightTxt(),
                            shape = MaterialTheme.shapes.small
                        )
                        .align(Alignment.TopEnd)
                    )
                }
            }
        }
    }

}

@Composable
private fun DrawerItemHeader(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = 42.dp)
            .padding(horizontal = 28.dp),
        contentAlignment = CenterStart
    ) {
        Text(
            text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ChatItem(
    text: String,
    icon: ImageVector = Icons.Filled.Edit,
    selected: Boolean,
    onChatClicked: () -> Unit
) {
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val isDark = chatViewModel.darkState.collectAsState()
    val background = if (selected) {
        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .then(background)
            .clickable(onClick = onChatClicked),
        verticalAlignment = CenterVertically
    ) {
        val iconTint = if (selected) {
            if (isDark.value) {
                isDarkPanel()
            } else {
                isLightPanel()
            }
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
        Icon(
            icon,
            tint = iconTint,
            modifier = Modifier
                .padding(start = 16.dp, top = 10.dp, bottom = 10.dp)
                .size(25.dp),
            contentDescription = null,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) {
                if (isDark.value) {
                    isDarkTxt()
                } else {
                    isLightTxt()
                }
            } else {
                MaterialTheme.colorScheme.onSurface //其实Android15也是没有效果
            },
            modifier = Modifier.padding(start = 12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RecycleChatItem(
    text: String,
    icon: ImageVector = Icons.Filled.Edit,
    selected: Boolean,
    onChatClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val isDark = chatViewModel.darkState.collectAsState().value
    val isReceive = conversationViewModel.isFabExpanded.collectAsState().value
    val background = if (selected) {
        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    } else {
        Modifier
    }
    Row(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .clip(RoundedCornerShape(30))
            .then(background)
            .clickable(onClick = onChatClicked),
        verticalAlignment = CenterVertically
    ) {
//        val iconTint = if (selected) {
////            MaterialTheme.colorScheme.primary
//            if (isDark) isDarkBg() else isLightBg()
//        } else {
//            if (isDark) isDarkPanel() else isLightPanel()
////            MaterialTheme.colorScheme.onSurfaceVariant
//        }
        val iconTint = if (selected) {
            if (isDark) {
                isDarkPanel()
            } else {
                isLightPanel()
            }
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
        Icon(
            icon,
            tint = iconTint,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                .size(25.dp),
            contentDescription = null,
        )
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (selected) {
                if (isDark) isDarkTxt() else isLightTxt()
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier
                .padding(start = 12.dp)
                .fillMaxWidth(0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
//        Spacer(Modifier.weight(0.9f, true).backgroundy(Color.Yellow))

        if (selected && isReceive) {
            //防止删除正在生成的会话
        } else {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.padding(
                    end = 5.dp
                ).size(35.dp).clickable { onDeleteClicked() }
            )
        }
    }
}

@Composable
private fun ProfileItem(text: String, urlToImage: String?, onProfileClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .height(46.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(CircleShape)
            .clickable(onClick = onProfileClicked),
        verticalAlignment = CenterVertically
    ) {
        val paddingSizeModifier = Modifier
            .padding(start = 16.dp, top = 5.dp, bottom = 5.dp)
            .size(24.dp)
        if (urlToImage != null) {
            Image(
                painter = rememberAsyncImagePainter(urlToImage),
                modifier = paddingSizeModifier.then(Modifier.clip(CircleShape)),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        } else {
            Spacer(modifier = paddingSizeModifier)
        }
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
fun DividerItem(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

