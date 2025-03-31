package com.hwj.ai.selection

import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.Psapi
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.HHOOK
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mmarquee.automation.Element
import mmarquee.automation.UIAutomation
import mmarquee.automation.controls.EditBox
import mmarquee.automation.controls.Search
import mmarquee.automation.pattern.LegacyIAccessible
import mmarquee.automation.pattern.Text
import mmarquee.automation.pattern.Value
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent
import java.nio.charset.StandardCharsets

/**
 * @author by jason-何伟杰，2025/3/28
 * des: JNA + UIAutomation
 */
object GlobalMouseHook8 {

    private val automation = UIAutomation.getInstance()
    private var mouseHook: HHOOK? = null
    private val user32 = User32.INSTANCE
    private const val WM_LBUTTONUP = 0x0202
    private const val WM_LBUTTONDOWN = 0x0201
    private val appBuilder = StringBuilder()
    private val contentBuilder = StringBuilder()
    var robot: Robot? = null
    val state=SelectionState()

    private var block1: (String?) -> Unit = {}
    private var block2: (String?) -> Unit = {}

    private val mouseProc6 = WinUser.LowLevelMouseProc { nCode, wParam, lParam ->
        if (nCode >= 0) {
            val wParamInt = wParam.toInt()
            //扩展鼠标事件检测
            if (wParamInt == WM_LBUTTONUP) {
//                printD("鼠标左键抬起，尝试获取前台窗口信息 ${lParam.pt.y}")
                println("Start--------------mouse left up,find window info>${lParam.pt.y}")
                fetchForegroundAppInfo()
                handleMouseAct()
//                textToClipboard()

                val d = distanceBetween(state.mousePressedPos, lParam.pt)
                println("distance>$d")
                if (d>10){
                    state.isDragging=true

                }
            }else if (wParamInt== WM_LBUTTONDOWN){
                state.mousePressedPos=lParam.pt
                state.isDragging=false
            }
        }

        user32.CallNextHookEx(
            mouseHook, nCode, wParam, LPARAM( Pointer.nativeValue(lParam.pointer))
        ) // Found: WinUser.MSLLHOOKSTRUCT! Required: WinDef.LPARAM!
    }

    fun start(appBlock: (String?) -> Unit, contentBlock: (String?) -> Unit) {
        mouseHook = user32.SetWindowsHookEx(
            WinUser.WH_MOUSE_LL, mouseProc6, Kernel32.INSTANCE.GetModuleHandle(null), 0
        )
        if (mouseHook == null) {
            println("Hack install failed>")
            return
        }
        robot = Robot()
        block1 = appBlock
        block2 = contentBlock
        println("Hack install success!")

        // 必须阻塞消息循环，否则 Hook 会立即退出
        val msg = WinUser.MSG()
        while (user32.GetMessage(msg, null, 0, 0) != 0) {
            user32.TranslateMessage(msg)
            user32.DispatchMessage(msg)
        } //有个循环
    }

    fun stop() {
        mouseHook?.let { user32.UnhookWindowsHookEx(it) }
    }

