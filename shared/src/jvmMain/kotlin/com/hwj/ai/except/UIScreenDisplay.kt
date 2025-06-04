package com.hwj.ai.except

import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GeneratingTokens
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.capture.LocalMainWindow
import com.hwj.ai.capture.ScreenshotOverlay11
import com.hwj.ai.capture.getPlatformCacheImgDir11
import com.hwj.ai.capture.saveToFile11
import com.hwj.ai.checkSystem
import com.hwj.ai.global.DATA_SIZE_INPUT_SEND
import com.hwj.ai.global.Event
import com.hwj.ai.global.EventHelper
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.PrimaryColor
import com.hwj.ai.global.onlyDesktop
import com.hwj.ai.global.workInSub
import com.hwj.ai.models.MessageModel
import com.hwj.ai.selection.GlobalMouseHook9
import com.hwj.ai.ui.FloatWindowInside
import com.hwj.ai.ui.showMainWindow
import com.hwj.ai.ui.viewmodel.ChatViewModel
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import com.hwj.ai.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import java.awt.Desktop
import java.net.URI

@Composable
actual fun OpenCameraScreen(isOpen: Boolean, onBack: (Boolean, ByteArray?) -> Unit) {
}

@Composable
actual fun BotMsgMenu(message: MessageModel) {
    val conversationViewModel = koinViewModel(ConversationViewModel::class)
    Row {
        TooltipArea(
            tooltip = {
                Surface(modifier = Modifier.padding(2.dp)) {
                    Text(text = "复制", Modifier.padding(4.dp), fontSize = 12.sp)
                }
            },
            delayMillis = 100,
            tooltipPlacement = TooltipPlacement.CursorPoint(offset = DpOffset(5.dp, 5.dp))
        ) {
            IconButton(onClick = { //复制 不成功是字数超了
                if (message.answer.length > DATA_SIZE_INPUT_SEND) {
                    conversationViewModel.copyToClipboard(
                        message.answer.substring(
                            0,
                            DATA_SIZE_INPUT_SEND
                        )
                    )
                } else {
                    conversationViewModel.copyToClipboard(message.answer)
                }
            }, modifier = Modifier.padding(start = 15.dp, end = 10.dp)) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = PrimaryColor, modifier = Modifier.size(20.dp)
                )
            }
        }

        TooltipArea(
            tooltip = { //鼠标移动浮动指向提示
                Surface(modifier = Modifier.padding(2.dp)) {
                    Text(text = "重新生成", Modifier.padding(4.dp), fontSize = 12.sp)
                }
            },
            delayMillis = 100,
            tooltipPlacement = TooltipPlacement.CursorPoint(offset = DpOffset(5.dp, 5.dp))
        ) {
            IconButton(onClick = { //重新生成
                conversationViewModel.workInSub {
                    conversationViewModel.generateMsgAgain()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.GeneratingTokens,
                    contentDescription = "Generate Again",
                    tint = PrimaryColor, modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
actual fun ToolTipCase(modifier: Modifier?, tip: String, content: @Composable () -> Unit) {
    TooltipArea(
        tooltip = { //鼠标移动浮动指向提示
            Surface(modifier = Modifier.padding(2.dp)) {
                Text(text = tip, Modifier.padding(4.dp), fontSize = 12.sp)
            }
        },
        delayMillis = 100,
        tooltipPlacement = TooltipPlacement.CursorPoint(offset = DpOffset(5.dp, 5.dp)),
        modifier = modifier ?: Modifier
    ) {
        content()
    }
}


actual fun isMainThread(): Boolean {
    return Thread.currentThread().name == "main"
}

@Composable
actual fun ScreenShotPlatform(onSave: (String?) -> Unit) {
    val mainWindow = LocalMainWindow.current
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val subScope = rememberCoroutineScope()
    val isShotState = chatViewModel.isShotState.collectAsState().value
    val isHotShotState = chatViewModel.isShotByHotKeyState.collectAsState().value
    if (isShotState && onlyDesktop()) {
        ScreenshotOverlay11(mainWindow = mainWindow, onCapture = { pic ->
            val file = saveToFile11(pic)
            onSave(file)
            if (isHotShotState && file != null) {
                EventHelper.post(Event.AnalyzePicEvent(file))
                GlobalMouseHook9.bring2Front()
            }
            //新建会话会清除
        }, onCancel = {
            subScope.launch(Dispatchers.Main) {
                chatViewModel.shotScreen(false)
            }
        })
    }
}

//AI 划词工具
//只实现windows的划词
@Composable
actual fun HookSelection() {
    if (checkSystem() != OsStatus.WINDOWS) return
    //浮窗
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val settingsViewModel = koinViewModel(SettingsViewModel::class)
    val useSelectState by settingsViewModel.useSelectState.collectAsState()
    val useHotKeyState by settingsViewModel.useHotKeyState.collectAsState()
    val isShotState by chatViewModel.isShotState.collectAsState()
    val subScope = rememberCoroutineScope()
    LaunchedEffect(useSelectState || useHotKeyState) { //是否开启划词功能
        if (useSelectState || useHotKeyState) {
            subScope.launch(Dispatchers.IO) {
                GlobalMouseHook9.start(useHotKeyState, appBlock = { info ->
                    chatViewModel.findAppInfo(info)
                }, contentBlock = { content ->
                    if (!isShotState)
                        content?.let {
                            chatViewModel.findSelectText(content)
//                            println("result>$content")
                            chatViewModel.preWindow(true)
                        }
                })
            }
            //拆线程才不卡
            if (useSelectState) {
                subScope.launch(Dispatchers.IO) {
                    while (useSelectState) {
                        delay(50)
                        if (GlobalMouseHook9.isDragging) {
                            GlobalMouseHook9.handleMouseAct()
                            GlobalMouseHook9.isDragging = false
                        }
                    }
                }
            }
        } else {

            GlobalMouseHook9.stop()
        }
    }
}

@Composable
actual fun FloatWindow() {
    FloatWindowInside()
}

@Composable
actual fun BringMainWindowFront() {
    showMainWindow(true)
}

actual fun getShotCacheDir(): String? {
    return getPlatformCacheImgDir11().absolutePath
}

actual object EnvLoader {

    actual fun loadEnv(): Map<String, String> {
        val classLoader = EnvLoader::class.java.classLoader
        val inputStream = classLoader.getResourceAsStream(".env") //?: return emptyMap()
        val map = mutableMapOf<String, String>()
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isNotEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
                    val (key, value) = trimmed.split("=", limit = 2)
                    map[key.trim()] = value.trim()
                }
            }
        }
        return map
    }
}

@Composable
actual fun switchUrlByBrowser(url: String) {
    if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        Desktop.getDesktop().browse(URI(url))
    }
}