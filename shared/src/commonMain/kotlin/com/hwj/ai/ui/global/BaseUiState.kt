package com.hwj.ai.ui.global

open class BaseUiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String? = null
)

open class BaseUiState2<T> {
    var isLoading: Boolean = false
    var data: T? = null
    var error: String? = null

    constructor(p1: Boolean, p2: T, p3: String?) {
        isLoading = p1
        data = p2
        error = p3

    }

//     constructor(d:T?){
//         data=d
//         isLoading=false
//         error=null
//     }

    constructor(data: T?) {}
}

//全局用户意图
sealed class GlobalIntent {
    data object CheckDarkTheme : GlobalIntent()
}

data class AISelectState(
    val data: String? = null
)

sealed class AISelectIntent {
    data object SearchData : AISelectIntent()
    data object SummaryData : AISelectIntent()
    data object CopyData : AISelectIntent()

}