    private fun handleMouseAct() {
        try {
            val focusedElement: Element? = automation.focusedElement

            if (focusedElement != null) {
                val name = focusedElement.name
                if (name.isNotEmpty()) {
                    println("Hack file name>$name") //这个文件名?偶然把整个内容也打出来
                }

                println("v>${focusedElement.className} ${focusedElement.isContentElement}")
                try {
                    println("k>${focusedElement.controlType} ")

//                    val window = automation.getDesktopWindow("c1.txt - 记事本")
//                    if (window != null) {
//                        println("w》${window.name} ${window.isModal}")
//                        val editBox: EditBox = window.getEditBox(Search.getBuilder(0).build())
//
//                        println("eb>${editBox.frameworkId} ")
//                        val p1 = PointerByReference()
//                        val i1 = focusedElement.element.getCurrentFrameworkId(p1)
//                        when (p1.value.getWideString(0)) {
//                            "Win32" -> println("传统Win32控件，优先LegacyIAccessible")
//                            "WPF" -> println(".NET控件，尝试TextPattern")
//                            "XAML" -> println("UWP控件，需特殊处理")
//                        }

//                        val hwnd = user32.GetForegroundWindow()
//                        val selStart = IntByReference()
//                        val selEnd = IntByReference()
//                        user32.SendMessage(
//                            hwnd,
//                            0xB0,
//                            WinDef.WPARAM(selStart.value.toLong()),
//                            LPARAM(selEnd.value.toLong())
//                        )
//                        val selLength = selEnd.value - selStart.value
//                        if (selLength > 0) {
//                            val charArray = CharArray(selLength + 1)
//                            val mem = Memory((selLength + 1) * 2L)
////                            user32.SendMessage(hwnd,0xB2,selStart.value)
//                        }
//                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val textPattern = focusedElement.getProvidedPattern(Text::class.java)
                if (textPattern != null) { //基本都返回null
                    val selection = textPattern.selection //限制选取长度
                    if (selection != null && selection.isNotEmpty()) {
//                        println("Hack text1>${selection}")
//                        println("Hack text2>${selection[0]}")
                        showContent(selection)
                    }
                    return
                }

                val legacy = focusedElement.getProvidedPattern(LegacyIAccessible::class.java)
                if (legacy != null) {
                    println("ll>${legacy.currentValue}")
                }


                val valuePattern = focusedElement.getProvidedPattern(Value::class.java)
                if (valuePattern != null) {
                    val value = valuePattern.value()
                    println("Hack value> $value")
                    if (value.isNotEmpty()) {
                        showContent(value)
                    }
                    return
                } else {
                    println("Hack failed! not support textPattern")
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun textToClipboard() {

//        if (robot == null) {
        robot = Robot()
//        }

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
                println("thread>${Thread.currentThread().name}")
                copiedText = clipboard.getData(DataFlavor.stringFlavor) as String?
                if (!copiedText.isNullOrEmpty()) break
            }
            println("c>$copiedText")
//            clipboard.setContents(emptyBoard, null)
        }
    }

    private fun showContent(text: String) {
        contentBuilder.clear()
        contentBuilder.append(text)
        block2(contentBuilder.toString())
    }

    private fun fetchForegroundAppInfo() {
        val hwnd = user32.GetForegroundWindow()
        if (hwnd == null) {
            println("Hack failed! can not find foreground app info ")
            return
        }

        // 获取窗口标题
        val windowText = CharArray(512)
        user32.GetWindowText(hwnd, windowText, 512)
        val title = String(windowText).trim()

        // 获取进程 ID
        val pidRef = IntByReference()
        user32.GetWindowThreadProcessId(hwnd, pidRef)
        val pid = pidRef.value

        // 获取可执行文件路径
        val hProcess = Kernel32.INSTANCE.OpenProcess(
            WinNT.PROCESS_QUERY_INFORMATION or WinNT.PROCESS_VM_READ, false, pid
        )

        val exePath = CharArray(1024)
        Psapi.INSTANCE.GetModuleFileNameExW(hProcess, null, exePath, 1024)
        Kernel32.INSTANCE.CloseHandle(hProcess)

        appBuilder.clear()
//        appBuilder.append("Hack title: $title").append(". Hack process ID: $pid").append("\n")
//            .append("Hack application path: ${String(exePath).trim()}")
//        block1(appBuilder.toString())
        covertSet()
        println("Hack title: $title")
        println("Hack process ID: $pid")
        println("Hack application path: ${String(exePath).trim()}")
    }

    //处理乱码
    private fun covertSet() {
        val bytes: ByteArray = appBuilder.toString().toByteArray(StandardCharsets.UTF_8)
        block1(String(bytes, StandardCharsets.UTF_8)) //new?性能如何
    }
}

