package com.hwj.ai.models

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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