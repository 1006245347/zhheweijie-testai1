package com.hwj.ai.selection

import androidx.compose.material.Text
import com.hwj.ai.global.printList
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.Psapi
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.Variant
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.HHOOK
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import io.ktor.util.reflect.instanceOf
import io.ktor.utils.io.core.toByteArray
import mmarquee.automation.Element
import mmarquee.automation.PatternID
import mmarquee.automation.PropertyID
import mmarquee.automation.UIAutomation
import mmarquee.automation.controls.Document
import mmarquee.automation.controls.Search
import mmarquee.automation.controls.Window
import mmarquee.automation.pattern.Text
import mmarquee.uiautomation.IUIAutomationElement
import mmarquee.uiautomation.IUIAutomationTextPattern
import mmarquee.uiautomation.TreeScope
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

/**
 * @author by jason-何伟杰，2025/3/28
 * des: JNA + UIAutomation
 */
object GlobalMouseHook9 {

    private val automation = UIAutomation.getInstance()
    private var mouseHook: HHOOK? = null
    private val user32 = User32.INSTANCE
    private const val WM_LBUTTONUP = 0x0202
    private val appBuilder = StringBuilder()
    private val contentBuilder = StringBuilder()
    private var endPoint: Long = 0

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
//                handleMouseAct()
                handleWordAct()
            }
        }

        endPoint = Pointer.nativeValue(lParam.pointer)
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

    private fun handleWordAct() {
        val focusedElement: Element? = automation.focusedElement

        val fhwnd = user32.GetForegroundWindow()
        val processId = IntByReference()
        val targetThreadId = user32.GetWindowThreadProcessId(fhwnd, processId)
        val currentThreadId = Kernel32.INSTANCE.GetCurrentThreadId()

        var attatched = false
        if (targetThreadId != currentThreadId) {
            attatched = user32.AttachThreadInput(
                WinDef.DWORD(currentThreadId.toLong()),
                WinDef.DWORD(targetThreadId.toLong()), true
            )
        }

        val hwnd = ExUser32.INSTANCE.GetFocus()
//        focusedElement?.let {
//            val p1=PointerByReference()
//            it.findAll(TreeScope(TreeScope.NONE),p1)
//            println("t1>${p1.value}")
//        }

        try {
            focusedElement?.let {
                println("controlType>${it.controlType}") //50030 Document
                println("name>${it.className} ${it.name}")
                val pr1 = it.getPattern(PatternID.Text.value)
                println("pr1>${pr1.pointer.getWideString(0)}")
                val list = automation.desktopWindows
                list.forEach { ii ->
//                    println("name>${ii.name} ,${ii.className}")
                }
                val window :Window? = automation.getDesktopWindow(Pattern.compile(".*Kotlin.*"))
                println("w=$window")

                val document: Document? = window?.getDocument(Search.getBuilder(0).build())
//                println("document>${document?.name}")



            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    //这种命令只可获取到 notepad++的文本，gettext
    //原代码中的handleMouseAct()函数主要使用了 Windows API 的SendMessage方法来获取文本和选中范围，
    // 这种方法更适用于传统的 Win32 控件（如 Notepad++）。而对于 Word 文档、浏览器等现代应用程序，它们通常使用更复杂的 UI 框架（如 WPF、UWP），
    // 这些框架可能不会通过SendMessage响应文本获取请求。
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

//                    val window = automation.getDesktopWindow("*c1.txt - 记事本")
                    if (true) {
//                        println("w》${window.name} ${window.isModal}")
//                        val editBox = window.getEditBox(Search.getBuilder(0).build())

//                        println("eb>${editBox.frameworkId} ")
                        val p1 = PointerByReference()
                        val i1 = focusedElement.element.getCurrentFrameworkId(p1)
                        when (p1.value.getWideString(0)) {
                            "Win32" -> println("传统Win32控件，优先LegacyIAccessible") //notepad
                            "WPF" -> println(".NET控件，尝试TextPattern")
                            "XAML" -> println("UWP控件，需特殊处理")
                        }


                        val fhwnd = user32.GetForegroundWindow()
                        val processId = IntByReference()
                        val targetThreadId = user32.GetWindowThreadProcessId(fhwnd, processId)
                        val currentThreadId = Kernel32.INSTANCE.GetCurrentThreadId()

                        var attatched = false
                        if (targetThreadId != currentThreadId) {
                            attatched = user32.AttachThreadInput(
                                WinDef.DWORD(currentThreadId.toLong()),
                                WinDef.DWORD(targetThreadId.toLong()), true
                            )
                        }

                        val hwnd = ExUser32.INSTANCE.GetFocus()
                        println("hwnd=$hwnd $attatched")//不是拿当前窗口的句柄，应该是焦点元素控件

                        hwnd?.let {
                            var textLength = user32.SendMessage(
                                hwnd, 0x000E,
//                                    WinDef.WPARAM(0), LPARAM(0)
                                null, null
                            ).toInt()
                            println("textLength>" + textLength)
//                                user32.AttachThreadInput()
                            if (textLength > 0) {
//                                    val textBuffer = CharArray(textLength + 1)
//                                    val pointer =
//                                        Native.malloc((textLength + 1) * Native.WCHAR_SIZE.toLong())
                                textLength += 1
                                val buffer = Memory(textLength * 2L)

                                user32.SendMessage(
                                    hwnd,
                                    0x000D,
                                    WinDef.WPARAM((textLength).toLong()),
                                    LPARAM(Pointer.nativeValue(buffer))
                                )
//                                    val fullText = buffer.getString(0, "UTF-16LE")
                                val fullText = buffer.getWideString(0)
                                println("fullTxt>$fullText")
                                var selStart = IntByReference()
                                var selEnd = IntByReference()
                                user32.SendMessage(
                                    hwnd, 0xB0,
                                    WinDef.WPARAM(Pointer.nativeValue(selStart.pointer)),
                                    LPARAM(Pointer.nativeValue(selEnd.pointer))
                                )
                                val start = selStart.value
                                val end = selEnd.value
                                if (start >= 0 && end > start && end <= fullText.length) {
                                    println("result>>${fullText.substring(start, end)}")
//                                        val mText=String(fullText.substring(start,end).toByteArray(StandardCharsets.UTF_16LE),StandardCharsets.UTF_8)
                                }

                            }
                        }
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }

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


    // **扩展 User32 接口，添加 GetFocus() 方法**
    interface ExUser32 : User32 {
        fun GetFocus(): WinDef.HWND?

        companion object {
            val INSTANCE: ExUser32 = Native.load("user32", ExUser32::class.java)
        }
    }

}

