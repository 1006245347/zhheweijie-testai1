package com.hwj.ai.data.repository

import com.hwj.ai.data.http.JsonApi
import com.hwj.ai.global.LLM_API_KEY
import com.hwj.ai.global.baseHostUrl
import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.hwj.ai.global.urlChatCompletions
import com.hwj.ai.models.ChatCompletionChunkReason
import com.hwj.ai.models.TextCompletionsParam
import com.hwj.ai.models.toJson
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.append
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * @author by jason-何伟杰，2025/1/18
 * des:大模型数据接口
 */
@Deprecated("流式输出")
class LLMRepository(
    private val client: HttpClient
) {

    suspend fun chatRequestStream(
        params: TextCompletionsParam,
        useWeb: Boolean = false,
    ) = channelFlow {
        withContext(Dispatchers.IO) {
            client.preparePost(baseHostUrl + urlChatCompletions) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                    append(HttpHeaders.Authorization, "Bearer $LLM_API_KEY")
                    //加入stream输出,每个包以data:开头，不然字符全拼接乱的
//                            append(HttpHeaders.Accept, ContentType.Text.EventStream)
                    append(HttpHeaders.CacheControl, "no-cache")
                    append(HttpHeaders.Connection, "keep-alive")
                    //json返回一串无序
//                    append(HttpHeaders.Accept, ContentType.Application.Json)
                }
                setBody(params.toJson())
            }.execute { _response: HttpResponse ->
                if (_response.status == HttpStatusCode.OK) {
                    val channel = _response.bodyAsChannel()

                    val flow = channelFlow {
                        channel.onEachLine {
                            val line = it.removePrefix("data:") //把空行都干掉了
                            if (line.contains("[DONE]")) {
                                printD("answer Done!")
                                close()
                            } else {
                                send(JsonApi.decodeFromString<ChatCompletionChunkReason>(line))
                            }
                        }
                    }

                    flow.collect { trySend(it) }
//                    val flow = JsonApi.decodeToFlow<ChatCompletionChunkReason>(channel)
//                    flow.collect { trySend(it) }
                } else {
                    printE(_response.bodyAsText())
                }
            }
        }
    }


}

internal suspend fun ByteReadChannel.onEachLine(block: suspend (String) -> Unit) {
    while (!isClosedForRead) {
        awaitContent()
        val line = readUTF8Line()?.takeUnless { it.isEmpty() } ?: continue
        block(line)
    }
}


internal inline fun <reified T> Json.decodeToFlow(channel: ByteReadChannel): Flow<T> = channelFlow {
    channel.onEachLine {
        val data = it.removePrefix("data:")
        send(decodeFromString(data))
    }
}

/**
 * Writes the provided [bytes] to the channel and closes it.
 *
 * Just a wrapper around [writeFully] that closes the channel after writing is complete.
 *
 * @param bytes the data to send through the channel
 */
internal suspend fun ByteChannel.send(bytes: ByteArray) {
    writeFully(bytes)
    close()
}

/** String separator used in SSE communication to signal the end of a message. */
internal const val SSE_SEPARATOR = "\r\n\r\n"