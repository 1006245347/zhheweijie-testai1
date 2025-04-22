package com.hwj.ai.models

import com.aallam.openai.api.chat.ChatChunk
import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ChatCompletionChunkReason (

    /**
     * A unique id assigned to this completion
     */
    @SerialName("id")
    public val id: String,

    /**
     * The creation time in epoch milliseconds.
     */
    @SerialName("created")
    public val created: Long,

    /**
     * The model used.
     */
    @SerialName("model")
    public val model: ModelId,

    /**
     * A list of generated completions
     */
    @SerialName("choices")
    public val choices: List<ChatChunkReason>,

    /**
     * Text completion usage data.
     */
    @SerialName("usage")
    public val usage: Usage? = null,

    /**
     * This fingerprint represents the backend configuration that the model runs with. Can be used in conjunction with
     * the `seed` request parameter to understand when backend changes have been made that might impact determinism.
     */
    @SerialName("system_fingerprint")
    public val systemFingerprint: String? = null,
)