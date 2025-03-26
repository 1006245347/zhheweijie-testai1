import androidx.compose.ui.window.application
import com.hwj.ai.test.WindowsSelectionTest
import di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.Koin

lateinit var koin: Koin

//编译运行命令 ./gradlew :desktop:run
//打包命令 打包时开翻墙要下编译库 ./gradlew packageDistributionForCurrentOS
//安装包路径 build/compose/binaries/main/
//build/compose/binaries/main/exe/
//build/compose/binaries/main/deb/
//Ubuntu/Debian: MyApp-1.0.0.deb

//control +  option +O       control + C 中断调试

fun main() {
    //日志
    Napier.base(DebugAntilog())
    //依赖注入，不需要new对象，全模版生成
    koin = initKoin()
    koin.loadModules(
        listOf()
    )

    return application {
//        PlatformWindowStart { exitApplication() }
//        PlatformWindowStartTest { exitApplication() } //测试截图
        WindowsSelectionTest { exitApplication() } //测试划词
    }
}



