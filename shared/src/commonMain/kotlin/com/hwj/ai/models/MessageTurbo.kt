package com.hwj.ai.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class MessageTurbo(
    val content: String = "",
    val role: TurboRole = TurboRole.user,
)

fun MessageTurbo.toJson() : JsonObject {
    val jsonObject = buildJsonObject {
        put("content", content)
        put("role", role.value)
    }
    return jsonObject
}
