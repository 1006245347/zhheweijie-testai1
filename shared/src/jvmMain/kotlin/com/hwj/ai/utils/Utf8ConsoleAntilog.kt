package com.hwj.ai.utils

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class Utf8ConsoleAntilog:Antilog() {
    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        val log = "[${priority.name}] ${tag ?: "Napier"}: ${message.orEmpty()}"
        println(log) // JVM默认是UTF-8，但如果是IDE终端则受编码影响
    }
}