import com.hwj.ai.global.printD
import com.hwj.ai.global.printList
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.Psapi
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.HHOOK
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.PointerByReference
import io.ktor.utils.io.core.toByteArray
import mmarquee.automation.Element
import mmarquee.automation.PatternID
import mmarquee.automation.UIAutomation
import mmarquee.automation.controls.EditBox
import mmarquee.automation.pattern.Text
import mmarquee.automation.pattern.Value
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

/**
 * @author by jason-何伟杰，2025/3/28
 * des: JNA + UIAutomation
 */
object GlobalMouseHook6 {

    private val automation = UIAutomation.getInstance()
    private var mouseHook: HHOOK? = null
    private const val WM_LBUTTONUP = 0x0202
    private val appBuilder = StringBuilder()
    private val contentBuilder = StringBuilder()

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
        User32.INSTANCE.CallNextHookEx(
            mouseHook, nCode, wParam, LPARAM(Pointer.nativeValue(lParam.pointer))
        ) // Found: WinUser.MSLLHOOKSTRUCT! Required: WinDef.LPARAM!
    }

    fun start(appBlock: (String?) -> Unit, contentBlock: (String?) -> Unit) {
        mouseHook = User32.INSTANCE.SetWindowsHookEx(
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
        while (User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
            User32.INSTANCE.TranslateMessage(msg)
            User32.INSTANCE.DispatchMessage(msg)
        }
    }

    fun stop() {
        mouseHook?.let { User32.INSTANCE.UnhookWindowsHookEx(it) }
    }

    private fun handleMouseAct() {
        try {
//            val window = automation.getDesktopWindow(Pattern.compile("Notepad"))
//            println("w>$window")
//            val panel = automation.desktop//word/els
//            println("panel>${panel.name}")

            val focusedElement: Element? = automation.focusedElement


            if (focusedElement != null) {
                val name = focusedElement.name
                if (name.isNotEmpty()) {
                    println("Hack file name>$name") //这个文件名?偶然把整个内容也打出来
                }
//                println("Hack des>${focusedElement.getFullDescription()}") //not set
//                println("Hack des2>${focusedElement.getProviderDescription()}")
//                println("Hack content is>${focusedElement.isContentElement}")




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

//                try {
//
//                    focusedElement.element
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }


//                val newText = Text(focusedElement)
//                println("select>${newText.selection} ,${newText.text}")
//                val ref1: PointerByReference = focusedElement.getPattern(PatternID.Text.value)
//                println("Hack1>>>>${ref1}")
//                println("Hack2>>>${ref1.pointer.getWideString(0)}")
////                println("Hack2>>>>${patternhr.value.getWideString(0)}")
//
//                val selectRef= PointerByReference()



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

    private fun showContent(text: String) {
        contentBuilder.clear()
        contentBuilder.append(text)
        block2(contentBuilder.toString())
    }

    private fun fetchForegroundAppInfo() {
        val hwnd = User32.INSTANCE.GetForegroundWindow()
        if (hwnd == null) {
            println("Hack failed! can not find foreground app info ")
            return
        }

        // 获取窗口标题
        val windowText = CharArray(512)
        User32.INSTANCE.GetWindowText(hwnd, windowText, 512)
        val title = String(windowText).trim()

        // 获取进程 ID
        val pidRef = IntByReference()
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pidRef)
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

