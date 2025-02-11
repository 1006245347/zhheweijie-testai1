package di

import com.hwj.ai.createHttpClient
import com.hwj.ai.data.repository.ConversationRepository
import com.hwj.ai.data.repository.LLMRepository
import com.hwj.ai.data.repository.MessageRepository
import com.hwj.ai.data.repository.SettingsRepository
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        mainModule, modelModule, sharedPlatformModule()
    )
}

@Suppress("unused") // currently only used in debug builds,for ios
fun initKoin(): Koin {
    return initKoin { }.koin
}

//依赖注入目的是为了对象创建解耦，对象不在new具体的类，而是根据模版依赖生成
//factory每次都会创建新实例，而single是单例
val mainModule = module {
    single { createHttpClient(10000) }
    single { LLMRepository(get()) }
    single { ConversationRepository() }
    single { MessageRepository() }
    single { SettingsRepository() }
}

val modelModule = module {
    factoryOf(::ConversationViewModel)
}

/**
 * des:注意声明接口的完整路径，多个平台必须一致
 */
expect fun sharedPlatformModule(): Module