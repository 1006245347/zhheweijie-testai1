package com.hwj.ai.models

import kotlinx.serialization.SerialName


enum class TurboRole(val value: String) {
    @SerialName("system")
    system("system"),
    @SerialName("assistant")
    assistant("assistant"),
    @SerialName("user")
    user("user")
}