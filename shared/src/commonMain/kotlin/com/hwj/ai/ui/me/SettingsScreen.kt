package com.hwj.ai.ui.me

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.hwj.ai.ui.viewmodel.SettingsIntent
import com.hwj.ai.ui.viewmodel.SettingsViewModel
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun SettingsScreen(navigator: Navigator) {

    val viewModel = koinViewModel(SettingsViewModel::class)
    val uiState = viewModel.uiState.collectAsState().value

    //Unit首次组合触发，当条件变化还能自动取消上次未结操作
    LaunchedEffect(Unit) {
        viewModel.processIntent(SettingsIntent.LoadData)//自动拉数据
    }

    Column {
        if (uiState.isLoading) {
            Text(text = "loading...")
        } else if (uiState.error != null) {
            Text(text = "err>>${uiState.error}")
        } else {
            LazyColumn {
                items(uiState.data) { item ->
                    Text(text = item, modifier = Modifier.clickable {
                        //发生交互事件
                        viewModel.processIntent(SettingsIntent.ItemClicked(item))
                    })
                }
            }
        }

        Button(onClick = { viewModel.processIntent(SettingsIntent.LoadData) }) {
            Text(text = "load data")//点击拉取数据
        }
    }
}