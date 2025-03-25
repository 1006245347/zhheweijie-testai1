package com.hwj.ai.models

import com.hwj.ai.global.getMills
import com.hwj.ai.global.getNowTime
import com.hwj.ai.global.printList
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.datetime.LocalDateTime

/**
 * @author by jason-何伟杰，2025/3/12
 * des:一轮对话，可被拆解两条对应消息
 */
data class MessageModel(
    var id: String = getMills().toString(),
    var conversationId: String = "",
    var question: String = "",
    var answer: String = "",
    var imagePath:List<PlatformFile>?=null, //图片消息参数
//    var createdAt: LocalDateTime = getNowTime(),
    var createdAt: Any? = null
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

    fun clone(): MessageModel {
        return MessageModel(
            this.id, this.conversationId,
            this.question, this.answer,this.imagePath, this.createdAt
        )
    }

    override fun toString(): String {
//        return super.toString()
        return "MessageModel>"+"id=$id,+conversationId=$conversationId,question=$question"+
        ",answer=$answer,imgPath=${imagePath}"
    }
}