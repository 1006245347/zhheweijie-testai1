package com.hwj.ai.data.repository

import com.hwj.ai.data.http.JsonApi
import com.hwj.ai.data.local.getConversationList
import com.hwj.ai.data.local.saveConversation
import com.hwj.ai.data.local.saveConversationList
import com.hwj.ai.global.DATA_CONVERSATION_TAG
import com.hwj.ai.global.DATA_USER_ID
import com.hwj.ai.global.getCacheLong
import com.hwj.ai.global.getCacheString
import com.hwj.ai.global.removeKey
import com.hwj.ai.global.saveString
import com.hwj.ai.models.ConversationModel

/**
 * @author by jason-何伟杰，2025/2/11
 * des:对话增删保存本地
 */
class ConversationRepository() {

    suspend fun fetchConversations(): MutableList<ConversationModel> {
        val list = getConversationList()
        return if (list.isNullOrEmpty()) {
            mutableListOf()
        } else {
            list.reversed().toMutableList()
        }
    }

    suspend fun newConversation(conversationModel: ConversationModel): ConversationModel {
        //add
        saveConversation(conversationModel)
        return conversationModel
    }

    suspend fun deleteConversation(conversationId: String) {
        val list = getConversationList()
        if (!list.isNullOrEmpty()) {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val tmp = iterator.next()
                if (tmp.id == conversationId) {
                    iterator.remove()
                    break
                }
            }
        }
        saveConversationList(list)
    }

    fun getFirstConversation(): ConversationModel? {

//        return fakeConversations[0]
        return null
    }


}