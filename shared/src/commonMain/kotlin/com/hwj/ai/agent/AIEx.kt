package com.hwj.ai.agent

import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.rag.vector.InMemoryDocumentEmbeddingStorage
import ai.koog.rag.vector.InMemoryTextDocumentEmbeddingStorage
import ai.koog.rag.vector.InMemoryVectorStorage

/**
 * @author by jason-何伟杰，2025/9/1
 * shared
 * des:快速集成所有智能体的工具库对象
 */

//专业向量化服务对象
fun createLLMEmbedder(apiKey: String, llModel: LLModel = createEmbedLLM()): LLMEmbedder {
    val client = OpenAiRemoteLLMClient(apiKey = apiKey)
    val embedder = LLMEmbedder(client, llModel)
    return embedder
}

//专用向量化模型
fun createEmbedLLM(modelId: String = "bge-gree"): LLModel {
    return LLModel(
        LLMProvider.OpenAI, modelId, capabilities = listOf(LLMCapability.Embed),
        contextLength = 128_000, maxOutputTokens = 16_384
    )
}

//只有内存向量存储是所有平台都支持，iOS不支持Jvm类型的storage
fun createMemoryStorage(embedder: LLMEmbedder ) {
//    InMemoryTextDocumentEmbeddingStorage(embedder)
//    embedder.embed("")
//    InMemoryVectorStorage()
}



