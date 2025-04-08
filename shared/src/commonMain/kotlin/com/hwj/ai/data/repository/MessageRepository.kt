package com.hwj.ai.data.repository

import com.hwj.ai.data.http.JsonApi
import com.hwj.ai.global.DATA_MESSAGE_TAG
import com.hwj.ai.global.DATA_USER_ID
import com.hwj.ai.global.getCacheLong
import com.hwj.ai.global.getCacheString
import com.hwj.ai.global.saveString
import com.hwj.ai.models.MessageModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * @author by jason-何伟杰，2025/2/11
 * des:一轮对话中的消息队列
 */
class MessageRepository() {

    //    private  lateinit var  shot:
    fun fetchMessages(conversationId: String): Flow<List<MessageModel>> =

    //test
//        flowOf(
////            fakeMessages
//        )
        //real
        callbackFlow {

        }

    suspend fun createMessage(message: MessageModel): MessageModel {
        //add ... 向集合添加
        saveMessage(message)
        return message
    }

    fun deleteMessage(message: MessageModel) {
        //delete ...
    }

    private suspend fun getMsgList(conversationId: String): MutableList<MessageModel>? {
        //缓存要跟userID绑定，不然切账号就乱了
        val result = getCacheString(buildTag(conversationId))
        if (!result.isNullOrEmpty()) {
            val list = JsonApi.decodeFromString<MutableList<MessageModel>>(result)
            return list
        } else {
            return null
        }
    }

    private suspend fun saveMessage(message: MessageModel) {
        val cacheList = getMsgList(message.conversationId)
        if (cacheList.isNullOrEmpty()) {
            val newList = mutableListOf<MessageModel>()
            newList.add(message)
            saveString(buildTag(message.conversationId), JsonApi.encodeToString(newList))
        } else {
            cacheList.add(message)
            saveString(buildTag(message.conversationId), JsonApi.encodeToString(cacheList))
        }
    }

    private suspend fun buildTag(conversationId: String): String {
        return DATA_MESSAGE_TAG + getCacheLong(DATA_USER_ID) + "_$conversationId"
    }
}