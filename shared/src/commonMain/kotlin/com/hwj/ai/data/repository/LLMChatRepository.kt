package com.hwj.ai.data.repository

import coil3.request.ImageRequest
import com.aallam.openai.api.InternalOpenAI
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.api.image.ImageSize.Companion.is256x256
import com.aallam.openai.api.image.internal.ImageCreationRequest
import com.aallam.openai.api.image.internal.ImageResponseFormat
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.client.OpenAI
import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.hwj.ai.models.GPTModel
import com.hwj.ai.models.TextCompletionsParam
import com.hwj.ai.models.toAIList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * @author by jason-何伟杰，2025/2/24
 * des:用第三方数据sdk获取大模型接口数据
 */
class LLMChatRepository(private val openAI: OpenAI) {

    //流式回复
    suspend fun receiveAIMessage(params: TextCompletionsParam): Flow<ChatCompletionChunk>? {

        val requestArgs = ChatCompletionRequest(
            model = ModelId(GPTModel.gpt35Turbo.model),
            messages = toAIList(params.messagesTurbo)
        )


        try {
            return openAI.chatCompletions(requestArgs)
        } catch (e: OpenAIHttpException) {
            e.printStackTrace()
        } catch (e: OpenAIAPIException) {
            e.printStackTrace()
        } catch (e: Exception) {
            printE(e)
        }

        return null

    }

    //一次回复所有结果数据
    suspend fun receiveCompletion(params: TextCompletionsParam): String? {
        val requestArgs = ChatCompletionRequest(
            model = ModelId(GPTModel.gpt35Turbo.model),
            messages = toAIList(params.messagesTurbo)
        )
        return openAI.chatCompletion(requestArgs).choices.first().message.content
    }

//    @OptIn(InternalOpenAI::class)
//    suspend fun receiveImage(){
//        val imageRequest = ImageCreationRequest(prompt = "美女",n=1,size=is256x256,responseFormat= ImageResponseFormat("url"))
//    }
}