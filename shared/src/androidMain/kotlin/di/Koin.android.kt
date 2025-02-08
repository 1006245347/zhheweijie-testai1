package di

import com.hwj.ai.global.NotificationsManager
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun sharedPlatformModule(): Module = module {
    single { NotificationsManager(context = get()) }
}