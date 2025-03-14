package com.hwj.ai.models

import com.hwj.ai.global.LLM_MODEL

enum class GPTModel(val model: String, val maxTokens: Int, val isChatCompletion: Boolean = false) {
    //    gpt35Turbo("gpt-3.5-turbo", 4096),
    gpt35Turbo(LLM_MODEL, 2048, isChatCompletion = true),
//    gpt35Turbo(LLM_MODEL, 2048, isChatCompletion = true)
    hunyuan("hunyuan-vision",4048,isChatCompletion = true),
}