package com.hwj.ai.data.repository

import com.hwj.ai.data.local.getMsgList
import com.hwj.ai.data.local.saveMessage
import com.hwj.ai.models.MessageModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * @author by jason-何伟杰，2025/2/11
 * des:一轮对话中的消息队列
 */
class MessageRepository() {

    fun fetchMessages(conversationId: String): Flow<List<MessageModel>> =

        callbackFlow {
            val list = getMsgList(conversationId)
            if (list.isNullOrEmpty()) {
                trySend(listOf())
            } else {
                trySend(list)
            }
            awaitClose { close() }
        }

    suspend fun createMessage(message: MessageModel): MessageModel {
        //add ... 向集合添加
        saveMessage(message)
        return message
    }

    fun deleteMessage(message: MessageModel) {
        //delete ...
    }

}