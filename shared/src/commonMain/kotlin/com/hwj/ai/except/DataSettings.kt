package com.hwj.ai.except

import com.russhwolf.settings.coroutines.FlowSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * @author by jason-何伟杰，2025/2/25
 * des:从已注入的单例赋值给全局对象
 */
class DataSettings : KoinComponent {
     val settingsCache: FlowSettings by inject()

    init {
//        printD("set>$settingsCache")
    }
}

