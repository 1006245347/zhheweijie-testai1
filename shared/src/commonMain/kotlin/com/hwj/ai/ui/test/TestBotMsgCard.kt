package com.hwj.ai.ui.test

import androidx.compose.runtime.Composable
import com.hwj.ai.data.local.mockMinList
import io.github.vinceglb.filekit.FileKit
import io.ktor.client.request.forms.formData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * @author by jason-何伟杰，2025/2/18
 * des:想本地sse数据测试流式布局控件，markdown
 */
@Composable
fun TestBotMsgCard() {

}

fun sendStreamMsg(): Flow<String> =
    flow {
        withContext(Dispatchers.Default) {
            val list = mockMinList()

        }
    }

fun cc(){

}