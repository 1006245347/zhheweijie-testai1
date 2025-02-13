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
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
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
import kotlinx.coroutines.withContext

class LLMRepository(
    private val client: HttpClient
) {

    fun textCompletionsWithStream(params: TextCompletionsParam): Flow<String> =
        callbackFlow {
            withContext(Dispatchers.IO) {

                var response: HttpResponse? = null

                try {
                    response = client.post(baseHostUrl + urlChatCompletions) {
                        headers {
                            append(HttpHeaders.ContentType, ContentType.Application.Json)
                            append(HttpHeaders.Authorization, "Bearer $LLM_API_KEY")
                        }
                        setBody(params.toJson())
                    }
                } catch (e: HttpRequestTimeoutException) {
                    printE("接口超时")
                }
                response?.bodyAsChannel()?.let { channel ->
                    val buffer = ByteArray(DEFAULT_HTTP_BUFFER_SIZE)
                    try {
                        while (!channel.isClosedForRead) {

                            val event = channel.readUTF8Line()?.trim()
                            event?.takeIf {
                                it.startsWith("data:")
                            }?.let {
//                                val json = it.removePrefix("data:")
////                                printD(json)
//                                if (json!="[DONE]") {
//                                    trySend(json)
//                                }
                                //这个输出没有问题，而且是拆词数据
                                val value = lookupDataFromResponseTurbo(it)
                                if (value.isNotEmpty()) {
                                    printD(value)
                                    trySend(value)
                                }
                            }
                        }

                    } finally {
                        ByteArrayPool.recycle(buffer)
                        close()
                    }
                }
            }


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