package di

import com.hwj.ai.data.local.SettingsFactory
import com.hwj.ai.except.ClipboardHelper
import com.hwj.ai.except.DatabaseDriverFactory
import com.hwj.ai.global.NotificationsManager
import com.hwj.ai.global.initKermitLog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual fun sharedPlatformModule(): Module = module {
    single { SettingsFactory() }
    single { NotificationsManager() }
    single { DatabaseDriverFactory() }
    factory { ClipboardHelper() }
}

actual fun startUp(){
    Napier.base(DebugAntilog())
    initKermitLog()
}