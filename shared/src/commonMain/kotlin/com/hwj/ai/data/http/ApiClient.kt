package com.hwj.ai.data.http

import com.hwj.ai.checkSystem
import com.hwj.ai.global.NotificationsManager
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.printD
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

//get请求
suspend inline fun <reified T> HttpClient.fetch(
    block: HttpRequestBuilder.() -> Unit
): Result<T> = try {
    val response = request(block)

    if (response.status == HttpStatusCode.OK) {
//        printLogW("header=${response.headers} ")
        response.headers.get("set-cookie")?.let {
            val arr = it.split(";")
            for (str in arr) {
                if (str.contains("SESSION")) {
//                    savestr(a.DATA_APP_TOKEN, str)
                    printD("token>$str")
                    break
                }
            }
//            printLogW("set-cookie>$it")
        }
        Result.Success(response.body())
    } else {
        Result.Error(Throwable("${response.status}: ${response.bodyAsText()}"))
    }
} catch (e: Exception) {
    Result.Error(e)
}

//post请求
suspend inline fun <reified T> HttpClient.sumit(
    block: HttpRequestBuilder.() -> Unit
): Result<T> = try {
    val response = request(block)
    if (response.status == HttpStatusCode.OK) {
        Result.Success(response.body())
    } else {
        Result.Error(Throwable("${response.status}: ${response.bodyAsText()}"))
    }
} catch (e: Exception) {
    Result.Error(e)
}

sealed interface Result<out R> {
    class Success<out R>(val value: R) : Result<R>
    data object Loading : Result<Nothing>

    class Error(val throwable: Throwable) : Result<Nothing>
}

inline fun <T, R> Result<T>.map(transform: (value: T) -> R): Result<R> =
    when (this) {
        is Result.Success -> Result.Success(transform(value))
        is Result.Error -> Result.Error(throwable)
        is Result.Loading -> Result.Loading
    }


fun ViewModel.handleAIException(toastManager: NotificationsManager, throwable: Throwable,block:()->Unit) {
    block()

    toast(toastManager, throwable.message + "", "toast")
}

fun ViewModel.toast(toastManager: NotificationsManager, title: String, des: String) {
    viewModelScope.launch(Dispatchers.Main) {
        if (checkSystem() == OsStatus.ANDROID) {
            if (des != "toast") {
                toastManager.showNotification(title, des)
            } else {
                toastManager.showNotification(title, "toast")
            }
        } else {
            toastManager.showNotification(title, des)
        }
    }
}
