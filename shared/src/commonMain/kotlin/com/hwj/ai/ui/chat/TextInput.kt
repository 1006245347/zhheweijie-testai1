package com.hwj.ai.ui.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.checkSystem
import com.hwj.ai.global.NavigationScene
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.printD
import com.hwj.ai.models.MenuActModel
import com.hwj.ai.ui.global.KeyEventEnter
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator


@Composable
fun TextInput(
    conversationViewModel: ConversationViewModel,navigator: Navigator
) {
    val coroutineScope = rememberCoroutineScope()
    TextInputIn(
        sendMessage = { text ->
            //判断是否在生成消息不让点击事件
            if (!conversationViewModel.getFabStatus()) {
                coroutineScope.launch {
                    conversationViewModel.sendMessage(text)
                }
            }
        },navigator
    )
}

@Composable
fun InputTopIn(state: LazyListState,navigator: Navigator) {
    val subScope = rememberCoroutineScope()

      val list = mutableListOf<MenuActModel>()
    list.add(MenuActModel("相册"))
    if (checkSystem() == OsStatus.ANDROID
        || checkSystem() == OsStatus.IOS
    ) {
        list.add(MenuActModel("拍摄"))
    } else {
        list.add(MenuActModel("翻译"))
    }
    LazyRow(state = state, modifier = Modifier.animateContentSize()) {
        items(list.size) { index ->
            Button(
                modifier = Modifier.padding(start = 10.dp, bottom = 4.dp).size(86.dp, 36.dp),
                onClick = {
                    subScope.launch {
                        when (list[index].title) {
                            "相册" -> {
                                val imageFile: PlatformFile? =
                                    FileKit.openFilePicker(type = FileKitType.Image)
                                printD("file>${imageFile?.name}")
                            }

                            "拍摄" -> {
                                navigator.navigate(NavigationScene.Camera.path)
                            }

                            "翻译" -> {}
                        }
                    }
                }) {
                Text(
                    text = list[index].title,
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}


@Composable
fun TextInputIn(
    sendMessage: (String) -> Unit,navigator: Navigator
) {
    val scope = rememberCoroutineScope()
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val isFabExpanded by conversationViewModel.isFabExpanded.collectAsState()
    var text by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        // Use navigationBarsPadding() imePadding() and , to move the input panel above both the
        // navigation bar, and on-screen keyboard (IME)
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Column {
            if (!isFabExpanded) {
                InputTopIn(rememberLazyListState(),navigator)
            }
            HorizontalDivider(Modifier.height(0.2.dp))
            Box(
                Modifier
                    .padding(horizontal = 4.dp)
                    .padding(top = 6.dp, bottom = 10.dp)
            ) {
                Row {
                    TextField(
                        value = text,
                        onValueChange = {
                            text = it
                        },
                        label = null,
                        placeholder = {
                            Text(
                                "Ask me anything",
                                fontSize = 12.sp
                            )
                        },
//                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .background(Color.Transparent)
                            .weight(1f).KeyEventEnter {
                                scope.launch {
                                    val textClone = text.text
                                    text = TextFieldValue("")
                                    sendMessage(textClone)
                                }
                            },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent, //去除边框
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedTextColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                            focusedIndicatorColor = Color.Transparent, //去除底部横线
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    IconButton(onClick = {
                        scope.launch {
                            val textClone = text.text
                            text = TextFieldValue("")
                            sendMessage(textClone)
                        }
                    }) {
                        Icon(
                            Icons.Filled.Send,
                            "sendMessage",
                            modifier = Modifier.size(26.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}
