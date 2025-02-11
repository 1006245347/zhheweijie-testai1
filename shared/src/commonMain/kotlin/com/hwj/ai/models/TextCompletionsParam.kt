package com.hwj.ai.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.addAll
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject


data class TextCompletionsParam(
    @SerialName("prompt")
    val promptText: String = "",
    @SerialName("temperature")
    val temperature: Double = 0.9,
    @SerialName("top_p")
    val topP: Double = 1.0,
    @SerialName("n")
    val n: Int = 1,
    @SerialName("stream")
    var stream: Boolean = true,
    @SerialName("maxTokens")
    val maxTokens: Int = 2048,
    @SerialName("model")
    val model: GPTModel = GPTModel.gpt35Turbo,
    @SerialName("messages")
    val messagesTurbo: List<MessageTurbo> = emptyList(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as TextCompletionsParam

        if (promptText != other.promptText) return false
        if (temperature != other.temperature) return false
        if (topP != other.topP) return false
        if (n != other.n) return false
        if (stream != other.stream) return false
        if (maxTokens != other.maxTokens) return false
        if (model != other.model) return false
        if (messagesTurbo != other.messagesTurbo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = promptText.hashCode()
        result = 31 * result + temperature.hashCode()
        result = 31 * result + topP.hashCode()
        result = 31 * result + n
        result = 31 * result + stream.hashCode()
        result = 31 * result + maxTokens
        result = 31 * result + model.hashCode()
        result = 31 * result + messagesTurbo.hashCode()
        return result
    }

    val isChatCompletions: Boolean
        get() = model.isChatCompletion
}

fun TextCompletionsParam.toJson(): JsonObject {
//    val json = JsonObject()
//    json.addProperty("temperature", temperature)
//    json.addProperty("stream", stream)
//    json.addProperty("model", model.model)

    val jsonObject = buildJsonObject {
        put("temperature", temperature)
        put("stream", stream)
        put("model", model.model)
        if (isChatCompletions) {
            val jsonArray = buildJsonArray {
                for (message in messagesTurbo) {
                    add(message.toJson())
                }
            }
            put("messages", jsonArray)
        } else {
            put("prompt", promptText)
        }
    }
//    if (isChatCompletions) {
//        val jsonArray = buildJsonArray {
//            for (message in messagesTurbo) {
//                add(message.toJson())
//            }
//        }
//
//        json.add("messages", jsonArray)
//    } else {
//        json.addProperty("prompt", promptText)
//    }

    return jsonObject
}