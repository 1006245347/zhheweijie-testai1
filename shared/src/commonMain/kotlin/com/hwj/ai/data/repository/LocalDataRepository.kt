package com.hwj.ai.data.repository

import com.hwj.ai.data.local.PreferenceLocalDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * @author by jason-何伟杰，2025/2/12
 * des:引入PreferenceLocalDataSource是多实例非单例
 */
class LocalDataRepository(
    private val dataSource: PreferenceLocalDataSource
) {

    suspend fun welcomeShown(): Boolean = withContext(Dispatchers.Default) {
        dataSource.welcomeShown()
    }

    fun inAppReviewShown(): Flow<Boolean> = dataSource.inAppReviewShown()

    suspend fun setWelcomeShown(): Unit = withContext(Dispatchers.Default) {
        dataSource.setWelcomeShown()
    }

    suspend fun setInAppReviewShown(): Unit = withContext(Dispatchers.Default) {
        dataSource.setInAppReviewShown()
    }

}