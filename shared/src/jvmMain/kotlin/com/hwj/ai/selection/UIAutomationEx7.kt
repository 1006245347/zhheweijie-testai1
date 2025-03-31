package com.hwj.ai.selection

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.COM.COMUtils
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.Psapi
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.HHOOK
import com.sun.jna.platform.win32.WinUser.WM_GETICON
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import io.ktor.utils.io.core.toByteArray
import mmarquee.automation.Element
import mmarquee.automation.PatternID
import mmarquee.automation.UIAutomation
import mmarquee.automation.controls.EditBox
import mmarquee.automation.controls.Search
import mmarquee.automation.pattern.LegacyIAccessible
import mmarquee.automation.pattern.Text
import mmarquee.automation.pattern.Value
import java.nio.charset.StandardCharsets

/**
 * @author by jason-何伟杰，2025/3/28
 * des: JNA + UIAutomation
 */
object GlobalMouseHook7 {

    private val automation = UIAutomation.getInstance()
    private var mouseHook: HHOOK? = null
    private val user32 =User32.INSTANCE
    private const val WM_LBUTTONUP = 0x0202
    private val appBuilder = StringBuilder()
    private val contentBuilder = StringBuilder()
    private var endPoint:Long=0

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
            }
        }

        endPoint =  Pointer.nativeValue(lParam.pointer)
        user32.CallNextHookEx(
            mouseHook, nCode, wParam, LPARAM(endPoint)
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

        block1 = appBlock
        block2 = contentBlock
        println("Hack install success!")

        // 必须阻塞消息循环，否则 Hook 会立即退出
        val msg = WinUser.MSG()
        while (user32.GetMessage(msg, null, 0, 0) != 0) {
            user32.TranslateMessage(msg)
            user32.DispatchMessage(msg)
        }
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

                    val window = automation.getDesktopWindow("c1.txt - 记事本")
                    if (window != null) {
                        println("w》${window.name} ${window.isModal}")
                        val editBox = window.getEditBox(Search.getBuilder(0).build())

                        println("eb>${editBox.frameworkId} ")
                        val p1 = PointerByReference()
                        val i1 = focusedElement.element.getCurrentFrameworkId(p1)
                        when (p1.value.getWideString(0)) {
                            "Win32" -> println("传统Win32控件，优先LegacyIAccessible")
                            "WPF" -> println(".NET控件，尝试TextPattern")
                            "XAML" -> println("UWP控件，需特殊处理")
                        }

                        if (editBox != null) {
                            val hwnd =user32.GetForegroundWindow()
                            println("hwnd=$hwnd")
                            hwnd?.let {
                                val textLength = user32.SendMessage(
                                    hwnd, 0x000E,
                                    WinDef.WPARAM(0), LPARAM(0)
                                ).toInt()
                                println("textLength>"+textLength)
                                if (textLength > 0) {
                                    val textBuffer = CharArray(textLength + 1)
                                    val pointer =
                                        Native.malloc((textLength + 1) * Native.WCHAR_SIZE.toLong())
                                    user32.SendMessage(
                                        hwnd,
                                        0x000D,
                                        WinDef.WPARAM((textLength + 1).toLong()),
                                        LPARAM((pointer))
                                    )
                                    val sTxt = String(textBuffer).trim()
                                    println("ss>$sTxt")
                                    Native.free(pointer)
                                }
                            }

//                            val le =
//                                editBox.element.getProvidedPattern(LegacyIAccessible::class.java)
//                            println("le=$le")//null
//                            println("e>${editBox.name} ${editBox.value}")
//                        editBox.value="sskk"//可设置值
//                            val txtP = editBox.element.getProvidedPattern(Text::class.java)
//                            println("p1>$txtP")
//                            if (txtP != null) {
//                                println("p>${txtP.selection}")
//                            }
//                            println("et>${editBox.selection}")
                        }

//                        val document = window.getDocument(Search.getBuilder(0).build())
//                        if (document != null) {
//                            println("d1>${document.name} ${document.selection}")
//                        }

//                        val window1 = automation.getDesktopWindow("*新文件 6 - Notepad++")
//                        if (window1!=null){
//                            val panel = window1.getPanel(Search.getBuilder(0).build())
//                            if (panel!=null){
//                                println("p3>${panel.name}")
//                            }
//                        }

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val textPattern = focusedElement.getProvidedPattern(Text::class.java)
                if (textPattern != null) {
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


//                val ref1: PointerByReference = focusedElement.getPattern(PatternID.Text.value)
//                println("Hack1>>>>${ref1}")
//                println("Hack2>>>${ref1.pointer.getWideString(0)}")
////                println("Hack2>>>>${patternhr.value.getWideString(0)}")
//
//                val selectRef= PointerByReference()


            }
        } catch (e: Exception) {
            e.printStackTrace()
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

