package com.hwj.ai.data.http

import com.hwj.ai.global.DATA_APP_TOKEN
import com.hwj.ai.global.getCacheString
import com.hwj.ai.global.printE
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.IOException
import kotlinx.serialization.json.jsonObject

/**Json解析器参数影响普通解析结果*/
inline fun <reified T> Json.parseJson(json: String): T? {
    val t = JsonApi.decodeFromString<T>(json)
    return t
}

inline fun <reified T> stateResponse(json: String): HttpData<T>? {
    val jsonObject = Json.parseToJsonElement(json).jsonObject
    val code = jsonObject["code"].toString().toInt()
    val msg = jsonObject["msg"].toString()
    val result = if (code == 1000) {
        Json.parseJson<HttpData<T>>(json)
    } else {
        HttpData<T>(code, null, msg)
    }
    return result
}

val JsonApi = Json {
    isLenient = true
    ignoreUnknownKeys = true
//    encodeDefaults = true
//        prettyPrint = true
//        coerceInputValues = true
}

@Serializable
class HttpData<T> {
    var code: Int = 0
    var data: T? = null
    var msg: String? = null

    constructor()

    constructor(code: Int, data: T?, msg: String?) {
        this.code = code
        this.data = data
        this.msg = msg
    }

    override fun toString(): String {
        return "{$code,$data,$msg}"
    }
}

data class ErrorResponse(
    val success: Boolean, val statusCode: Int, val statusMessage: String
) : Exception()

@Serializable
data class ErrorResponseDto(
    @SerialName("success") val success: Boolean,

    @SerialName("status_code") val statusCode: Int,

    @SerialName("status_message") val statusMessage: String
)

fun ErrorResponseDto.toDomain(): ErrorResponse {
    return ErrorResponse(
        success = this.success, statusCode = this.statusCode, statusMessage = this.statusMessage
    )
}

suspend fun HttpClient.getWithCookie(
    url: String, map: Map<String, String>? = null
): String {
    val json = get(url) {
        getCacheString(DATA_APP_TOKEN)?.let {
            header("Cookie", it)
        }
        map?.forEach {
            parameter(it.key, it.value)
        }
    }.body<String>()
    return json
}

suspend fun HttpClient.postWithCookie(url: String, map: Map<String, String>): String {
    return post(url) {
        getCacheString(DATA_APP_TOKEN)?.let {
            header("Cookie", it)
        }
        map.forEach {
            parameter(it.key, it.value)
        }
    }.body<String>()
}

suspend fun HttpClient.postJsonWithCookie(url: String, jsonBean: Any): String {
//    return post(url) {
//        getCacheStr(KmmConfig.DATA_SINGLE_COOKIE)?.let {
//            header("Cookie", it)
//        }
//        contentType(ContentType.Application.Json)
//        setBody(jsonBean)
//    }.body<String>()
    return try { //有时候timeOut导致崩溃
        post(url) {
            getCacheString(DATA_APP_TOKEN)?.let {
                header("Cookie", it)
            }
            contentType(ContentType.Application.Json)
            setBody(jsonBean)
        }.body<String>()
    } catch (e: Exception) { //未测试
        printE("errorPost>${handleException(e)}")
        ""
    }
}

fun handleException(exception: Exception): HttpStatusCode {
    return when (exception) {
        is HttpRequestTimeoutException -> HttpStatusCode.GatewayTimeout
        is IOException -> HttpStatusCode.InternalServerError
        is ClientRequestException -> {
            val exceptionResponse = exception.response
            if (exceptionResponse.status == HttpStatusCode.Unauthorized) {
                HttpStatusCode.Unauthorized
            } else {
                HttpStatusCode.BadRequest
            }
        }

        else -> HttpStatusCode.InternalServerError
    }
}

fun globalParams(): HashMap<String, String> {
    return hashMapOf()
}

suspend fun <T : Any?> safeApiCall(apiCall: suspend () -> T): ResultState<T> {
    return try {
        ResultState.Loading

        ResultState.Success(apiCall.invoke())
    } catch (e: RedirectResponseException) {
        val error = parseNetworkError(e.response.body())
        ResultState.Failure(exception = error)
    } catch (e: ClientRequestException) {
        val error = parseNetworkError(e.response.body())
        ResultState.Failure(exception = error)
    } catch (e: ServerResponseException) {
        val error = parseNetworkError(e.response.body())
        ResultState.Failure(exception = error)
    } catch (e: UnresolvedAddressException) {
        val error = parseNetworkError(exception = e)
        ResultState.Failure(exception = error)
    } catch (e: Exception) {
        val error = parseNetworkError(exception = e)
        ResultState.Failure(exception = error).apply {
//            printE("$this")
        }
    }
}

/**Generate [Exception] from network or system error when making network calls
 *
 * @throws [Exception]
 * */
internal suspend fun parseNetworkError(
    errorResponse: HttpResponse? = null, exception: Exception? = null
): Exception {
//    throw errorResponse?.body<ErrorResponseDto>()?.toDomain() ?: ErrorResponse(
    return errorResponse?.body<ErrorResponseDto>()?.toDomain() ?: ErrorResponse(
        success = false, statusCode = 999, statusMessage = exception?.message ?: "Error"
    )
}
