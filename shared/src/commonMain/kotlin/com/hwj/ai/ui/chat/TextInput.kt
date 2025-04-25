package com.hwj.ai.ui.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hwj.ai.checkSystem
import com.hwj.ai.createPermission
import com.hwj.ai.data.local.PermissionPlatform
import com.hwj.ai.except.ScreenShotPlatform
import com.hwj.ai.except.ToolTipCase
import com.hwj.ai.global.BackInnerColor1
import com.hwj.ai.global.NavigationScene
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.PrimaryColor
import com.hwj.ai.global.ToastUtils
import com.hwj.ai.global.cBlue244260FF
import com.hwj.ai.global.cBlue629DE8
import com.hwj.ai.global.cDeepLine
import com.hwj.ai.global.cGrey666666
import com.hwj.ai.global.isDarkTxt
import com.hwj.ai.global.isLightTxt
import com.hwj.ai.global.onlyDesktop
import com.hwj.ai.models.MenuActModel
import com.hwj.ai.ui.global.KeyEventEnter
import com.hwj.ai.ui.viewmodel.ChatViewModel
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun TextInput(
    conversationViewModel: ConversationViewModel, navigator: Navigator
) {
    val subScope = rememberCoroutineScope()
    val imagePathList by conversationViewModel.imageListState.collectAsState() //选中的图片

    TextInputIn(
        sendMessage = { text ->
            //判断是否在生成消息不让点击事件
            if (!conversationViewModel.getFabStatus()) {
                if (imagePathList.isNotEmpty()) {
                    subScope.launch(Dispatchers.Default) {
                        conversationViewModel.sendAnalyzeImageMsg(imagePathList.toList(), text)
                    }
                } else {
                    conversationViewModel.sendTxtMessage(text)
                    //test function
//                    conversationViewModel.sendTxtToolMessage(
//                        "What's the weather like in San Francisco, Tokyo, and Paris?",
//                        toolWeather
//                    )
                }
            }
        }, navigator
    )
}

