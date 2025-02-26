package com.hwj.ai.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

//这是标准的模型实体，有很多参数都无用

@Serializable
data class LLMModel(
    val model: String? = null,
    val name: String? = null,
    val url: String? = null,
    val switchable: Boolean? = null,
    val prompts: LLMPrompts? = null,

    val body: LLMBody? = null,
)

fun LLMModel.toJson(): JsonObject {
    val jsObject = buildJsonObject {
        model?.let { put("model", it) }
        name?.let { put("name", it) }
        url?.let { put("url", it) }
        switchable?.let { put("switchable", it) }
    }
    return jsObject
}

@Serializable
data class LLMPrompts(
    val fileUpload: String? = null,
    val textQuestion: String? = null
)

@Serializable
data class LLMBody(
    val model: String? = null,
    val top_p: Float? = null,
    val repetition_penalty: Float? = null,
    val temperature: Float? = null,
    val max_tokens: Int? = null,
    val stop_token_ids: List<Long>? = null,
    val stream_options: LLMStreamOptions? = null
)


@Serializable
data class LLMStreamOptions(val include_usage: Boolean?)


