package com.hwj.ai.global

import com.hwj.ai.Platform
import com.hwj.ai.getPlatform

class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}

