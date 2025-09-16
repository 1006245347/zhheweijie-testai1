package com.hwj.ai.ui.me

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.except.getShotCacheDir
import com.hwj.ai.global.CODE_HOT_KEY
import com.hwj.ai.global.CODE_LANGUAGE_ZH
import com.hwj.ai.global.CODE_SELECTION_USE
import com.hwj.ai.global.DATA_APP_TOKEN
import com.hwj.ai.global.DATA_EMBED_TOKEN
import com.hwj.ai.global.PrimaryColor
import com.hwj.ai.global.StrUtils
import com.hwj.ai.global.cTransparent
import com.hwj.ai.global.getCacheBoolean
import com.hwj.ai.global.getCacheString
import com.hwj.ai.global.isDarkTxt
import com.hwj.ai.global.isLightTxt
import com.hwj.ai.global.onlyDesktop
import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.hwj.ai.global.printLog
import com.hwj.ai.global.saveBoolean
import com.hwj.ai.global.saveString
import com.hwj.ai.ui.global.Corner8
import com.hwj.ai.ui.global.DialogUtils
import com.hwj.ai.ui.viewmodel.ChatViewModel
import com.hwj.ai.ui.viewmodel.SettingsIntent
import com.hwj.ai.ui.viewmodel.SettingsViewModel
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun SettingsScreen(openState: MutableState<Boolean>) {
    val chatViewModel = koinViewModel(ChatViewModel::class)
    val viewModel = koinViewModel(SettingsViewModel::class)
    val isDark = chatViewModel.darkState.collectAsState().value
    val isChineseState = remember { mutableStateOf(StrUtils.switchLanguage) }

    var useSelectionState by remember { mutableStateOf(false) }
    var useHotKeyState by remember { mutableStateOf(false) }
    var useChineseState by remember { mutableStateOf(false) }
    val subScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        subScope.launch {
            useChineseState = getCacheBoolean(CODE_LANGUAGE_ZH, true)
            useHotKeyState = getCacheBoolean(CODE_HOT_KEY, true)
            useSelectionState = getCacheBoolean(CODE_SELECTION_USE, true)
        }
    }

    fun refresh() {
        viewModel.initialize()
    }

    DisposableEffect(Unit) { //页面关闭，响应重组最后的回调
        onDispose {
            openState.value = false
            refresh()
        }
    }

    DialogUtils.CreateDialog(
        openState,
        marginTop = 10.dp,
        paddingStart = 10.dp,
        dialogHeight = 400.dp
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { openState.value = false }) {
                        Icon(Icons.Filled.ArrowBackIosNew, "close", tint = PrimaryColor)
                    }
                }, title = {
                    Text(text = "Settings", color = if (isDark) isDarkTxt() else isLightTxt())
                }, colors = TopAppBarDefaults.topAppBarColors(
                    //smallTopAppBarColors
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = PrimaryColor,
                ), actions = {})

            HorizontalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Switch(checked = useChineseState, onCheckedChange = {
                    subScope.launch {
                        useChineseState = it
                        saveBoolean(CODE_LANGUAGE_ZH, useChineseState)
                        refresh()
                    }
                })
                Text(
                    text = isChineseState.value,
                    fontSize = 14.sp,
                    color = if (isDark) isDarkTxt() else isLightTxt(),
                    modifier = Modifier.padding(top = 13.dp, start = 5.dp)
                )
            }
            if (onlyDesktop()) {//桌面端才有
                Row {
                    Switch(checked = useHotKeyState, onCheckedChange = {
                        subScope.launch {
                            useHotKeyState = it
                            saveBoolean(CODE_HOT_KEY, useHotKeyState)
                            refresh()
                        }
                    })
                    Text(
                        text = "使用Alt A截图", fontSize = 14.sp,
                        color = if (isDark) isDarkTxt() else isLightTxt(),
                        modifier = Modifier.padding(top = 13.dp, start = 5.dp)
                    )
                }

                Row {
                    Switch(checked = useSelectionState, onCheckedChange = {
                        subScope.launch {
                            useSelectionState = it
                            saveBoolean(CODE_SELECTION_USE, useSelectionState)
                            refresh()
                        }
                    })
                    Text(
                        "启用划词搜索", fontSize = 14.sp,
                        color = if (isDark) isDarkTxt() else isLightTxt(),
                        modifier = Modifier.padding(top = 13.dp, start = 5.dp)
                    )
                }

                Row {
                    SelectionContainer {
                        Text(
                            text = "截图缓存目录：${getShotCacheDir()}", fontSize = 11.sp,
                            color = if (isDark) isDarkTxt() else isLightTxt(),
                            modifier = Modifier.padding(start = 1.dp, end = 10.dp)
                                .clickable {
                                    subScope.launch {
                                        FileKit.openFilePicker(
                                            directory = PlatformFile(
                                                getShotCacheDir()!!
                                            )
                                        )
                                    }
                                })
                    }
                }
            }
            //其他项
            InputTextInner(isDark, "Chat\ntoken", DATA_APP_TOKEN)
            InputTextInner(isDark, "Embed\ntoken", DATA_EMBED_TOKEN)
        }
    }
    subScope.launch {
        printLog("chat>${getCacheString(DATA_APP_TOKEN)}")
        printLog("embed>${getCacheString(DATA_EMBED_TOKEN)}")
    }
}

@Composable
fun InputTextInner(isDark: Boolean, des: String, key: String) {
    val scope = rememberCoroutineScope()
    var inputStr by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        var def = getCacheString(key)
        def?.let { inputStr = def }
    }

    Row(Modifier.fillMaxWidth()) {
        Text(des, fontSize = 10.sp, modifier = Modifier.width(50.dp))
        OutlinedTextField(
            value = inputStr, onValueChange = { newValue ->
                if (newValue.length < 200) {
                    inputStr = newValue
                } else {
                    printE("too long!")
                }
            }, modifier = Modifier.padding(start = 2.dp, 0.dp, 2.dp, 0.dp)
                .weight(1f).height(50.dp).background(cTransparent(), Corner8()), singleLine = true,
            placeholder = {
                Text(
                    "input api key",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 13.sp,
                    modifier = Modifier.background(cTransparent()).height(50.dp)
                )
            },
            textStyle = TextStyle(
                fontSize = 12.sp,
                lineHeight = 50.sp,
                color = if (isDark) isDarkTxt() else isLightTxt()
            )
        )
        Button(
            onClick = {
                scope.launch {
                    saveString(key, inputStr)
                }
            }, contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(45.dp, 35.dp)
        ) {
            Text(
                "OK",
                fontSize = 12.sp,
                color = if (isDark) isDarkTxt() else isLightTxt()
            )
        }
    }
}

@Composable
fun testAVI(navigator: Navigator) {
    val viewModel = koinViewModel(SettingsViewModel::class)
    val uiState = viewModel.uiState.collectAsState().value

    //Unit首次组合触发，当条件变化还能自动取消上次未结操作
    LaunchedEffect(Unit) {
        viewModel.processIntent(SettingsIntent.LoadData)//自动拉数据
    }

    Column {
        CenterAlignedTopAppBar(
            navigationIcon = {
                IconButton(onClick = { navigator.goBack() }) {
                    Icon(Icons.Filled.ArrowBackIosNew, "back", tint = PrimaryColor)
                }
            }, title = {
                Text(text = "Settings")
            }, colors = TopAppBarDefaults.topAppBarColors(
                //smallTopAppBarColors
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = PrimaryColor,
            ), actions = {})
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