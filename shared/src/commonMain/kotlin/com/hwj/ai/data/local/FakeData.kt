package com.hwj.ai.data.local

import com.hwj.ai.global.getNowTime
import com.hwj.ai.models.ConversationModel
import com.hwj.ai.models.MessageModel

val fakeConversations: List<ConversationModel> = listOf(
    ConversationModel(
        id = "1",
        title = "What's Flutter?",
        createdAt = getNowTime(),
    ),
    ConversationModel(
        id = "2",
        title = "What's Compose?",
        createdAt = getNowTime(),
    ),
    ConversationModel(
        id = "3",
        title = "What's ChatGPT?",
        createdAt = getNowTime(),
    ),
)

val fakeMessages: List<MessageModel> = listOf(
    MessageModel(
        question = "Who is kakaka?",
        answer = "I'm Kai (lambiengcode), currently working as the Technical Leader at Askany and Waodate. Computador Also, I'm a freelancer. If you have a need for a mobile application or website",
        createdAt = 1
    ),
    MessageModel(
        question = "Who is bibibi?",
        answer = "I'm Kai (lambiengcode), currently working as the Technical Leader at Askany and Waodate. Computador Also, I'm a freelancer. If you have a need for a mobile application or website ",
        createdAt =1// getNowTime()
    ))