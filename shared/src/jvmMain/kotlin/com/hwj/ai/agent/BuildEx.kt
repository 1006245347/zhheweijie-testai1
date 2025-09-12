package com.hwj.ai.agent

import ai.koog.rag.base.files.JVMDocumentProvider
import ai.koog.rag.vector.EmbeddingBasedDocumentStorage
import ai.koog.rag.vector.InMemoryVectorStorage
import ai.koog.rag.vector.JVMTextDocumentEmbedder

class BuildEx {
}

fun a(){
    val e1 = createLLMEmbedder("")
    val dp= JVMTextDocumentEmbedder(e1)
    val c=EmbeddingBasedDocumentStorage(dp, InMemoryVectorStorage())


}

