package com.hwj.ai.models


enum class GPTModel(val model: String, val maxTokens: Int, val isChatCompletion: Boolean = false) {
    DeepSeekV3("deepseek-ai/DeepSeek-V3", 4048, isChatCompletion = true),
    QwenTool("Qwen/Qwen2.5-72B-Instruct",4048,isChatCompletion = false),
    DeepSeekR1("deepseek-ai/DeepSeek-R1",4096,isChatCompletion = true),
    visionhunyuan("hunyuan-vision",4048,isChatCompletion = true),
    visionDeepv2("deepseek-ai/deepseek-vl2",4048, isChatCompletion = true),
    visionQwen("Pro/Qwen/Qwen2.5-VL-7B-Instruct",4048, isChatCompletion = true)
}

//deepseek-ai/DeepSeek-V3
//见鬼了，视觉模型中，混元多图偶现参数丢失，其他模型又没