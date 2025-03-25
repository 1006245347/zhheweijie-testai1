package com.hwj.ai.data.repository

import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import com.hwj.ai.global.LLM_API_KEY
import com.hwj.ai.global.baseHostUrl
import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.hwj.ai.models.GPTModel
import com.hwj.ai.models.TextCompletionsParam
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

/**
 * @author by jason-何伟杰，2025/2/24
 * des:用第三方数据sdk获取大模型接口数据
 */
class LLMChatRepository {//private val openAI: OpenAI,单例的话无法变更模型参数


    //流式回复
    suspend fun receiveAIMessage(params: TextCompletionsParam): Flow<ChatCompletionChunk>? {
        val openAI = OpenAI(setAIConfig())
        val requestArgs = ChatCompletionRequest(
            model = ModelId(GPTModel.gpt35Turbo.model),
            messages = (params.messagesTurbo)
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
        val openAI = OpenAI(
            setAIConfig(
                token = "sk-NDI07Dpew9y1J7W0Fpoj1ywjo50p7H0cwKePxl4EEjJiLIlI",
                hostUrl = "https://hunyuan.tencentcloudapi.com/"
            )
        )
        val requestArgs = ChatCompletionRequest(
            model = ModelId(GPTModel.gpt35Turbo.model),
            messages = (params.messagesTurbo)
        )
        return openAI.chatCompletion(requestArgs).choices.first().message.content
    }

    //图生文要专用模型才行
    suspend fun AnalyzeImage(params: TextCompletionsParam): Flow<ChatCompletionChunk>? {
        val requestArgs = ChatCompletionRequest(
            model = ModelId(GPTModel.hunyuan.model),
            messages = params.messagesTurbo
        )
        val map = mutableMapOf<String, String>()
//        map["d"] = ""
        try {
            val openAI = OpenAI(
                setAIConfig(
                    token = "sk-NDI07Dpew9y1J7W0Fpoj1ywjo50p7H0cwKePxl4EEjJiLIlI",
                    hostUrl = "https://api.hunyuan.cloud.tencent.com/v1/",
                    headers = map
                )
            )
            return openAI.chatCompletions(requestArgs, requestOptions = RequestOptions())
        } catch (e: OpenAIAPIException) {
            e.printStackTrace()
        } catch (e: OpenAIHttpException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.message?.let {
                if (it.contains("HostException")) {

                }
            }

            printE(e)
        }
        return null
    }

    suspend fun GenerateImage(params: TextCompletionsParam) {
        //        val imageRequest = ImageCreationRequest(prompt = "美女",n=1,size=is256x256,responseFormat= ImageResponseFormat("url"))

    }

//    suspend fun AnalyzeFile(){
//        OpenAI().file()
//    }

    private fun setAIConfig(
        token: String = LLM_API_KEY,
        hostUrl: String = baseHostUrl,
        headers: Map<String, String> = emptyMap()
    ): OpenAIConfig {
        return OpenAIConfig(token = token,
            host = OpenAIHost(hostUrl),
            headers = headers,
            logging = LoggingConfig(com.aallam.openai.api.logging.LogLevel.Body),
            httpClientConfig = {
                //换json配置
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true // 忽略未知字段
                        prettyPrint = true
                        isLenient = true
                    })
                }
                install(Logging) {
                    level = LogLevel.BODY //禁止流式对话日志
                    logger = object : Logger {
                        override fun log(message: String) {
                            printD(message)
                        }
                    }
                }
            })
    }
}