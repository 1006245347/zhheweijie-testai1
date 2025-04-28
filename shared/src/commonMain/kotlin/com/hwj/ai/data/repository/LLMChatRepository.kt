package com.hwj.ai.data.repository

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.Effort
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolChoice
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.http.Timeout
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
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY_PROPERTY_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

/**
 * @author by jason-何伟杰，2025/2/24
 * des:用第三方数据sdk获取大模型接口数据
 */
class LLMChatRepository(private val client: HttpClient) {//private val openAI: OpenAI,单例的话无法变更模型参数


    //流式回复
    fun receiveAIMessage(
        params: TextCompletionsParam,
        useThink: Boolean = false,
        useWeb: Boolean = false
    ): Flow<ChatCompletionChunk>? {
        val openAI = OpenAI(setAIConfig())
        var thinkEffort: Effort? = null
        var thinkResponseFormat: ChatResponseFormat? = null
        if (useThink) {
            thinkEffort = Effort("medium")
            thinkResponseFormat = ChatResponseFormat.JsonObject
        }

        val requestArgs = ChatCompletionRequest(
            model = ModelId(if (useThink) GPTModel.DeepSeekR1.model else params.model.model),
            reasoningEffort = thinkEffort,
            responseFormat = thinkResponseFormat,
            messages = params.messagesTurbo,
            temperature = if (useThink) 0.6 else params.temperature,
            topP = if (useThink) 0.95 else params.topP,
            n = params.n,
            maxTokens = params.maxTokens,
        )

//        try {

            return openAI.chatCompletions(requestArgs).catch { e->
                printE(e,"msg")
            }
//        } catch (e: Exception) {
//            printE(e, des = "msg")
//            return null
//        }
    }

    suspend fun receiveAICompletion(
        params: TextCompletionsParam, chats: MutableList<ChatMessage>? = null
    ): ChatCompletion {
        val openAI = OpenAI(setAIConfig())
        var thinkResponseFormat: ChatResponseFormat? = null

        val requestArgs = ChatCompletionRequest(
            model = ModelId(params.model.model),
            responseFormat = thinkResponseFormat,
            messages = chats ?: params.messagesTurbo,
            temperature = params.temperature,
            topP = params.topP,
            n = params.n,
            maxTokens = params.maxTokens,
        )

        return openAI.chatCompletion(requestArgs)
    }

    //图生文要专用模型才行
    fun analyzeImage(params: TextCompletionsParam): Flow<ChatCompletionChunk> {
        val requestArgs = ChatCompletionRequest(
//            model = ModelId(GPTModel.visionhunyuan.model),
//            model= ModelId(GPTModel.visionDeepv2.model),
            model = ModelId(GPTModel.visionQwen.model),
            messages = params.messagesTurbo
        )

        val map = mutableMapOf<String, String>()
        val openAI = OpenAI(
            setAIConfig(
//                token = "sk-NDI07Dpew9y1J7W0Fpoj1ywjo50p7H0cwKePxl4EEjJiLIlI",
//                hostUrl = "https://api.hunyuan.cloud.tencent.com/v1/",
                token = "sk-qylhzhkqljizdtsbqcssefvqbknxbxxydpwppumwfeijince",
                hostUrl = "https://api.siliconflow.cn/v1/",
                headers = map
            )
        )
        try {
            return openAI.chatCompletions(requestArgs, requestOptions = RequestOptions())
        } catch (e: Exception) {
            printE(e, "http-err")
        }
        return flowOf()
    }

    suspend fun GenerateImage(params: TextCompletionsParam) {
        //        val imageRequest = ImageCreationRequest(prompt = "美女",n=1,size=is256x256,responseFormat= ImageResponseFormat("url"))

    }

//    suspend fun AnalyzeFile(){
//        OpenAI().file()
//    }

    //函数工具调用，需要特定模型，有些还内置工具，如网络请求
    suspend fun toolAICall(
        params: TextCompletionsParam,
        tool: Tool
    ): ChatCompletion {
        val openAI = OpenAI(setAIConfig())
        val requestArgs = chatCompletionRequest {
            model = ModelId(GPTModel.QwenTool.model)
            messages = (params.messagesTurbo)
            tools { tool(tool) }
            toolChoice = ToolChoice.Auto
        }
        return openAI.chatCompletion(requestArgs)
    }


    private fun setAIConfig(
        token: String = LLM_API_KEY,
        hostUrl: String = baseHostUrl,
        headers: Map<String, String> = emptyMap()
    ): OpenAIConfig {
        return OpenAIConfig(token = token,
            host = OpenAIHost(hostUrl),
            headers = headers,
            timeout = Timeout(connect = 5.seconds),
            logging = LoggingConfig(com.aallam.openai.api.logging.LogLevel.Body),
//            httpClientConfig = {
//                //换json配置
//                install(client)
//                install(ContentNegotiation) {
//                    json(Json {
//                        ignoreUnknownKeys = true // 忽略未知字段
//                        prettyPrint = true
//                        isLenient = true
//                    })
//                }
//                install(Logging) {
//                    level = LogLevel.INFO //禁止流式对话日志
//                    logger = object : Logger {
//                        override fun log(message: String) {
//                            printD(message)
//                        }
//                    }
//                }
//            }
        )
    }
}