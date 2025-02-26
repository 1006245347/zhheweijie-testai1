package com.hwj.ai.ui.viewmodel

import com.hwj.ai.data.repository.SettingsRepository
import com.hwj.ai.global.printD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {

    //唯一可信数据源
    private val _uiObs = MutableStateFlow(SettingsUiState())
    val uiState = _uiObs.asStateFlow()


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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = repository.getAppData()
                _uiObs.update { it.copy(isLoading = false, data = list) } //设置状态为加载完成
            } catch (e: Exception) {
                _uiObs.update { it.copy(isLoading = false, data = emptyList(), error = e) }
            }
        }
    }

    fun getItemData(string: String){
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