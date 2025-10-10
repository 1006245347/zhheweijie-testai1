package di

import com.hwj.ai.data.local.SettingsFactory
import com.hwj.ai.except.ClipboardHelper
import com.hwj.ai.except.DatabaseDriverFactory
import com.hwj.ai.global.NotificationsManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun sharedPlatformModule(): Module = module {
    single { SettingsFactory(context=get()) }
    single { NotificationsManager(context = get()) }
    single { DatabaseDriverFactory(context = get()) } //数据库
    factory { ClipboardHelper(context=get()) }

}

actual fun startUp(){

}