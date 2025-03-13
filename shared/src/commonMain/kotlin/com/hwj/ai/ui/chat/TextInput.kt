package com.hwj.ai.ui.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hwj.ai.checkSystem
import com.hwj.ai.global.NavigationScene
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.cHalfGrey80717171
import com.hwj.ai.global.printD
import com.hwj.ai.models.MenuActModel
import com.hwj.ai.ui.global.KeyEventEnter
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator


@Composable
fun TextInput(
    conversationViewModel: ConversationViewModel, navigator: Navigator
) {
    val coroutineScope = rememberCoroutineScope()
    val imagePathList by conversationViewModel.imageListState.collectAsState() //选中的图片

    TextInputIn(
        sendMessage = { text ->
            //判断是否在生成消息不让点击事件
            if (!conversationViewModel.getFabStatus()) {
                coroutineScope.launch {
                    if (imagePathList.isNotEmpty()) {
                        conversationViewModel.sendAnalyzeImageMsg(imagePathList.toList(), text)
                    } else {
                        conversationViewModel.sendMessage(text)
                    }
                }
            }
        }, navigator
    )
}

@Composable
fun InputTopIn(state: LazyListState, navigator: Navigator) {
    val subScope = rememberCoroutineScope()
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val list = mutableListOf<MenuActModel>()
    list.add(MenuActModel("相册"))
    if (checkSystem() == OsStatus.ANDROID || checkSystem() == OsStatus.IOS) {
        list.add(MenuActModel("拍摄"))
    } else {
        list.add(MenuActModel("翻译"))
    }
    LazyRow(state = state, modifier = Modifier.animateContentSize()) {
        items(list.size) { index ->
            Button(modifier = Modifier.padding(start = 10.dp, bottom = 4.dp).size(86.dp, 38.dp),
                onClick = {
                    subScope.launch {
                        when (list[index].title) {
                            "相册" -> {
                                conversationViewModel.selectImage()
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
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}


@Composable
fun TextInputIn(
    sendMessage: (String) -> Unit, navigator: Navigator
) {
    val scope = rememberCoroutineScope()
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val isFabExpanded by conversationViewModel.isFabExpanded.collectAsState()
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var hasFocus by remember { mutableStateOf(false) } //判断焦点
    val focusManager = LocalFocusManager.current
    val maxInputSize = 300

    Box(
        // Use navigationBarsPadding() imePadding() and , to move the input panel above both the
        // navigation bar, and on-screen keyboard (IME)
        modifier = Modifier.navigationBarsPadding().imePadding(),
    ) {
        Column {
            if (!isFabExpanded ) { //isFabExpanded=true正在回答
                InputTopIn(rememberLazyListState(), navigator)
            }
            HorizontalDivider(Modifier.height(0.2.dp))
            //如果有图片，要插入图片列表
            ImageSelectIn()
            //输入监听区域
            Box(
                Modifier.padding(horizontal = 4.dp).padding(top = 6.dp, bottom = 10.dp)
            ) {
                Row {
                    TextField(value = text, onValueChange = { newText ->
                        if (newText.text.length <= maxInputSize) {
                            text = newText
                        }
                    },
                        label = null, placeholder = {
                            Text(
                                "Ask me anything", fontSize = 12.sp
                            )
                        },
//                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                            .background(Color.Transparent)
                            .verticalScroll(rememberScrollState())
                            .onFocusChanged { focusState -> hasFocus = focusState.isFocused }
                            .weight(1f).KeyEventEnter {
                                scope.launch {
                                    val textClone = text.text
                                    text = TextFieldValue("")
                                    sendMessage(textClone)
                                }
                            }, colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent, //去除边框
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedTextColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                            focusedIndicatorColor = Color.Transparent, //去除底部横线
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    //发生、中断 融合为一个按钮
                    EnterEventButton(isFabExpanded, sendBlock = {
                        if (text.text.trim().isNotEmpty()) {
                            val textClone = text.text
                            text = TextFieldValue("")
//                            focusManager.clearFocus() //清除焦点，注意线程
                            sendMessage(textClone)
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun EnterEventButton(isFabExpanded: Boolean, sendBlock: () -> Unit) {
    val subScope = rememberCoroutineScope()
    val conversationViewModel = koinViewModel(ConversationViewModel::class)

    ExtendedFloatingActionButton(
        text = {
            Text(text = "Stop Generating", color = Color.White)
        },
        icon = {
            if (isFabExpanded) { //中断按钮
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop Generating",
                    tint = Color.White,
                    modifier = Modifier.size(23.dp)
                )
            } else { //发送按钮
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "sendMessage",
                    tint = Color.White,
                    modifier = Modifier.size(23.dp)
                )
            }
        },
        onClick = {
            if (isFabExpanded) {
                conversationViewModel.stopReceivingResults()
            } else {
                subScope.launch { sendBlock() }
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .animateContentSize().padding(end = 6.dp),
        expanded = isFabExpanded,
        containerColor = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ImageSelectIn() {
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val imagePathList by conversationViewModel.imageListState.collectAsState() //选中的图片
    //必须是最后一轮对话，且是图片解析，解析图片完清除所有？
    if (imagePathList.isNotEmpty()) {
        LazyRow(
            state = rememberLazyListState(), modifier = Modifier.padding(start = 10.dp, top = 4.dp)
                .wrapContentSize().background(cHalfGrey80717171())
        ) {
            items(imagePathList.size) { index ->
                Box(modifier = Modifier.padding(3.dp).size(35.dp)) {
                    AsyncImage(
                        imagePathList[index].path,
                        contentDescription = imagePathList[index].name,
//                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
//                    IconButton(
//                        onClick = {
//                            conversationViewModel.deleteImage(index)
//                        },
//                        modifier = Modifier.size(20.dp).align(Alignment.Center)
//                    ) {
//                        Icon(
//                            Icons.Default.Delete,
//                            modifier = Modifier.fillMaxSize(),
//                            contentDescription = "Delete",
//                            tint = Color.DarkGray
//                        )
//                    }
                }
            }
        }
    }
}
