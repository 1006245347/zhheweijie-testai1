package com.hwj.ai.except

import androidx.compose.runtime.Composable
import com.hwj.ai.models.MessageModel


@Composable
expect fun OpenCameraScreen(isOpen: Boolean, onBack: (Boolean) -> Unit)

@Composable
expect fun BotMsgMenu(message: MessageModel)

@Composable
expect fun ToolTipCase(tip: String, content: @Composable () -> Unit)