package com.hwj.ai.data.local

import com.hwj.ai.data.http.JsonApi
import com.hwj.ai.global.DATA_CONVERSATION_TAG
import com.hwj.ai.global.DATA_MESSAGE_TAG
import com.hwj.ai.global.DATA_USER_ID
import com.hwj.ai.global.getCacheLong
import com.hwj.ai.global.getCacheString
import com.hwj.ai.global.removeKey
import com.hwj.ai.global.saveString
import com.hwj.ai.models.ConversationModel
import com.hwj.ai.models.MessageModel


suspend fun getConversationList(): MutableList<ConversationModel>? {
    val result = getCacheString(buildConversationTag())
    if (!result.isNullOrEmpty()) {
        val list = JsonApi.decodeFromString<MutableList<ConversationModel>>(result)
        return list
    } else {
        return null
    }
}

 suspend fun saveConversation(conversation: ConversationModel) {
    val cacheList = getConversationList()
    if (cacheList.isNullOrEmpty()) {
        val newList = mutableListOf<ConversationModel>()
        newList.add(conversation)
        saveString(buildConversationTag(), JsonApi.encodeToString(newList))
    } else {
        cacheList.add(conversation)
        if (cacheList.size > 20) { //本地存储大小限制下
            cacheList.removeAt(0)
        }
        saveString(buildConversationTag(), JsonApi.encodeToString(cacheList))
    }
}

 suspend fun saveConversationList(list: MutableList<ConversationModel>?) {
    if (list.isNullOrEmpty()) {
        removeKey(buildConversationTag())
    } else {
        saveString(buildConversationTag(), JsonApi.encodeToString(list))
    }
}


private suspend fun buildConversationTag(): String {
    return DATA_CONVERSATION_TAG + getCacheLong(DATA_USER_ID)
}

suspend fun deleteMsgByConversationID(conversationId: String){
    removeKey(buildMsgTag(conversationId))
}

 suspend fun getMsgList(conversationId: String): MutableList<MessageModel>? {
    //缓存要跟userID绑定，不然切账号就乱了
    val result = getCacheString(buildMsgTag(conversationId))
    if (!result.isNullOrEmpty()) {
        val list = JsonApi.decodeFromString<MutableList<MessageModel>>(result)
        return list
    } else {
        return null
    }
}

 suspend fun saveMessage(message: MessageModel) {
    val cacheList = getMsgList(message.conversationId)
    if (cacheList.isNullOrEmpty()) {
        val newList = mutableListOf<MessageModel>()
        newList.add(message)
        saveString(buildMsgTag(message.conversationId), JsonApi.encodeToString(newList))
    } else {
        cacheList.add(message)
        saveString(buildMsgTag(message.conversationId), JsonApi.encodeToString(cacheList))
    }
}

 suspend fun buildMsgTag(conversationId: String): String {
    return DATA_MESSAGE_TAG + getCacheLong(DATA_USER_ID) + "_$conversationId"
}