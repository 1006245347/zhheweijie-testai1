package com.hwj.ai.models

import com.aallam.openai.api.chat.ChatDelta
import com.aallam.openai.api.chat.ContentFilterOffsets
import com.aallam.openai.api.chat.ContentFilterResults
import com.aallam.openai.api.core.FinishReason
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ChatChunkReason (
    @SerialName("index") public val index: Int,
    /**
     * The generated chat message.
     */
    @SerialName("delta") public val delta: ChatDeltaReason? = null,

    /**
     * Azure content filter offsets
     */
    @SerialName("content_filter_offsets") public val contentFilterOffsets: ContentFilterOffsets? = null,

    /**
     * Azure content filter results
     */
    @SerialName("content_filter_results") public val contentFilterResults: ContentFilterResults? = null,

    /**
     * The reason why OpenAI stopped generating.
     */
    @SerialName("finish_reason") public val finishReason: FinishReason? = null,

    )