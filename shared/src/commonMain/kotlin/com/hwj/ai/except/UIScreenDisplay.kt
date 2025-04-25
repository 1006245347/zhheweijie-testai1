package com.hwj.ai.except

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hwj.ai.models.MessageModel


@Composable
expect fun OpenCameraScreen(isOpen: Boolean, onBack: (Boolean,ByteArray?) -> Unit)

@Composable
expect fun BotMsgMenu(message: MessageModel)

@Composable
expect fun ToolTipCase(modifier: Modifier?=null,tip: String, content: @Composable () -> Unit)

expect fun isMainThread():Boolean

@Composable
expect fun ScreenShotPlatform(onSave: (String?) -> Unit)

@Composable
expect fun HookSelection()

@Composable
expect fun FloatWindow()

expect fun getShotCacheDir():String?