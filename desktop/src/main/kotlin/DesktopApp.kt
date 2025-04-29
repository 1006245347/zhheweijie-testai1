import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import com.hwj.ai.PlatformWindowStart
import com.hwj.ai.global.LocalAppResource
import com.hwj.ai.global.rememberAppResource
import com.hwj.ai.selection.GlobalMouseHook9
import com.hwj.ai.test.WindowsSelectionUIATest
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
    Napier.base(DebugAntilog())//defaultTag = "yuy"
    //依赖注入，不需要new对象，全模版生成
    koin = initKoin()
    koin.loadModules(
        listOf()
    )

    return application {
        val windowState = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = 700.dp,
            height = 500.dp,
        )
        CompositionLocalProvider(
            LocalAppResource provides rememberAppResource(),
        ) {
            val isShowWindowState = remember { mutableStateOf(true) } //控制主窗口显示
            val mainWindow = mutableStateOf<ComposeWindow?>(null)
            val trayState = rememberTrayState()
            Tray(icon = LocalAppResource.current.icon, state = trayState,
                tooltip = "testAi", menu = {
                    Item("open", onClick = {
                        windowState.isMinimized = false //设置程序最小化
                        isShowWindowState.value = true
                        mainWindow.value?.isVisible = true
                    })
                    Item("exit", onClick = {
                        GlobalMouseHook9.stop()
                        exitApplication()
                    })
                }, onAction = { //双击触发
                    windowState.isMinimized = false
                    isShowWindowState.value = true
                    mainWindow.value?.isVisible = true
                })

            PlatformWindowStart(windowState, isShowWindowState, onWindowChange = { w, isShow ->
                mainWindow.value = w
                //主要处理关闭窗口的响应，避免其他地方重复调用
                if (!isShow)
                    mainWindow.value?.isVisible = isShow //关闭主窗口
            }) {
                isShowWindowState.value = false
                exitApplication()
            }
//        WindowsCaptureTest { exitApplication() } //测试截图
//        WindowsSelectionTest { exitApplication() } //测试划词
//        WindowsOcrTest{ exitApplication()} //测试ocr划词
//            WindowsSelectionUIATest(windowState) { exitApplication() } //可识别win32、浏览器选中文字
        }
    }
}



