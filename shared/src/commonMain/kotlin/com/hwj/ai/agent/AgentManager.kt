package com.hwj.ai.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.memory.feature.AgentMemory
import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.embeddings.local.OllamaEmbeddingModels
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import com.hwj.ai.global.printD


suspend fun QuickAgent(input: String) {

    val apiKey =
        "YWNjZXNzVG9rZW46MjA4NzA1NjI0NjMxNzU1OTgwODozNTE1LjA5M0VFMTBGNTRDQzk4MTYwM0Q2RTA2RkI0QkI2MDJF"
    val remoteAiExecutor: SingleLLMPromptExecutor =
        SingleLLMPromptExecutor(OpenAiRemoteLLMClient(apiKey))
    val agent = AIAgent(
//        executor = simpleOpenAIExecutor(apiKey),
        executor = remoteAiExecutor,
        systemPrompt = "You are a helpful assistant. Answer user questions concisely.",
        llmModel = OpenAIModels.Chat.GPT4o,
//        llmModel = OpenRouterModels.GPT4o
        temperature = 0.7,
//        toolRegistry = ToolRegistry{
//            tool(SayToUser)
//        }
    ) {
        install(AgentMemory) //记忆
    }


    val result = agent.run(input)

//    println("R>$result")


}

