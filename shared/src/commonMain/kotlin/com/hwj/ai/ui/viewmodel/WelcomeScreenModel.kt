package com.hwj.ai.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.hwj.ai.data.repository.LocalDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel

class WelcomeScreenModel(private val localDataRepository: LocalDataRepository) : ViewModel() {

    //类注入对象
//    private val localDataRepository: LocalDataRepository by inject()

    private val coroutineScope: CoroutineScope = MainScope()

    var uiState: AppUiState by mutableStateOf(AppUiState.Loading)
        private set

    init {
        coroutineScope.launch {
            val isWelcomeShown = localDataRepository.welcomeShown()
            uiState = AppUiState.Success(isWelcomeShown = isWelcomeShown)
        }
    }

    fun setFirstWelcome() {
        coroutineScope.launch {
            localDataRepository.setWelcomeShown()
        }
    }
}

sealed interface AppUiState {
    data object Loading : AppUiState
    data class Success(val isWelcomeShown: Boolean) : AppUiState
}