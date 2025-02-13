package com.hwj.ai.models

enum class GPTModel(val model: String, val maxTokens: Int, val isChatCompletion: Boolean = false) {
    //    gpt35Turbo("gpt-3.5-turbo", 4096),
    gpt35Turbo("deepseek-chat", 2048, isChatCompletion = true)
}