package com.hwj.ai.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import com.hwj.ai.global.printD


suspend fun quickAgent(input: String) {

    val apiKey =
        "YWNjZXNzVG9rZW46MjA4NzA1NjI0NjMxNzU1OTgwODozNTE1LkJGQTE2MDc0QjlBQTNCRTlGOTNGRDg2NDdBRkVFODA3"
    val remoteAiExecutor = SingleLLMPromptExecutor(OpenAiRemoteLLMClient(apiKey))
    val agent = AIAgent(
        executor = remoteAiExecutor,
        systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
        llmModel = OpenAIModels.Chat.GPT4o,
        temperature = 0.7,
//        toolRegistry = ToolRegistry{
//            tool(SayToUser)
//        }
    ) {
//        install(AgentMemory) //记忆
    }

    //它默认是非流式
    agent.run(input).also {
        printD("agent>$it")
    }
}

