package com.hwj.ai.models

import com.hwj.ai.global.LLM_MODEL

enum class GPTModel(val model: String, val maxTokens: Int, val isChatCompletion: Boolean = false) {
    //    gpt35Turbo("gpt-3.5-turbo", 4096),
    gpt35Turbo(LLM_MODEL, 4048, isChatCompletion = true),
//    gpt35Turbo(LLM_MODEL, 2048, isChatCompletion = true)
    visionhunyuan("hunyuan-vision",4048,isChatCompletion = true),
    visionDeepv2("deepseek-ai/deepseek-vl2",4048, isChatCompletion = true),
    visionQwen("Pro/Qwen/Qwen2.5-VL-7B-Instruct",4048, isChatCompletion = true)
}

//见鬼了，视觉模型中，混元多图偶现参数丢失，其他模型又没