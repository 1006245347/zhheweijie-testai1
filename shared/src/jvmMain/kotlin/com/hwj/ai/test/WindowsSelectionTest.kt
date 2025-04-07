package com.hwj.ai.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.hwj.ai.checkSystem
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.ThemeChatLite
import com.hwj.ai.global.printD
import com.hwj.ai.global.printList
import com.hwj.ai.selection.ClipMonitor
import com.hwj.ai.selection.ClipboardManager
import com.hwj.ai.selection.GlobalMouseHook8
import com.hwj.ai.selection.GlobalMouseHook8.robot
import com.hwj.ai.selection.isValidSelection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.ProvidePreComposeLocals
import java.awt.Dimension
import java.awt.Robot
import java.awt.datatransfer.DataFlavor
import java.awt.event.KeyEvent
import java.io.File
import javax.swing.SwingUtilities

@Composable
fun WindowsSelectionTest(onCloseRequest: () -> Unit) {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        width = 700.dp,
        height = 500.dp,
    )
    val subScope = rememberCoroutineScope()
    var selectedText = remember { mutableStateOf<String?>("find") }
    var appText = remember { mutableStateOf<String?>("") }
    val isStart = remember { mutableStateOf(false) }

    var fileList = remember { mutableStateListOf<File>() }
    var backupTxt = remember { mutableStateOf<String?>(null) }
    val isTxtClip = remember { mutableStateOf<Boolean>(false) }
    LaunchedEffect(isStart.value) {
        if (isStart.value) {
            subScope.launch(Dispatchers.IO) {
                GlobalMouseHook8.start(
                    appBlock = { str ->
                        appText.value = str
                    }, contentBlock = { content ->
                        selectedText.value = content
                    }
                )
                //start()最后有个循环，不能再执行后续代码 ，另起一个
            }

//            return@LaunchedEffect
            subScope.launch(Dispatchers.IO) {
                robot = Robot()
                printD("running>>${isStart.value} ${GlobalMouseHook8.state.isDragging}")
                while (isStart.value) {
                    //记录当前鼠标位置
                    delay(50) //要同步才对
                    if (
                        GlobalMouseHook8.state.isDragging
                    //加上isContent判断吧
//                     &&  selectedText.value != GlobalMouseHook8.state.lastClipboardText
                    ) {
                        try {
                            robot?.let {
                                val clipboard = ClipMonitor.clipManager.clipboard
                                //难道先判断最后一次类型再缓存？
                                val contents = clipboard.getContents(null)
                                isTxtClip.value =
                                    contents.isDataFlavorSupported(DataFlavor.stringFlavor)
                                if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                                    val backTxt = ClipMonitor.clipManager.backupClipTxt()
                                    backTxt?.let {
                                        backupTxt.value = backTxt
                                    }
                                    println("backupTxt>${backupTxt.value}")
                                } else {
//                                    fileList.clear()
                                    val list = //有可能备份错误。。
                                        ClipMonitor.clipManager.fetchClipFile() //本地文件备份
                                    list?.let {
                                        fileList.clear()
                                        fileList.addAll(list)
                                    }
                                    printList(list, "backupList>")
                                }

                                //多轮复制粘贴后，当先复制文件，再选中文字，再cv会无法覆盖
                                it.keyPress(KeyEvent.VK_CONTROL)
                                it.keyPress(KeyEvent.VK_C)
                                it.keyRelease(KeyEvent.VK_C)
                                it.keyRelease(KeyEvent.VK_CONTROL)

                                val startTime = System.currentTimeMillis()
                                var copiedText: String? = null
                                while (System.currentTimeMillis() - startTime < 500) {//一直读剪切板
                                    Thread.sleep(100)

                                    try {
                                        val contents = clipboard.getContents(null)
                                        println(
                                            "content is file>${
                                                contents.isDataFlavorSupported(
                                                    DataFlavor.javaFileListFlavor
                                                )
                                            }"
                                        )
                                        if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                                            copiedText = //这里是接上面 的手动ctrl+c后的数据获取，为啥上面的类型再复制文件后没有变化！
                                                contents.getTransferData(DataFlavor.stringFlavor) as String?
                                        } else {
                                            //剪切板被占用锁住？
//                                            ClipMonitor.clipManager.clear()//前面有文件挡住了，无法有效cv,清空？
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                    if (!copiedText.isNullOrEmpty() && isValidSelection(copiedText)) {
                                        GlobalMouseHook8.state.lastClipboardText = copiedText
                                        GlobalMouseHook8.state.isDragging = false
                                        selectedText.value = copiedText

                                        SwingUtilities.invokeLater {
                                            if (isTxtClip.value) {  //根据 之前的数据类型进行备份？
                                                backupTxt.value?.let { b ->
                                                    ClipMonitor.clipManager.restoreClipboardTxt(b)
                                                }
                                            } else {
                                                if (fileList.isNotEmpty()) {
                                                    ClipboardManager().restoreClipFile(fileList)
                                                } else {
                                                }
                                            }
                                        }
                                        break
                                    }
                                }
                                GlobalMouseHook8.state.isDragging = false //保守重置状态
                                println("copyText>$copiedText")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } else {
            GlobalMouseHook8.stop()
        }
    }

    return Window(
        onCloseRequest, title = "hwj-ai-chat", state = windowState
    ) {
        val window = this.window
        window.minimumSize = Dimension(600, 450)
        ProvidePreComposeLocals {
            ThemeChatLite {
                Surface(Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                        Column {
                            Button(onClick = {
                                if (checkSystem() == OsStatus.WINDOWS) {
                                    isStart.value = !isStart.value
                                }
                            }) {
                                Text("windows划词 ${isStart.value}>")
                            }
                            Button(onClick = {
                                val list1 = ClipMonitor.clipManager.fetchClipFile()
                                printList(list1)
                                ClipMonitor.clipManager.restoreClipFile(list1)
                            }) {
                                Text("restore>")
                            }
                            Text(
                                text = "app> ${appText.value}",
                                fontFamily = FontFamily.Default,
                                maxLines = 5
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = "find> ${selectedText.value}",
                                fontFamily = FontFamily.Default,
                                maxLines = 8
                            )
                        }
                    }
                }
            }
        }
    }
}