package com.hwj.ai.data.local

import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow

class PreferenceLocalDataSource(
    val settings: FlowSettings
) {

    // GETTERS

    suspend fun welcomeShown(): Boolean =
        settings.getBoolean(WELCOME_SHOWN, false)

    fun inAppReviewShown(): Flow<Boolean> =
        settings.getBooleanFlow(IN_APP_REVIEW_SHOWN, false)

    fun coins(): Flow<Int> =
        settings.getIntFlow(COINS, 5)

    // SETTERS

    suspend fun setWelcomeShown() {
        settings.putBoolean(WELCOME_SHOWN, true)
    }

    suspend fun setInAppReviewShown() {
        settings.putBoolean(IN_APP_REVIEW_SHOWN, true)
    }

    suspend fun addTokens(tokens: Int): Int {
        val currentTokens = settings.getInt(TOKENS_TOTAL, 0)
        val newTokens = currentTokens + tokens
        settings.putInt(TOKENS_TOTAL, newTokens)
        return newTokens
    }

    suspend fun incrementMessages(): Int {
        val currentMessages = settings.getInt(MESSAGES_TOTAL, 0)
        val newMessages = currentMessages + 1
        settings.putInt(MESSAGES_TOTAL, newMessages)
        return newMessages
    }

    suspend fun setCoins(coins: Int) {
        settings.putInt(COINS, coins)
    }

    suspend fun saveString(key: String, value: String?) {
        value?.let {
            settings.putString(key, it)
        }
    }

    suspend fun saveInt(key: String, value: Int) {
        settings.putInt(key, value)
    }

    suspend fun saveBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
    }

    suspend fun saveFloat(key: String, value: Float) {
        settings.putFloat(key, value)
    }

    suspend fun saveDouble(key: String, value: Double) {
        settings.putDouble(key, value)
    }

    suspend fun getString(key: String): String? {
        return settings.getStringOrNull(key)
    }

    suspend fun getInt(key: String): Int {
        return settings.getInt(key, 0)
    }

    suspend fun getBoolean(key: String): Boolean {
        return settings.getBoolean(key, false)
    }

    suspend fun getFloat(key: String): Float {
        return settings.getFloat(key, 0f)
    }

    suspend fun getDouble(key: String): Double {
        return settings.getDouble(key, 0.0)
    }

    companion object {
        private const val WELCOME_SHOWN = "welcome_shown"
        private const val IN_APP_REVIEW_SHOWN = "in_app_review_shown"
        private const val TOKENS_TOTAL = "tokens_total"
        private const val MESSAGES_TOTAL = "messages_total"
        private const val COINS = "coins"
    }

}
