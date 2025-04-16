package com.hwj.ai.ui.viewmodel

import com.hwj.ai.data.http.toast
import com.hwj.ai.data.repository.GlobalRepository
import com.hwj.ai.except.ClipboardHelper
import com.hwj.ai.global.CODE_IS_DARK
import com.hwj.ai.global.Event
import com.hwj.ai.global.EventHelper
import com.hwj.ai.global.NotificationsManager
import com.hwj.ai.global.getCacheBoolean
import com.hwj.ai.global.getMills
import com.hwj.ai.models.LLMModel
import com.hwj.ai.ui.global.AISelectIntent
import com.hwj.ai.ui.global.GlobalIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class ChatViewModel(
    private val globalRepo: GlobalRepository,
    private val toastManager: NotificationsManager,
    private val clipboardHelper: ClipboardHelper
) : ViewModel() {

    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened = _drawerShouldBeOpened.asStateFlow()

    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }

    //全局参数状态
    private val _configObs = MutableStateFlow(ModelConfigState())
    val configState = _configObs.asStateFlow()
    private var lastTime = getMills()

    //本地主题值修改
    private val _darkObs = MutableStateFlow(false)
    val darkState = _darkObs.asStateFlow()

    //首页是否折叠抽屉
    private val _isCollapsedObs = MutableStateFlow(false)
    val isCollapsedState = _isCollapsedObs.asStateFlow()

    //是否触发截图
    private val _isShotObs = MutableStateFlow(false)
    val isShotState = _isShotObs.asStateFlow()

    //是否启用划词
    private val _useSelectObs = MutableStateFlow(true)
    val useSelectState = _useSelectObs.asStateFlow()

    //显示浮窗功能
    private val _isPreWindowObs = MutableStateFlow(false)
    val isPreWindowState = _isPreWindowObs.asStateFlow()

    //划词选中的数据
    private val _appInfoObs = MutableStateFlow<String?>(null)
    val appInfoState = _appInfoObs.asStateFlow()

    private val _selectTextObs = MutableStateFlow<String?>(null)
    val selectTextState = _selectTextObs.asStateFlow()

    fun processConfig(intent: ModelConfigIntent) {
        when (intent) {
            is ModelConfigIntent.LoadData -> {
                fetchModelConfig()
            }

            is ModelConfigIntent.UpdateData -> {
                if (getMills() - lastTime > 30000) {
                    fetchModelConfig()
                }
            }
        }
    }

    fun processGlobal(intent: GlobalIntent) {
        when (intent) {
            is GlobalIntent.CheckDarkTheme -> {
                fetchDarkStatus()
            }
        }
    }

    fun processAiSelect(intent: AISelectIntent) {
        when (intent) {
            is AISelectIntent.CopyData -> {
                _selectTextObs.value?.let {
                    copyToClipboard(it)
                }
            }

            else -> {
                AISearch(intent)
            }
        }
    }

    private fun fetchModelConfig() {
        _configObs.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = globalRepo.fetchModelConfig()
                _configObs.update { it.copy(isLoading = false, data = result) }
            } catch (e: Exception) {
                _configObs.update { it.copy(error = e.toString()) }
            }
        }
    }

    private fun fetchDarkStatus() {
        viewModelScope.launch {
            val isDark = getCacheBoolean(CODE_IS_DARK)
            _darkObs.value = isDark
        }
    }

    private fun AISearch(intent: AISelectIntent) {

        //对选中的词用完要立刻清掉，不然浮窗又弹？
    }


    fun collapsedPage() {
        _isCollapsedObs.value = !_isCollapsedObs.value
    }

    fun shotScreen(flag: Boolean) {
        println("shot>$flag")
        _isShotObs.value = flag
    }

    fun preWindow(flag: Boolean) {
        _isPreWindowObs.value = flag
    }

    fun findAppInfo(info: String?) {
        info?.let {
            _appInfoObs.value = info
        }
    }

    fun findSelectText(text: String?) {
        text?.let {
            _selectTextObs.value = text
        }
    }

    fun copyToClipboard(text: String) {
        try {
            clipboardHelper.copyToClipboard(text)
        } catch (e: Exception) {
            toast(toastManager, "err", e.message.toString())
        }
    }

    val eventObs = viewModelScope.launch {
        EventHelper.events.collect { event ->
            println("collect-event?")
            when (event) {
                is Event.HotKeyEvent -> {
                    if (event.code == 1 && !_isShotObs.value) {
                        //清空界面、缓存数据？？
                        shotScreen(true)
                        println("hot>${_isShotObs.value}")
                    }
                }

                else -> {}
            }
        }
    }
}

data class ModelConfigState(
    val isLoading: Boolean = false,
    val data: List<LLMModel>? = null,
    val error: String? = null
)
//解决不了基类的写法
//data class ModelConfigState(val json: String? = null) : BaseUiState<String>(data = json)

sealed class ModelConfigIntent {
    //获取所有的大模型数据，解析后保存到本地
    data object LoadData : ModelConfigIntent()

    //判断时间间隔是否需要更新数据，主动拉取，
    data class UpdateData(val time: Long) : ModelConfigIntent()
}


//// MyState 继承自 UiState
//data class MyState(
//    val items: List<String> = emptyList() // 你可以根据具体需要定制数据类型
//) : BaseUiState<List<String>>(data = items)