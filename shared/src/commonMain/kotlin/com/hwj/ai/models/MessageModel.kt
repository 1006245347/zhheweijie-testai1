package com.hwj.ai.models

import com.hwj.ai.global.getMills
import com.hwj.ai.global.getNowTime
import kotlinx.datetime.LocalDateTime


data class MessageModel(
    var id: String = getMills().toString(),
    var conversationId: String = "",
    var question: String = "",
    var answer: String = "",
//    var createdAt: LocalDateTime = getNowTime(),
    var createdAt: Any?=null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MessageModel

        if (id != other.id) return false
        if (conversationId != other.conversationId) return false
        if (question != other.question) return false
        if (answer != other.answer) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + conversationId.hashCode()
        result = 31 * result + question.hashCode()
        result = 31 * result + answer.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }

    fun clone():MessageModel{
        return MessageModel(this.id,this.conversationId,
            this.question,this.answer,this.createdAt)
    }
}