package com.hwj.ai.data.repository

import com.hwj.ai.data.http.JsonApi
import com.hwj.ai.data.http.getWithCookie
import com.hwj.ai.data.http.parseJson
import com.hwj.ai.except.Env
import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.hwj.ai.global.printList
import com.hwj.ai.global.urlModelConfig
import com.hwj.ai.models.LLMModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.prepareGet
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonObject

/**
 * @author by jason-何伟杰，2025/2/26
 * des:通用数据获取
 */
class GlobalRepository(private val client: HttpClient) {

    suspend fun fetchModelConfig(): List<LLMModel>? {
        try {
            val json = client.getWithCookie(urlModelConfig)
            val jsonObject = JsonApi.parseToJsonElement(json).jsonObject
            val modelList = mutableListOf<LLMModel>()
            jsonObject.keys.forEach { model ->
                val js = jsonObject.get(model)?.jsonObject.toString()
                var llmModel: LLMModel? = null
                try {
                    llmModel = Json.parseJson<LLMModel>(js)
                } catch (e: Exception) {
                    printE(e)
                }
                llmModel?.let {
                    if (model == "default") {
                        modelList.add(0, llmModel)//这样存第一个是default
                    } else {
                        modelList.add(llmModel)
                    }
                }
            }

            return modelList
        } catch (e: Exception) {
            printE(e)
            return null
        }
    }

    suspend fun isConnectNet(): Boolean {
        var isSuc = false
        try {
            client.prepareGet("https://www.baidu.com") {
                timeout { requestTimeoutMillis = 3000 }
            }.execute { response ->
                if (response.status.value == 200) {
                    isSuc = true
                }
            }
            return isSuc
        } catch (e: Exception) {
           printE(e)
            return false
        }
    }

    suspend fun localModelConfig(): List<LLMModel> {
        val list = mutableListOf<LLMModel>()

        val siliconflow = LLMModel()
        siliconflow.model = Env.get("MODEL_TEXT_SILICONFLOW")
        siliconflow.hostUrl = Env.get("API_HOST_SILICONFLOW")
        siliconflow.sk = Env.get("API_KEY_SILICONFLOW")

        val deepseek = LLMModel()
        deepseek.model = Env.get("MODEL_THINK")
        deepseek.hostUrl = Env.get("API_HOST_SILICONFLOW")
        deepseek.sk = Env.get("API_KEY_SILICONFLOW")

        val hunyuan = LLMModel()
        hunyuan.model = Env.get("MODEL_TEXT_HUANYUAN")
        hunyuan.hostUrl = Env.get("API_HOST_HUANYUAN")
        hunyuan.sk = Env.get("API_KEY_HUNYUAN")

        list.add(deepseek)
        list.add(siliconflow)
        list.add(hunyuan)

        return list
    }
}

//目前已支持的 VLM 模型： 硅基流动
//
//Qwen 系列：
//Qwen/Qwen2.5-VL-32B-Instruct
//Qwen/Qwen2.5-VL-72B-Instruct
//Qwen/QVQ-72B-Preview
//Qwen/Qwen2-VL-72B-Instruct
//Pro/Qwen/Qwen2-VL-7B-Instruct
//Pro/Qwen/Qwen2.5-VL-7B-Instruct
//DeepseekVL2 系列：
//deepseek-ai/deepseek-vl2

//混元视觉
//token = "sk-NDI07Dpew9y1J7W0Fpoj1ywjo50p7H0cwKePxl4EEjJiLIlI",
//                hostUrl = "https://api.hunyuan.cloud.tencent.com/v1/",
//hunyuan-vision