package com.hwj.ai.data.repository

import com.hwj.ai.models.MessageModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * @author by jason-何伟杰，2025/2/11
 * des:一轮对话中的消息队列
 */
class MessageRepository() {
    fun fetchMessages(conversationId: String): Flow<List<MessageModel>> =

        //test
        flowOf(
//            fakeMessages
        )
        //real
//        callbackFlow {
//
//        }

    fun createMessage(message: MessageModel): MessageModel {
        //add ...
        return message
    }

    fun deleteMessage(message: MessageModel) {
        //delete ...
    }
}