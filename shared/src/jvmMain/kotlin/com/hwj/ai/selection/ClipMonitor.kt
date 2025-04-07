package com.hwj.ai.selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hwj.ai.global.printList
import com.hwj.ai.selection.GlobalMouseHook8.robot
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.POINT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.event.KeyEvent
import java.io.File
import kotlin.math.pow
import kotlin.math.sqrt


object ClipMonitor {
    val clipManager: ClipboardManager = ClipboardManager()
    private var lastCopiedFile: List<File>? = null
    private var lastCopiedText: String? = null

//    init {
//        //定时任务，每一秒检测一下
//        fixedRateTimer("ClipboardWatcher", true, 0L, 5000) {
//            checkClipboardContent()
//        }
//    }

    private fun checkClipboardContent() {
        val curType = getClipType()
        when (curType) {
            DataFlavor.javaFileListFlavor -> {
                val files = clipManager.fetchClipFile()
                if (!files.isNullOrEmpty()) {
                    printList(files)
                }

                val text = clipManager.backupClipTxt()
                println("check>$text")
            }
        }
    }

    fun getClipType(): DataFlavor {
        val contents = clipManager.clipboard.getContents(null) ?: return DataFlavor.stringFlavor
        return when {
            contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor) -> DataFlavor.javaFileListFlavor
            else -> DataFlavor.stringFlavor
        }
    }
}

@Composable
fun ClipMonitor1(isActive: Boolean, state: SelectionState) {
    var selectedTxt by remember { mutableStateOf("") }
//    val state = remember { SelectionState() }
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                println("runing>>")
                //记录当前鼠标位置

                delay(100) //要同步才对
                if (state.isDragging) {
                    robot = Robot()
                    robot?.let {
                        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
//            val emptyBoard = StringSelection("")
//            clipboard.setContents(emptyBoard, null)

                        it.keyPress(KeyEvent.VK_CONTROL)
                        it.keyPress(KeyEvent.VK_C)
                        it.keyRelease(KeyEvent.VK_C)
                        it.keyRelease(KeyEvent.VK_CONTROL)

                        val startTime = System.currentTimeMillis()
                        var copiedText: String? = null

                        while (System.currentTimeMillis() - startTime < 500) {
                            Thread.sleep(50)
                            copiedText = clipboard.getData(DataFlavor.stringFlavor) as String?
                            if (!copiedText.isNullOrEmpty() && isValidSelection(copiedText)) {
                                break
                            }
                        }
                    }
                }
            }
        }
    }
}

//会把最新的内容置null
fun clearClip() {
    val os = System.getProperty("os.name").lowercase()
    when {
        "win" in os -> Runtime.getRuntime().exec(arrayOf("cmd", "/c", "echo off | clip"))
        "nix" in os || "nux" in os -> Runtime.getRuntime()
            .exec(arrayOf("sh", "-c", "xclip -selection clipboard -delete"))

        else -> throw UnsupportedOperationException("Unsupported OS")
    }.waitFor()
}

fun setClipboardContent(content: String) {
    val board = Toolkit.getDefaultToolkit().systemClipboard
    val text = StringSelection(content)
//    println("board?$text")
    board.setContents(text, null)
}


fun restoreClipFile(files: List<File>) {
    val transferable = object : Transferable {
        override fun getTransferDataFlavors(): Array<DataFlavor> {
//            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
            return arrayOf(DataFlavor.javaFileListFlavor)
        }

        override fun isDataFlavorSupported(p0: DataFlavor?): Boolean {
            return p0 == DataFlavor.javaFileListFlavor
        }

        override fun getTransferData(p0: DataFlavor?): Any {
            if (isDataFlavorSupported(p0)) {
                return files
            }
            throw UnsupportedFlavorException(p0)
        }
    }
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    // 先清空剪贴板现有内容
//    clipboard.setContents(Transferable { _, _ -> null }, null)
    clipboard.setContents(transferable, null)
}

suspend fun detectedDragging(state: SelectionState) {
    val currentPos = POINT()
    repeat(4) {
        delay(50)
        User32.INSTANCE.GetCursorPos(currentPos)
        if (distanceBetween(state.mousePressedPos, currentPos) > 2) {
            state.isDragging = true
            return
        }
    }
}

// 判断是否为有效选中
fun isValidSelection(text: String): Boolean {
    return when {
        text.isEmpty() -> false
        text == GlobalMouseHook8.state.lastClipboardText -> false
        text.length > 300 -> false // 防止复制大段代码
        else -> {
            GlobalMouseHook8.state.lastClipboardText = text
            true
        }
    }
}

// 计算鼠标移动距离
fun distanceBetween(p1: POINT, p2: POINT): Int {
    return sqrt((p2.x - p1.x).toDouble().pow(2) + (p2.y - p1.y).toDouble().pow(2)).toInt()
}

class SelectionState {
    var lastClipboardText = ""
    var mousePressedPos = WinDef.POINT() // 记录鼠标按下时的坐标
    var isDragging = false
}