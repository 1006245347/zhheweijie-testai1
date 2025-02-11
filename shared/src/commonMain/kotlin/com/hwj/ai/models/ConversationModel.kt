package com.hwj.ai.models

import com.hwj.ai.global.getMills
import com.hwj.ai.global.getNowTime
import kotlinx.datetime.LocalDateTime


data class ConversationModel(
    var id: String = getMills().toString(),
    var title: String = "",
    var createdAt: LocalDateTime= getNowTime(),
)