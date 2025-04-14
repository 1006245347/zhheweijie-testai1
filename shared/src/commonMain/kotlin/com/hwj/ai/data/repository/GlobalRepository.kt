package com.hwj.ai.data.repository

import com.hwj.ai.data.http.JsonApi
import com.hwj.ai.data.http.getWithCookie
import com.hwj.ai.data.http.parseJson
import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.hwj.ai.global.printList
import com.hwj.ai.global.urlModelConfig
import com.hwj.ai.models.LLMModel
import io.ktor.client.HttpClient
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

    suspend fun localModelConfig(): List<LLMModel> {
        val list = mutableListOf<LLMModel>()

        val deepseek = LLMModel()
        deepseek.model = "deepseek-chat"
        deepseek.hostUrl = "https://api.deepseek.com/"
        deepseek.url = "chat/completions"
        deepseek.sk = "sk-6271abd28b07427ba84a38580ed76bc0"
        val siliconflow = LLMModel()
        siliconflow.model = "deepseek-ai/DeepSeek-V3"
        siliconflow.hostUrl = "https://api.siliconflow.cn/"
        siliconflow.url = "chat/completions"
        siliconflow.sk = "sk-qylhzhkqljizdtsbqcssefvqbknxbxxydpwppumwfeijince"
        val hunyuan = LLMModel()
        hunyuan.model = "hunyuan-vision"
//        hunyuan.hostUrl = "https://hunyuan.tencentcloudapi.com/"
        hunyuan.hostUrl="https://api.hunyuan.cloud.tencent.com/v1/"
        hunyuan.url = "chat/completions"
        hunyuan.sk = "sk-NDI07Dpew9y1J7W0Fpoj1ywjo50p7H0cwKePxl4EEjJiLIlI"
        val baitong = LLMModel()
        baitong.model = "gpt-4o"
        baitong.hostUrl = "https://copilot.gree.com/"
        baitong.url = "chat/completions"
        baitong.sk = "1"
        list.add(deepseek)
        list.add(siliconflow)
        list.add(hunyuan)
//        list.add(baitong)
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