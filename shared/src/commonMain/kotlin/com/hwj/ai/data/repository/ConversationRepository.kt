package com.hwj.ai.data.repository

import com.hwj.ai.data.local.deleteMsgByConversationID
import com.hwj.ai.data.local.getConversationList
import com.hwj.ai.data.local.saveConversation
import com.hwj.ai.data.local.saveConversationList
import com.hwj.ai.global.Event
import com.hwj.ai.global.EventHelper
import com.hwj.ai.models.ConversationModel

/**
 * @author by jason-何伟杰，2025/2/11
 * des:对话增删保存本地
 */
class ConversationRepository {

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
                    //要同时删除这个id下的所有消息记录
                    deleteMsgByConversationID(conversationId)
                    iterator.remove()
                    break
                }
            }
        }
        saveConversationList(list)
        //通知外部刷新
        EventHelper.post(Event.DeleteConversationEvent(conversationId))
    }
}