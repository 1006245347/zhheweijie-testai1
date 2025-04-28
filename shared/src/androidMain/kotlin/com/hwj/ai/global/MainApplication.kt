package com.hwj.ai.global

import android.app.Application
import android.content.Context
import com.hwj.ai.BuildConfig
import com.hwj.ai.except.EnvLoader
import di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import java.io.File

open class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        if (BuildConfig.DEBUG){
            Napier.base(DebugAntilog())
        }
        EnvLoader.init(appContext)
        initKoin {
            //不加这个上下文，单例注入无法成功
            androidContext(androidContext = this@MainApplication)
        }
    }

    companion object {
        lateinit var appContext: Context

        fun getUserCacheDir(): File? {
            return appContext.getExternalFilesDir("file_cache")
        }
    }
}