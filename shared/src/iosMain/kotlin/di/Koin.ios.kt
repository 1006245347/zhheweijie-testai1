package di

import com.hwj.ai.data.local.SettingsFactory
import com.hwj.ai.except.DatabaseDriverFactory
import com.hwj.ai.global.NotificationsManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual  fun sharedPlatformModule():Module = module {
//    singleOf(::SettingsFactory)
    single { SettingsFactory() }
    single { NotificationsManager() }
    single { DatabaseDriverFactory() }
}