@Composable
fun InputTopIn(state: LazyListState, navigator: Navigator) {
    val subScope = rememberCoroutineScope()
    val thinkCheckState = remember { mutableStateOf(false) }
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val isDark = chatViewModel.darkState.collectAsState().value
    val isShotState = chatViewModel.isShotState.collectAsState().value
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val needPermissionCamera = remember { mutableStateOf(false) }
    val needPermissionGallery = remember { mutableStateOf(false) }
    val imageList = conversationViewModel.imageListState.collectAsState().value
    val cameraPath = remember { mutableStateOf<String?>(null) }
    val cList by conversationViewModel.conversationsState.collectAsState()
    val list = mutableListOf<MenuActModel>()
    list.add(MenuActModel("相册"))
    if (checkSystem() == OsStatus.ANDROID || checkSystem() == OsStatus.IOS) {
        list.add(MenuActModel("拍摄"))
    } else {
        list.add(MenuActModel("截图"))
    }
//    list.add(MenuActModel("翻译"))

    if (needPermissionCamera.value) { //权限设置
        createPermission(PermissionPlatform.CAMERA, grantedAction = {
            subScope.launch {
                if (imageList.size == 2) {
                    conversationViewModel.toast("最多选取两张图片", "toast")
                } else {
                    cameraPath.value =
                        navigator.navigateForResult(NavigationScene.Camera.path).toString()
                    cameraPath.value?.let {
                        conversationViewModel.addCameraImage(PlatformFile(it))
                    }
                }
                needPermissionCamera.value = false
            }
        }, deniedAction = {
            needPermissionCamera.value = false
        })
    }
    if (needPermissionGallery.value) {
        createPermission(PermissionPlatform.GALLERY, grantedAction = {
            subScope.launch {
                conversationViewModel.selectImage()
                needPermissionGallery.value = false //用户打开后却文件管理器点击取消，没法重置
            }
        }, deniedAction = {
            needPermissionGallery.value = false
        })
    }

    Row(Modifier.fillMaxWidth()) {
        LazyRow(state = state, modifier = Modifier.wrapContentWidth()) {
            items(list.size) { index ->
                Button(
                    modifier = Modifier.padding(start = 10.dp, bottom = 4.dp).size(72.dp, 30.dp),
                    contentPadding = PaddingValues(0.dp),//ButtonDefaults里带padding，坑
                    onClick = {
                        when (list[index].title) {
                            "相册" -> {
                                needPermissionGallery.value = true
//                            subScope.launch { //测试入口
//                                delay(3000)
//                                chatViewModel.preWindow(true)
//                            }
                            }

                            "拍摄" -> {
                                needPermissionCamera.value = true
                            }

                            "翻译" -> {

                            }

                            "截图" -> {
                                if (conversationViewModel.checkSelectedImg()) {
                                    chatViewModel.shotByHotKey(false)
                                    chatViewModel.shotScreen(true)
                                } else {
                                    ToastUtils.show("最多处理两张图片")
                                }
                            }
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()) {
                        Text(
                            text = list[index].title,
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)//.background(Color.Blue)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Box(
            Modifier.height(30.dp).padding(end = 10.dp).clip(RoundedCornerShape(20.dp))
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    1.dp, if (thinkCheckState.value) cBlue629DE8() else cDeepLine()
                )
            ) {
                Text(
                    text = "深度思考",
                    fontSize = 11.sp,
                    color = if (thinkCheckState.value) cBlue629DE8() else {
                        if (isDark) isDarkTxt() else isLightTxt()
                    },
                    modifier = Modifier.background(
                        color = if (thinkCheckState.value) cBlue244260FF() else MaterialTheme.colorScheme.background
//                            if (isDark) isDarkPanel() else isLightPanel()
                    ).padding(bottom = 4.dp, start = 13.dp, end = 13.dp)
                        .clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            thinkCheckState.value = !thinkCheckState.value
                            conversationViewModel.setThinkUsed(thinkCheckState.value)
                        }
                )
            }
        }
    }

    if (isShotState) {
        ScreenShotPlatform(onSave = { filePath ->
            filePath?.let {
                subScope.launch {
                    conversationViewModel.addCameraImage(PlatformFile(filePath))
                }
            }
        })
        return
    }
}

@Composable
fun TextInputIn(
    sendMessage: (String) -> Unit, navigator: Navigator
) {
    val scope = rememberCoroutineScope()
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val isFabExpanded by conversationViewModel.isFabExpanded.collectAsState()
    var hasFocus by remember { mutableStateOf(false) } //判断焦点
    val focusManager = LocalFocusManager.current
    val maxInputSize = 300
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val isDark = chatViewModel.darkState.collectAsState().value
    val inputHint = if (onlyDesktop()) "给AI发送消息（Enter+Shift换行、Enter发送）" else "给AI发送消息"
    Box(
        // Use navigationBarsPadding() imePadding() and , to move the input panel above both the
        // navigation bar, and on-screen keyboard (IME)
        modifier = Modifier.navigationBarsPadding().imePadding(),
    ) {
        Column {
            if (!isFabExpanded) { //isFabExpanded=true正在回答
                InputTopIn(rememberLazyListState(), navigator)
            }

            HorizontalDivider(thickness = (0.5f).dp, color = cDeepLine())

            ImageSelectIn()   //如果有图片，要插入图片列表

            Box(      //输入监听区域
                Modifier.padding(horizontal = 4.dp).padding(top = 6.dp, bottom = 10.dp)
            ) {
                Row {
                    TextField(value = conversationViewModel.inputTxt,
                        onValueChange = { newText ->
                            if (newText.text.length <= maxInputSize) {
                                conversationViewModel.onInputChange(newText)
                            }
                        },
                        label = null, singleLine = false,
                        placeholder = {
                            Text(
                                inputHint, fontSize = 12.sp
                            )
                        }, //固定行高，输入时应用高度就不会一直抖动
                        maxLines = 3,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 24.sp,
                            color = if (isDark) BackInnerColor1 else isLightTxt()
                        ),
//                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)
                            .background(Color.Transparent)
                            .verticalScroll(rememberScrollState())
                            .imePadding()//适配键盘高度
                            .onFocusChanged { focusState -> hasFocus = focusState.isFocused }
                            .weight(1f).KeyEventEnter(enter = {
                                scope.launch {
                                    val textClone = conversationViewModel.inputTxt.text.trim()
                                    conversationViewModel.onInputChange("")
                                    sendMessage(textClone)
                                }
                            }, shift = {
                                val textClone = conversationViewModel.inputTxt.text
                                conversationViewModel.onInputChange(textClone + "\n")
                            }),
                        colors = TextFieldDefaults.colors(
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
                        if (conversationViewModel.inputTxt.text.trim().isNotEmpty()) {
                            val textClone = conversationViewModel.inputTxt.text.trim()
                            conversationViewModel.onInputChange("")
//                            focusManager.clearFocus()  //清除焦点，注意线程
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
//    val subScope = rememberCoroutineScope()
    val conversationViewModel = koinViewModel(ConversationViewModel::class)

//    ToolTipCase(tip = "发送/停止", content = {
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
            println("click>$isFabExpanded")
            if (isFabExpanded) {
                conversationViewModel.stopReceivingResults()
            } else {
                sendBlock()
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .animateContentSize().padding(end = 6.dp),
        expanded = isFabExpanded,
        containerColor = PrimaryColor
    )

//    })
}

@Composable
fun ImageSelectIn() {
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    val imagePathList by conversationViewModel.imageListState.collectAsState() //选中的图片
    val isStopUseImageState by conversationViewModel.isStopUseImageState.collectAsState()
    val messageList by conversationViewModel.messagesState.collectAsState()
    //必须是最后一轮对话，且是图片解析，解析图片完清除所有？
    LazyRow(
        state = rememberLazyListState(), modifier = Modifier.padding(start = 10.dp, top = 4.dp)
            .wrapContentSize()//.background(cHalfGrey80717171())
    ) {
        items(imagePathList.size) { index ->
            Box(modifier = Modifier.padding(3.dp).size(35.dp)) {
                AsyncImage(
                    imagePathList[index].path,
                    contentDescription = imagePathList[index].name,
//                        contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = {
                        conversationViewModel.deleteImage(index)
                    },
                    modifier = Modifier.size(20.dp).align(Alignment.TopEnd)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = "Delete",
                        tint = cGrey666666()
                    )
                }
            }
        }
        if (onlyDesktop() && imagePathList.size > 0) {
            item {
                ToolTipCase(tip = "不再引用图片", content = {
                    IconButton(onClick = {
                        conversationViewModel.deleteImage(0, true)
                        conversationViewModel.setImageUseStatus(true)
//                        printList(messageList.values.toList()) //整个会话永远是1
                    }, modifier = Modifier.padding(start = 5.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "不再引用图 X",
                            tint = PrimaryColor
                        )
                    }
                })
            }
        }
    }
}
