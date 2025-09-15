package com.hwj.ai.selection

import com.hwj.ai.global.Event
import com.hwj.ai.global.EventHelper
import com.sun.jna.Memory
import com.sun.jna.Native
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
import io.ktor.utils.io.core.toByteArray
import mmarquee.automation.ControlType
import mmarquee.automation.Element
import mmarquee.automation.PatternID
import mmarquee.automation.UIAutomation
import mmarquee.automation.controls.Container
import mmarquee.automation.controls.Document
import mmarquee.automation.controls.DocumentPage
import mmarquee.automation.controls.ElementBuilder
import mmarquee.automation.controls.TextBox
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
    private const val WM_LBUTTONDOWN = 0x0201
    private val appBuilder = StringBuilder()
    private val contentBuilder = StringBuilder()
    private var endPoint: Long = 0
    var mousePressedPos = WinDef.POINT() // 记录鼠标按下时的坐标
    var endPressPos = WinDef.POINT()

    private var block1: (String?) -> Unit = {}
    private var block2: (String?) -> Unit = {}

    var isDragging = false

    private val mouseProc6 = WinUser.LowLevelMouseProc { nCode, wParam, lParam ->
        if (nCode >= 0) {
            val wParamInt = wParam.toInt()
            //扩展鼠标事件检测
            if (wParamInt == WM_LBUTTONUP) {
//                printD("鼠标左键抬起，尝试获取前台窗口信息 ${lParam.pt.y}")
//                println("Start--------------mouse left up,find window info> ui=${isMainThread()} ${lParam.pt.y}")
                endPressPos = lParam.pt
//                fetchForegroundAppInfo()
                val d = distanceBetween(mousePressedPos, lParam.pt)
                if (d > 10) { //优化双击
                    isDragging = true
                }

//                handleMouseAct() //直接处理容易卡
            } else if (wParamInt == WM_LBUTTONDOWN) {
                mousePressedPos = lParam.pt
                isDragging = false
            }
        }

        endPoint = Pointer.nativeValue(lParam.pointer)
        user32.CallNextHookEx(
            mouseHook, nCode, wParam, LPARAM(endPoint)
        ) // Found: WinUser.MSLLHOOKSTRUCT! Required: WinDef.LPARAM!
    }

    fun start(useHot: Boolean, appBlock: (String?) -> Unit, contentBlock: (String?) -> Unit) {
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

        //快捷键注册
        val hwnd: WinDef.HWND? = null  //空代表当前进程
        val k_alt = 0x0001
        val k_a = 0x41
        val k_id1 = 1
        if (useHot) {
            if (!user32.RegisterHotKey(hwnd, k_id1, k_alt, k_a)) {
                println("register hot failed>")
            }

            if (!user32.RegisterHotKey(hwnd, 2, 0x0001, 0x42)) {
                println("hot2 failed")
            }
        }

        // 必须阻塞消息循环，否则 Hook 会立即退出
        val msg = WinUser.MSG()
        while (user32.GetMessage(msg, null, 0, 0) != 0) {
            if (msg.message == WinUser.WM_HOTKEY) {
                val hotkeyId = msg.wParam.toInt()
                if (hotkeyId == k_id1) {
                    EventHelper.post(Event.HotKeyEvent(k_id1, System.currentTimeMillis()))
                } else if (hotkeyId == 2) {
                    println("alt b")
                    EventHelper.post(Event.HotKeyEvent(2,System.currentTimeMillis()))
                }
            }
            user32.TranslateMessage(msg)
            user32.DispatchMessage(msg)
        }
    }

    fun stop() {
        releaseHotKey()
        mouseHook?.let { user32.UnhookWindowsHookEx(it) }
    }

    private fun releaseHotKey() {
        user32.UnregisterHotKey(null, 1)
        user32.UnregisterHotKey(null, 2)
    }

    //这种命令只可获取到 notepad++的文本，gettext
    //原代码中的handleMouseAct()函数主要使用了 Windows API 的SendMessage方法来获取文本和选中范围，
    // 这种方法更适用于传统的 Win32 控件（如 Notepad++）。而对于 Word 文档、浏览器等现代应用程序，它们通常使用更复杂的 UI 框架（如 WPF、UWP），
    // 这些框架可能不会通过SendMessage响应文本获取请求。
    fun handleMouseAct() {
        val focusedElement: Element? = automation.focusedElement

        if (focusedElement != null) {
//            val name = focusedElement.name
//            println("Hack  name>$name") //这个文件名?偶然把整个内容也打出来

            try {
                if (isDragging) {
                    //打印所有应用
//                    val pList = automation.desktopObjects
//                    val ss1 = StringBuilder()
//                    pList.forEach { p11 ->
//                        ss1.append("App> ${p11.name},${p11.processId},${p11.className}}")
//                            .append("\n")
//                    }
//                    val wList = automation.desktopWindows
//                    wList.forEach { w11 ->
//                        ss1.append("win> ${w11.name},${w11.processId},${w11.className}}")
//                            .append("\n")
//                    }
//                    block2(ss1.toString())

                    buildTest()
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //notepad 换行时 /r/n为一行
    fun checkByMsg(): String? {
        try {
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
//            println("hwnd=$hwnd $attatched")//不是拿当前窗口的句柄，应该是焦点元素控件

            hwnd?.let {
                var textLength = user32.SendMessage(
                    hwnd, 0x000E,
//                    WinDef.WPARAM(0), LPARAM(0)
                    null, null
                ).toInt()
//                        println("textLength>" + textLength)
                if (textLength > 0) {
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
//                            println("fullTxt>$fullText")
                    //发现会有错位，不准确
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
//                                        val mText=String(fullText.substring(start,end).toByteArray(StandardCharsets.UTF_16LE),StandardCharsets.UTF_8)
                        return fullText.substring(start, end)
//                            .replace("\r\n","\n") //有个图标在好像就不准了位置
//                            .trim()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun buildApps(): MutableList<AppInfoModel> {
        val start = System.currentTimeMillis()
        val appList = mutableListOf<AppInfoModel>()
        val panelList = automation.desktopObjects
        panelList.forEach { p1 ->
            appList.add(AppInfoModel(p1.processId.toString(), p1.name, p1.className))
        }
        val windowList = automation.desktopWindows
        windowList.forEach { w1 ->
            appList.add(AppInfoModel(w1.processId.toString(), w1.name, w1.className))
        }
//        println("costTime> ${System.currentTimeMillis() - start}")
        return appList
    }

    //考虑一个丢弃，还有只处理最后一个
    private fun buildTest() {
        val nowProcessId = automation.focusedElement.processId.toString()
        val apps = buildApps()
//        printList(apps)//Microsoft​ Edge

        for (app in apps) {
            if (app.processId == nowProcessId && app.title.isNotEmpty()) {
//                println("ed> ${app.title}")
                checkApp(app.title)
                break
            }
        }
    }

    fun checkApp(appName: String) {
        var panel: mmarquee.automation.controls.Panel? = null
        var window: mmarquee.automation.controls.Window? = null
        var result: String? = null
        val cType = automation.focusedElement.controlType
        val point1 = mousePressedPos
//        println("controlType>$cType ${PatternID.Text.value} ${PatternID.Text2.value}") //50020 10014 10024

        if (appName.endsWith("Google Chrome")) {
            panel = automation.getDesktopObject(getP("Google Chrome"), 2)
            if (panel != null && "Chrome_WidgetWin_1".equals(panel.className)) {
                result = panel.getDocument(0)?.selection
            }
        } else if (appName.endsWith("Microsoft\u200B Edge") ||
            appName.endsWith("Microsoft Edge")
        ) {
            window = automation.getDesktopWindow(getP("Microsoft\u200B Edge"), 2)
            if (window == null) {
                window = automation.getDesktopWindow(getP("Microsoft Edge"), 2)
            }
            if (window != null && window.className.equals("Chrome_WidgetWin_1")) {
                //50004 Edit
//                println("me>Microsoft\u200B Edge")//Microsoft​ Edge
                result = window.getDocument(0).selection
            }
        } else if (appName.endsWith("Mozilla Firefox")) {
            window = automation.getDesktopWindow(getP("Mozilla Firefox"), 2)
            if (window != null && "MozillaWindowClass".equals(window.className)) {
//                println("fire>${window.getDocument(0).selection} ")
                //这火狐浏览器，点哪个标签index都不认
//                val d1 = findXDocument()
                result = window.getDocument(0)?.selection
            }
        } else if (appName.contains("G平台")) {
            panel = automation.getDesktopObject(getP("G平台"), 2)
            if (panel != null && panel.className.equals("Chrome_WidgetWin_1")) {

                //focused是一个大容器不一定是最小组件
                val c1 = automation.focusedElement.controlType
                if (c1 == ControlType.Group.value) {
                    val b2 = Container(ElementBuilder(automation.focusedElement))
//                    println("b2>${b2.name}") //等同于 focus.name,没用
                    val l1 = b2.getChildren(false)
//                    l1.forEach { item->
//                        println("item>${item.element.controlType}")
//                    }

                    try {
//                    println("point>${point1.x} ${point1.y}") //自定义控件没法绕过pattern的判定 requestAutomationPattern
                        for (c in l1) { //这样以焦点容器内所有的 Text都出来，但是没法筛选哪个？选中内容
                            if (c.element.controlType == ControlType.Text.value) {
                                val b3 = TextBox(ElementBuilder(c.element))
                                if (b3.boundingRectangle.left - 10 < point1.x && b3.boundingRectangle.right + 10 > point1.x
                                    && b3.boundingRectangle.top - 10 < point1.y && b3.boundingRectangle.bottom + 10 > point1.y
                                ) { //当点击位置在控件边距内，判定为当前点击控件
//                                    println("b3c>${b3.boundingRectangle}") //边距还能造成误差，顶
//                                    println("b3->${b3.name} ")
//                                    block2(b3.name)
                                    result = b3.name
                                    break
                                }

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

//                    TreeWalker().v(automation) { s -> result = s }
            }
        } else if (appName.contains("M-AI")) {
            panel = automation.getDesktopObject(getP("M-AI"), 2)
            if (panel != null && panel.className.equals("Chrome_WidgetWin_1")) {
                result = panel.getDocument(0).selection
            }
        } else if (appName.contains("Notepad++")) {
            window = automation.getDesktopWindow(getP("Notepad++"), 2)
            if (window != null && window.className.equals("Notepad++")) {
                result = checkByMsg()
            }
        } else if (appName.endsWith("记事本")) {
            window = automation.getDesktopWindow(getP("记事本"), 2)
            if (window != null && window.className.equals("Notepad")) {
                result = checkByMsg()
            }
        }
//        else if (appName.contains("WPS 文字")) {
//            window = automation.getDesktopWindow(getP("WPS 文字"), 2)
//            if (null != window && window.className.equals("KxWpsPromeMainWindow")) {
////                TreeWalker().v(automation)
//            }
//        }

//        println("find>>  $result")
        result?.let {
            block2(result)
            isDragging = false
        }
    }

    fun findXDocument(): Document? {
        val pr = automation.createControlTypeCondition(ControlType.Document)
        val element = automation.focusedElement.findFirst(TreeScope(TreeScope.DESCENDANTS), pr)
        val c = DocumentPage(ElementBuilder().element(element))
        return c.getDocument(0)
    }

    fun getP(title: String): Pattern {
        return Pattern.compile(".*$title.*")
    }

    //解决COM接口的方式，用jacob.jar?
    fun handleWps() {

    }

    fun bring2Front() {
        val hwnd = user32.FindWindow(null, "hwj-ai-chat")
//        hwnd?.let {
//
//        }
        val code = user32.BringWindowToTop(hwnd) //null是当前进程？ 没用
//        user32.SetForegroundWindow()
        println("flag>$code")
    }

    private fun showContent(text: String) {
//        contentBuilder.clear()
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

