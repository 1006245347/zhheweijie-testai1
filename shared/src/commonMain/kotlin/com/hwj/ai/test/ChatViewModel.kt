package com.hwj.ai.test

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import moe.tlaster.precompose.viewmodel.ViewModel
import testai1.shared.generated.resources.Res

class ChatViewModel : ViewModel() {

    private val _msgList = mutableStateListOf<String>()
    var inputTxt = mutableStateOf("")



    fun onInputChange(txt: String) {
        inputTxt.value = txt

    }
}