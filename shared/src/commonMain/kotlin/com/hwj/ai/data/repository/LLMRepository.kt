package com.hwj.ai.data.repository

import com.hwj.ai.global.baseHostUrl
import com.hwj.ai.global.urlChatCompletions
import com.hwj.ai.models.TextCompletionsParam
import com.hwj.ai.models.toJson
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.utils.io.cancel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class LLMRepository(
    private val client: HttpClient,
//    private val apiUrl: String
) {

    fun textCompletionsWithStream(params: TextCompletionsParam): Flow<String> =
        callbackFlow {
            withContext(Dispatchers.IO) {
                try {

                    val response: HttpResponse = client.post(baseHostUrl+ urlChatCompletions) {
                        headers {
                            append(HttpHeaders.ContentType, ContentType.Application.Json)
                        }
                        setBody(params.toJson())
                    }
                    val channel = response.bodyAsChannel()
                    try {
                        val sb = StringBuilder()
                        channel.readRemaining().let { remaining ->
                            while (!remaining.exhausted()) {
                                val byte = remaining.readByte()
                                if (byte == "\n".toByte()) {
                                    val line = sb.toString()
                                    if (line == "data: [DONE]") {
                                        close()
                                    } else if (line.startsWith("data:")) {
                                        try {
                                            val value = if (params.isChatCompletions) {
                                                lookupDataFromResponseTurbo(line)
                                            } else {
                                                lookupDataFromResponse(line)
                                            }
                                            if (value.isNotEmpty()) {
                                                trySend(value)
                                            }
                                        } catch (e: Exception) {
                                        }
                                    }
                                    sb.clear()
                                } else {
                                    sb.append(byte.toChar())
                                }
                            }
                        }
                    } catch (e: Exception) {
                    } finally {
                        channel.cancel()//有必要？
                        close()
                    }
                } catch (_: Exception) {
                    trySend("Failure! Try again")
                    close()
                }
            }
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