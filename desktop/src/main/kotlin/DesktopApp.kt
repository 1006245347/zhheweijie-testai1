import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.hwj.ai.ui.global.TestPage
import di.initKoin
import org.koin.core.Koin

lateinit var koin: Koin

//编译运行命令 ./gradlew :desktop:run
//打包命令 ./gradlew packageDistributionForCurrentOS
//安装包路径 build/compose/binaries/main/
//build/compose/binaries/main/exe/
//build/compose/binaries/main/deb/
//Ubuntu/Debian: MyApp-1.0.0.deb
fun main() {
    koin = initKoin()
    koin.loadModules(
        listOf()
    )

    return application {
        Window(
            onCloseRequest = { exitApplication() },
            title = "hwj-ai",
            state = rememberWindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                width = 1200.dp,
                height = 700.dp,
            ),
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                TestPage() //入口界面
            }
        }
    }
}

