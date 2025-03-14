package com.hwj.ai.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

//这是标准的模型实体，有很多参数都无用
@Serializable
data class LLMModel(
    val prompts: LLMPrompts? = null,
    var url: String? = null,
    var hostUrl:String?=null,
    var sk:String?=null,
    val body: LLMBody? = null,
    var model: String? = null,
    val name: String? = null,
    val taskType: String? = null,
    val textGenerationType:String? = null
)

fun LLMModel.toJson(): JsonObject {
    val jsObject = buildJsonObject {
        model?.let { put("model", it) }
        name?.let { put("name", it) }
        url?.let { put("url", it) }
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
    val stop_token_ids: String? = null,
    val stream_options: LLMStreamOptions? = null
)


@Serializable
data class LLMStreamOptions(val include_usage: Boolean?)


