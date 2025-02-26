package com.hwj.ai.ui.viewmodel

import com.hwj.ai.data.repository.GlobalRepository
import com.hwj.ai.global.getMills
import com.hwj.ai.global.getNowTime
import com.hwj.ai.models.LLMModel
import com.hwj.ai.ui.global.BaseUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class ChatViewModel(private val globalRepo: GlobalRepository) : ViewModel() {

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

    fun processIntent(intent: ModelConfigIntent) {
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

    private fun fetchModelConfig() {
        _configObs.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = globalRepo.fetchModelConfig()
                _configObs.update { it.copy(isLoading = false, data = result) }
            } catch (e: Exception) {
                _configObs.update { it.copy(error = e.toString()) }
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
    object LoadData : ModelConfigIntent()

    //判断时间间隔是否需要更新数据，主动拉取，
    data class UpdateData(val time: Long) : ModelConfigIntent()
}

//// MyState 继承自 UiState
//data class MyState(
//    val items: List<String> = emptyList() // 你可以根据具体需要定制数据类型
//) : BaseUiState<List<String>>(data = items)