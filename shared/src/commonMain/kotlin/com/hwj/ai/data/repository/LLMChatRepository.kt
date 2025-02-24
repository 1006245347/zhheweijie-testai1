package com.hwj.ai.data.repository

import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.hwj.ai.models.GPTModel
import com.hwj.ai.models.TextCompletionsParam
import com.hwj.ai.models.toAIList
import kotlinx.coroutines.flow.Flow

/**
 * @author by jason-何伟杰，2025/2/24
 * des:用第三方数据sdk获取大模型接口数据
 */
class LLMChatRepository(private val openAI: OpenAI) {

    fun receiveAIMessage(params: TextCompletionsParam): Flow<ChatCompletionChunk>? {
        val requestArgs = ChatCompletionRequest(
            model = ModelId(GPTModel.gpt35Turbo.model),
            messages = toAIList(params.messagesTurbo)
        )

        try {
            return openAI.chatCompletions(requestArgs)
        }  catch (e: OpenAIHttpException) {
            e.printStackTrace()
        } catch (e: OpenAIAPIException) {
            e.printStackTrace()
        }catch (e: Exception) {
            printE(e)
        }

        return null
    }
}