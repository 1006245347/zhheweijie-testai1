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
}