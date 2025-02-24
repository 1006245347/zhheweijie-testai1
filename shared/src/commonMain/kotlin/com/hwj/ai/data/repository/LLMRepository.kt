package com.hwj.ai.data.repository

import com.hwj.ai.global.LLM_API_KEY
import com.hwj.ai.global.baseHostUrl
import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.hwj.ai.global.urlChatCompletions
import com.hwj.ai.models.TextCompletionsParam
import com.hwj.ai.models.toJson
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.sse.SSESession
import io.ktor.client.plugins.sse.sseSession
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.content
import io.ktor.client.utils.DEFAULT_HTTP_BUFFER_SIZE
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.utils.io.pool.ByteArrayPool
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

/**
 * @author by jason-何伟杰，2025/1/18
 * des:大模型数据接口
 */
@Deprecated("无法流式输出")
class LLMRepository(
    private val client: HttpClient
) {
    fun textCompletionsWithStream(params: TextCompletionsParam): Flow<String> =
//        callbackFlow {
//            withContext(Dispatchers.IO) {
//                var response: HttpResponse? = null
//                response = client.post (baseHostUrl+ urlChatCompletions){
//                    headers {
//                            append(HttpHeaders.ContentType, ContentType.Application.Json)
//                            append(HttpHeaders.Authorization, "Bearer $LLM_API_KEY")
//                            //加入stream输出,每个包以data:开头，不然字符全拼接乱的
//                            append(HttpHeaders.Accept, ContentType.Text.EventStream)
//                            //json返回一串无序
////                            append(HttpHeaders.Accept, ContentType.Application.Json)
//                        }
//                    setBody(params.toJson())
//                }
//
//
//                response.bodyAsText().lineSequence().forEach { line->
//                    if (line.isNotBlank()) {
//                        printD("line>$line")
//                        line.takeIf { it.startsWith("data:") }?.let {
//                                val value = lookupDataFromResponseTurbo(it)
//                                if (value.isNotEmpty()) {
//                                    trySend(value)
//                                }
//                            }
//                    }
//                }
//            }
//
//            close()
//        }


        callbackFlow {
            withContext(Dispatchers.IO) {
                var response: HttpResponse? = null
//https://github.com/ktorio/ktor-documentation/blob/3.1.0/codeSnippets/snippets/client-sse/src/main/kotlin/com.example/Application.kt
//直接sse是get请求，这不对呀
                try {
                    printD("post-start>")
                    response = client.preparePost(baseHostUrl + urlChatCompletions) {
                        headers {
                            append(HttpHeaders.ContentType, ContentType.Application.Json)
                            append(HttpHeaders.Authorization, "Bearer $LLM_API_KEY")
                            //加入stream输出,每个包以data:开头，不然字符全拼接乱的
                            append(HttpHeaders.Accept, ContentType.Text.EventStream)
                            append(HttpHeaders.CacheControl, "no-cache")
                            append(HttpHeaders.Connection, "keep-alive")
                            //json返回一串无序
//                            append(HttpHeaders.Accept, ContentType.Application.Json)
                        }

                        setBody(params.toJson())
                    }.execute()
                } catch (e: HttpRequestTimeoutException) {
                    printE("接口超时")
                }

                val buffer = ByteArray(DEFAULT_HTTP_BUFFER_SIZE)
                val channel = response?.bodyAsChannel()
                try {
                    channel?.let {
                        printD("channel-start>")
                        while (!channel.isClosedForRead) {
                            val line = withContext(Dispatchers.IO) {
                                channel.readUTF8Line()
                            } ?: continue
                            line.takeIf { it.startsWith("data:") }?.let {
                                printD("line>$it")
                                if (it.contains("data: [DONE]")) {
                                    printD("channel-end>")
                                    close()
                                }
                                val value = lookupDataFromResponseTurbo(it)
                                if (value.isNotEmpty()) {
                                    trySend(value).isSuccess
//                                    emit(value)
                                }
                            }
                        }
                    } ?: run {
                        printD("channel-null")
                        close()
                    }
                } catch (e: Exception) {
                    printE(e)
                    trySend("Failure! Try again.")
//                    emit("Failure")
                } finally {
                    ByteArrayPool.recycle(buffer)
//                    close()
                }
            }

            printD("callback-end")
            close()
        }

    private fun lookupDataFromResponse(jsonString: String): String {
        val regex = """"text"\s*:\s*"([^"]+)"""".toRegex()
        val matchResult = regex.find(jsonString)

        if (matchResult != null && matchResult.groupValues.size > 1) {
            val extractedText = matchResult.groupValues[1]
            return extractedText
                .replace("\\n\\n", " ")
                .replace("\\n", " ")
        }

        return " "
    }

    private fun lookupDataFromResponseTurbo(jsonString: String): String {
        val regex = """"content"\s*:\s*"([^"]+)"""".toRegex()
        val matchResult = regex.find(jsonString)

        if (matchResult != null && matchResult.groupValues.size > 1) {
            val extractedText = matchResult.groupValues[1]
            return extractedText
                .replace("\\n\\n", " ")
                .replace("\\n", " ")
        }

        return " "
    }

}