package com.hwj.ai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform