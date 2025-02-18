package com.hwj.ai.data.local

/**
 * @author by jason-何伟杰，2025/2/18
 * des:模拟流式数据
 */

var mockS1 =
    "data: {\"id\":\"3e9b820e-0419-4c02-8588-80278b8a591f\",\"object\":\"chat.completion.chunk\",\"created\":1739867232,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_3a5770e1b4\",\"choices\":[{\"index\":0,\"delta\":{\"role\":\"assistant\",\"content\":\"\"},\"logprobs\":null,\"finish_reason\":null}]}"

var mockS2 =
    "data: {\"id\":\"3e9b820e-0419-4c02-8588-80278b8a591f\",\"object\":\"chat.completion.chunk\",\"created\":1739867232,\"model\":\"deepseek-chat\",\"system_fingerprint\":\"fp_3a5770e1b4\",\"choices\":[{\"index\":0,\"delta\":{\"content\":\"中国\"},\"logprobs\":null,\"finish_reason\":null}]}"

fun mockMinList() {
    var mockSList = mutableListOf<String>()
    for (i in 0 until 30) {
        if (i % 2 == 0) {
            mockSList.add(mockS1)
        } else {
            mockSList.add(mockS2)
        }
    }
}