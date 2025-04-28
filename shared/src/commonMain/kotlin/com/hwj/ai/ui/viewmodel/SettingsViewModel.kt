package com.hwj.ai.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.hwj.ai.data.repository.GlobalRepository
import com.hwj.ai.data.repository.SettingsRepository
import com.hwj.ai.global.CODE_HOT_KEY
import com.hwj.ai.global.CODE_LANGUAGE_ZH
import com.hwj.ai.global.CODE_SELECTION_USE
import com.hwj.ai.global.NotificationsManager
import com.hwj.ai.global.getCacheBoolean
import com.hwj.ai.global.printD
import com.hwj.ai.models.LLMModel
import com.hwj.ai.global.StrUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

/**
 * @author by jason-何伟杰，2025/2/26
 * des: MVI+Repository+Jetpack Compose
 */
class SettingsViewModel(
    private val repository: SettingsRepository,
    private val globalRepository: GlobalRepository, private val toastManager: NotificationsManager
) : ViewModel() {

    //唯一可信数据源
    private val _uiObs = MutableStateFlow(SettingsUiState())
    val uiState = _uiObs.asStateFlow()

    private val _localLLMObs = mutableStateListOf<LLMModel>()
    val localLLMState = MutableStateFlow(_localLLMObs).asStateFlow()

    //语言、快捷键、模型选中、划词、截图路径
    private val _isChineseObs = MutableStateFlow(true)
    val isChineseState = MutableStateFlow(_isChineseObs).asStateFlow()

    //是否启用划词
    private val _useSelectObs = MutableStateFlow(false)
    val useSelectState = _useSelectObs.asStateFlow()

    private val _useHotKeyObs = MutableStateFlow(false)
    val useHotKeyState = _useHotKeyObs.asStateFlow()

     fun initialize() {
        viewModelScope.launch {
            _isChineseObs.value = getCacheBoolean(CODE_LANGUAGE_ZH, true)
            _useSelectObs.value = getCacheBoolean(CODE_SELECTION_USE, true)
            _useHotKeyObs.value = getCacheBoolean(CODE_HOT_KEY, true)
            println("isChinese>${_isChineseObs.value}")
            StrUtils.switchTo(_isChineseObs.value)
        }
    }

    suspend fun fetchLLMModels() {
        _localLLMObs.clear()
        _localLLMObs.addAll(globalRepository.localModelConfig())
    }

    //触发对应的用户事件
    fun processIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.LoadData -> {
                getAppData()
            }

            is SettingsIntent.ItemClicked -> {
                getItemData(intent.item)
            }
        }
    }

    fun getAppData() {
        _uiObs.update { it.copy(isLoading = true) } //设置状态为加载中
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val list = repository.getAppData()
                _uiObs.update { it.copy(isLoading = false, data = list) } //设置状态为加载完成
            } catch (e: Exception) {
                _uiObs.update { it.copy(isLoading = false, data = emptyList(), error = e) }
            }
        }
    }

    fun getItemData(string: String) {
        printD(string)
    }

    override fun onCleared() {
        super.onCleared()
    }
}

//页面对应的UI状态，高低频率刷新的可拆分多个类,基本全有默认值
data class SettingsUiState(
    val isLoading: Boolean = false, var data: List<String> = emptyList(),
    val error: Throwable? = null
) {
    val canSee: Boolean = data.isEmpty() || !isLoading//派生状态？
}

//页面对应的UI事件标识
sealed class SettingsIntent {
    object LoadData : SettingsIntent()
    data class ItemClicked(val item: String) : SettingsIntent()
}