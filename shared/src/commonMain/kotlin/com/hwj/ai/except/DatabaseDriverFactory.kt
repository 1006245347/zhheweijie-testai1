package com.hwj.ai.except

import app.cash.sqldelight.db.SqlDriver

/**
 * @author by jason-何伟杰，2025/2/8
 * des:定义平台特性接口，全部放在Platform.kt文件太长了
 * 每个平台接口路径都要有except
 */
expect class DatabaseDriverFactory {
    fun createDriver():SqlDriver
}