package com.hwj.ai.ui.me

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hwj.ai.global.LLM_API_KEY
import com.hwj.ai.global.LLM_MODEL
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.global.baseHostUrl
import com.hwj.ai.global.urlChatCompletions
import com.hwj.ai.models.LLMModel
import com.hwj.ai.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator

/**
 * @author by jason-何伟杰，2025/3/14
 * des:用于调试本地模型参数接口
 */
@Composable
fun ChooseLLMScreen(navigator: Navigator) {
    val subScope = rememberCoroutineScope()
    val settingsViewModel = koinViewModel(SettingsViewModel::class)
    val modelList = settingsViewModel.localLLMState.collectAsState().value
    LaunchedEffect(Unit) {
        subScope.launch {
            settingsViewModel.fetchLLMModels()
        }
    }

    LazyColumn(Modifier.padding(top = 80.dp)) {
        items(modelList) { bean ->
            CardLLM(bean)
        }
    }
}

@Composable
fun CardLLM(bean: LLMModel) {
    ThemeChatLite {
        Card(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
            Row {
                Text(text = bean.model!!, modifier = Modifier.fillMaxWidth().padding(start=5.dp))
                Button(onClick = {
                    LLM_API_KEY = bean.sk!!
                    urlChatCompletions = bean.url!!
                    baseHostUrl = bean.hostUrl!!
                    LLM_MODEL = bean.model!!
                }) {
                    Text("应用置本地")
                }
            }
        }
    }
}