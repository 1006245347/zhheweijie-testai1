package com.hwj.ai.agent

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.features.eventHandler.feature.handleEvents
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.executor.llms.all.simpleOpenAIExecutor
import com.hwj.ai.global.DATA_APP_TOKEN
import com.hwj.ai.global.getCacheString
import com.hwj.ai.global.printD
import com.hwj.ai.global.printLog


suspend fun quickAgent(input: String) {

    val apiKey = getCacheString(DATA_APP_TOKEN)
    if (apiKey == null) return
//    val remoteAiExecutor = SingleLLMPromptExecutor(OpenAiRemoteLLMClient(apiKey))
    val remoteAiExecutor=createAiClient(apiKey)
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
        handleEvents {
            onAgentRunError { ctx ->
                printLog("err??${ctx.throwable.message}")
            }
        }
    }

    //它默认是非流式
    agent.run(input).also {
        printD("agent>$it")
    }
}

