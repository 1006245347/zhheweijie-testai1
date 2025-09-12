package com.hwj.ai.agent

import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.rag.vector.InMemoryDocumentEmbeddingStorage
import ai.koog.rag.vector.InMemoryTextDocumentEmbeddingStorage
import ai.koog.rag.vector.TextDocumentEmbedder
import com.hwj.ai.global.getCacheString

/**
 * @author by jason-何伟杰，2025/9/1
 * shared/
 * des:快速集成所有智能体的工具库对象
 */

//专业向量化服务对象   keyCall: suspend (String) -> String?
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

//要是你现在在做 多平台的 RAG 知识库，推荐用 InMemoryDocumentEmbeddingStorage，因为后续检索时你会
// 需要 metadata（文件名、来源页码等），否则你只能拿到“某段文本”，没法溯源。
//InMemoryTextDocumentEmbeddingStorage 👉 玩具级 / 只管存 String
//InMemoryDocumentEmbeddingStorage 👉 生产级 / 存 Document + Metadata
fun cc() {
    val embed = createLLMEmbedder("")
    val documentProvider = TextDocumentEmbedder(AIDocumentProvider, embed)
//    val storage = InMemoryDocumentEmbeddingStorage(documentProvider)
//    val storage= InMemoryTextDocumentEmbeddingStorage(embed, AIDocumentProvider)
}


//只有内存向量存储是所有平台都支持，iOS不支持Jvm类型的storage
fun createMemoryStorage(embedder: LLMEmbedder) {
//    InMemoryTextDocumentEmbeddingStorage(embedder)
//    embedder.embed("")
//    InMemoryVectorStorage()
}





