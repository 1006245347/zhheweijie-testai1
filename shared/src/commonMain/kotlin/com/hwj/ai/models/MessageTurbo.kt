package com.hwj.ai.models

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.hwj.ai.data.http.JsonApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

@Deprecated("弃用")
@Serializable
data class MessageTurbo(
    val content: String = "",
    val role: TurboRole = TurboRole.user,
)


fun MessageTurbo.toJson(): JsonObject {
    val jsonObject = buildJsonObject {
        put("content", content)
        put("role", role.value)
    }
    return jsonObject
}

//消息队列重新构建
fun toAIList(list: List<MessageTurbo>): List<ChatMessage> {
    val chatList = mutableListOf<ChatMessage>()
    list.forEach { item ->
        if (item.role == TurboRole.user) {
            chatList.add(ChatMessage(role = ChatRole.User, content = item.content))
        } else {
            chatList.add(ChatMessage(role = ChatRole.System, content = item.content))
        }
    }
    return chatList
}

fun ChatMessage.toJson():JsonElement{
   return JsonApi.encodeToJsonElement(this)
}