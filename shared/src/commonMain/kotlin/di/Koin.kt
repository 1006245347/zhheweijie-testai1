package di

import com.hwj.ai.createKtorHttpClient
import com.hwj.ai.data.local.PreferenceLocalDataSource
import com.hwj.ai.data.local.SettingsFactory
import com.hwj.ai.data.repository.ConversationRepository
import com.hwj.ai.data.repository.GlobalRepository
import com.hwj.ai.data.repository.LLMChatRepository
import com.hwj.ai.data.repository.LLMRepository
import com.hwj.ai.data.repository.LocalDataRepository
import com.hwj.ai.data.repository.MessageRepository
import com.hwj.ai.data.repository.SettingsRepository
import com.hwj.ai.except.DataSettings
import com.hwj.ai.ui.viewmodel.ChatViewModel
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import com.hwj.ai.ui.viewmodel.SettingsViewModel
import com.hwj.ai.ui.viewmodel.WelcomeScreenModel
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * @author by jason-何伟杰，2025/2/12
 * des: 依赖注入 https://www.jianshu.com/p/bccb93a78cee
 */
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
    single { createKtorHttpClient(60000) }

    factoryOf(::PreferenceLocalDataSource)
    single {
        val factory: SettingsFactory = get()
        factory.createSettings()
    }
    single { DataSettings() }
    single { LLMRepository(get()) }
    single { ConversationRepository() }
    single { MessageRepository() }
    single { SettingsRepository() }
    single { LocalDataRepository(get()) }
    single { LLMChatRepository(get()) }
    single { GlobalRepository(get()) }

//    single {
//        val config = OpenAIConfig(token = LLM_API_KEY,
//            host = OpenAIHost(baseHostUrl),
//            logging = LoggingConfig(com.aallam.openai.api.logging.LogLevel.Body),
//            httpClientConfig = {
//                //换json配置
//                install(ContentNegotiation) {
//                    json(Json {
//                        ignoreUnknownKeys = true // 忽略未知字段
//                        prettyPrint = true
//                        isLenient = true
//                    })
//                }
//                install(Logging) {
//                    level = LogLevel.BODY //禁止流式对话日志
//                    logger = object : Logger {
//                        override fun log(message: String) {
//                            printD(message)
//                        }
//                    }
//                }
//            }
//        )
//        OpenAI(config)
//    }
}

val modelModule = module {
    single { ConversationViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { WelcomeScreenModel(get()) }
    single { ChatViewModel(get(), get(), get()) }
    single { SettingsViewModel(get(), get(), get()) }
}

/**
 * des:注意声明接口的完整路径，多个平台必须一致
 */
expect fun sharedPlatformModule(): Module