package com.hwj.ai.except

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hwj.ai.models.MessageModel

//手机端拍照
@Composable
expect fun OpenCameraScreen(isOpen: Boolean, onBack: (Boolean,ByteArray?) -> Unit)

//聊天界面消息富文本控件
@Composable
expect fun BotMsgMenu(message: MessageModel)

//处理desktop端鼠标指向图标文字提示
@Composable
expect fun ToolTipCase(modifier: Modifier?=null,tip: String, content: @Composable () -> Unit)

//是否在主线程
expect fun isMainThread():Boolean

//desktop截图
@Composable
expect fun ScreenShotPlatform(onSave: (String?) -> Unit)

//启动划词
@Composable
expect fun HookSelection()

//划词工具小浮窗 AI搜索
@Composable
expect fun FloatWindow()

@Composable
expect fun BringMainWindowFront()

//缓存截图的目录
expect fun getShotCacheDir():String?

//使用.env文件处理变量
object Env {
    private val values: Map<String, String> by lazy { EnvLoader.loadEnv() }

    fun get(key: String): String? = values[key]
}

expect object EnvLoader {
    fun loadEnv():Map<String,String>
}

//链接跳转浏览器
@Composable
expect fun switchUrlByBrowser(url:String)