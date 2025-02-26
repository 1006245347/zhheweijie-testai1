package com.hwj.ai.data.repository

class SettingsRepository {

    suspend fun getAppData():List<String>{
        return listOf("1>","2>","3>")
    }
}