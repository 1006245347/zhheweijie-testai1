package com.hwj.ai.global

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ToastUtils {

//    private val _message = mutableStateOf<String?>(null)
//    val message: State<String?> = _message
private val _message = MutableStateFlow<String?>(null)

    val messageState= _message.asStateFlow()
    fun show(msg: String) {
        _message.value = msg
    }

    fun dismiss() {
        _message.value = null
    }
}