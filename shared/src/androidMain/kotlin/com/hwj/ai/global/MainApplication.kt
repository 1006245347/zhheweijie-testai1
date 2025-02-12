package com.hwj.ai.global

import android.app.Application
import android.content.Context
import di.initKoin
//import org.koin.android.ext.koin.androidContext
import java.io.File

open class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        initKoin {
//            androidContext(appContext)
        }
    }


    companion object {
        lateinit var appContext: Context

        fun getUserCacheDir(): File? {
            return appContext.getExternalFilesDir("file_cache")
        }
    }
}