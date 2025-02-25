package com.hwj.ai.data.repository

import com.hwj.ai.models.ConversationModel

/**
 * @author by jason-何伟杰，2025/2/11
 * des:对话增删保存本地
 */
class ConversationRepository() {

    fun fetchConversations(): MutableList<ConversationModel> {
//        return fakeConversations.toMutableList()
    return mutableListOf()
    }

    fun newConversation(conversationModel: ConversationModel): ConversationModel {
        //add
        return conversationModel
    }

    fun deleteConversation(conversationId: String) {}

    fun getFirstConversation(): ConversationModel? {

//        return fakeConversations[0]
        return null
    }
}