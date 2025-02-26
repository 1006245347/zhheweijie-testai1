package com.hwj.ai.data.http

import com.hwj.ai.global.DATA_APP_TOKEN
import com.hwj.ai.global.getCacheString
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

object ApiService {

    suspend fun HttpClient.getUpgradeInfo(pkName: String = "com.lyentech.gg") =
        fetch<String> {
            val url = "http://cis.leayun.cn/cis/app/open/getNewVersion?packageName=$pkName"
            url(url)
            method = HttpMethod.Get
        }

    suspend fun HttpClient.getHttp(url: String, params: Map<String, String>?) =
        fetch<String> {
            getCacheString(DATA_APP_TOKEN)?.let {
                header("Cookie", it)
            }
            getCacheString(DATA_APP_TOKEN)?.let {
                header("Cookie", it)
            }

            params?.forEach {
                parameter(it.key, it.value)
            }
            url(url)
            method = HttpMethod.Get
        }

    suspend fun HttpClient.postHttp(url: String, params: Map<String, String>?) =
        sumit<String> {
            params?.forEach {
                parameter(it.key, it.value)
            }
            url(url)
            method = HttpMethod.Post
        }

    // https://ktor.io/docs/client-serialization.html#send_data 必须构造个bean
    suspend fun HttpClient.postJsonHttp(
        url: String,
        jsonBean: Any,
        params: Map<String, String>? = null
    ) =
        sumit<String> {
            url(url)
            params?.forEach {
                parameter(it.key, it.value)
            }
            contentType(ContentType.Application.Json)
            setBody(jsonBean)
            method = HttpMethod.Post
        }
}