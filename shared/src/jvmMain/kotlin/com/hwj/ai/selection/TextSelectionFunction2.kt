package com.hwj.ai.selection

import com.hwj.ai.global.printD
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.Psapi
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HINSTANCE
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinDef.LRESULT
import com.sun.jna.platform.win32.WinDef.POINT
import com.sun.jna.platform.win32.WinDef.WPARAM
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.platform.win32.WinUser.HHOOK
import com.sun.jna.platform.win32.WinUser.HOOKPROC
import com.sun.jna.platform.win32.WinUser.MSG
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import org.jetbrains.skia.impl.NativePointer

object GlobalMouseHook2 {
    private var hHook: WinUser.HHOOK? = null

    private val user32 = User32.INSTANCE

//    private val user32 = EUser32.INSTANCE  //是这玩意不行
   private val kernel32 = Kernel32.INSTANCE
    private val psapi = Psapi.INSTANCE
    private const val WM_LBUTTONDOWN = 0x0201
    private const val WM_LBUTTONUP = 0x0202

    // 手动定义缺少的常量
    var ES_MULTILINE: Int = 0x0004
    var EM_GETSELSTART: Int = 0x00B0
    var EM_GETSELEND: Int = 0x00B1
    var EM_GETSELTEXT: Int = 0x00B2
    var WM_GETTEXTLENGTH: Int = 0x000E
    var WM_GETTEXT: Int = 0x000D

    private var startPoint: WinDef.POINT? = null
    private var endPoint:Long=0

    fun start() {
        val mouseProc = WinUser.LowLevelMouseProc { nCode, wParam, lParam ->
            if (nCode >= 0) {
                val wParamInt = wParam.toInt()
                //扩展鼠标事件检测
                if (wParamInt == WM_LBUTTONDOWN) {
                    startPoint = lParam.pt
                } else if (wParamInt == WM_LBUTTONUP) {
//                printD("鼠标左键抬起，尝试获取前台窗口信息 ${lParam.pt.y}")
                    printD("mouse left up,find window info>${lParam.pt.y}")
                    fetchForegroundAppInfo()

//                    val point = POINT()
//                    user32.GetCursorPos(point)
//                    val hwnd = user32.WindowFromPoint(point)
//                    if (null != hwnd) {
//                        val selected = getTxt2(hwnd)
//                    }
                }
            }
         endPoint=  Pointer.nativeValue(lParam.pointer)
            user32.CallNextHookEx(
                hHook,
                nCode,
                wParam,
                LPARAM(endPoint)
            ) // Found: WinUser.MSLLHOOKSTRUCT! Required: WinDef.LPARAM!
        }

        hHook = user32.SetWindowsHookEx(
            WinUser.WH_MOUSE_LL,
            mouseProc,
            kernel32.GetModuleHandle(null),
            0
        )

        if (hHook == null) {
            println("Hook install failed>")
            return
        }
        println("hook install success!")

        // 必须阻塞消息循环，否则 Hook 会立即退出
        val msg = WinUser.MSG()
        while (user32.GetMessage(msg, null, 0, 0) != 0) {
            user32.TranslateMessage(msg)
            user32.DispatchMessage(msg)
        }

    }

    fun stop() {
        hHook?.let {
            user32.UnhookWindowsHookEx(it)
            println("Hook uninstall>")
        }
    }

    private fun fetchForegroundAppInfo() {
        //获取当前前台窗口的句柄
        val hwnd = user32.GetForegroundWindow()
        if (hwnd == null) {
            println("!!can not find foreground app info ")
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
        val hProcess = kernel32.OpenProcess(
            WinNT.PROCESS_QUERY_INFORMATION or WinNT.PROCESS_VM_READ,
            false, pid
        )

        val exePath = CharArray(1024)
        //获取进程对应的可执行文件路径
        psapi.GetModuleFileNameExW(hProcess, null, exePath, 1024)
        kernel32.CloseHandle(hProcess)

        println("application info：")
        println("title: $title")
        println("️ process ID: $pid")
        println("application path: ${String(exePath).trim()}")
    }

//    private fun getTxt2(hwnd: WinDef.HWND): String? {
//        val style = user32.GetWindowLong(hwnd, WinUser.GWL_STYLE)
//        if ((style and (ES_MULTILINE) )!=0){  //多行
//            val selStart = IntByReference()
//
//            val start = user32.SendMessage(hwnd, EM_GETSELSTART, WPARAM(0), LPARAM(0))
//            val end = user32.SendMessage(hwnd, EM_GETSELEND, WPARAM(0),LPARAM(0))
//            if (start!=end){
//                val buffer = CharArray(end.toInt() - start.toInt() + 1)
////                user32.SendMessage(hwnd, EM_GETSELTEXT, WPARAM(0),textBuffer)
//                val bufferMemory = Memory(buffer.size * 2L) // 每个字符占2字节
//
//                bufferMemory.write(0, buffer.map { it.code.toByte() }.toByteArray(), 0, buffer.size)
//                user32.SendMessage(hwnd, EM_GETSELTEXT, 0, WinDef.LPARAM(bufferMemory.pointer.nativeValue))
//                bufferMemory.read(0, buffer.map { it.code.toByte() }.toByteArray(), 0, buffer.size)
//            }
//        }else{//单行文本框
//
//        val len = user32.SendMessage(hwnd,WinUser.WM_GE)
//        }
//
//    }


//    private fun fetchSelectedText() {
//        val hwnd = user32.GetForegroundWindow()
//        if (hwnd == null) {
//            println("!! can not find foreground app info ")
//            return
//        }
//
//        // 获取窗口类名
//        val className = CharArray(256)
//        user32.GetClassName(hwnd, className, 256)
//        val classNameStr = String(className).trim()
//
//        // 只处理已知的编辑器类名，这里假设是 "Edit"
//        if (classNameStr == "Edit") {
//            val textLength = user32.SendMessage(hwnd, WM_GETTEXTLENGTH, 0, 0).toInt()
//            if (textLength > 0) {
//                val textBuffer = CharArray(textLength + 1)
//                user32.SendMessage(hwnd, WM_GETTEXT, textLength + 1, Pointer.nativeValue(textBuffer.toPointer()))
//                val selectedText = String(textBuffer).trim()
//                println("Selected text: $selectedText")
//            }
//        } else {
//            // 对于其他窗口，可以尝试通过发送 EM_GETSEL 和 EM_GETSELTEXT 消息来获取选中的文本
//            val selStart = IntByReference()
//            val selEnd = IntByReference()
//            user32.SendMessage(hwnd, 0xB0, selStart.pointer, selEnd.pointer) // EM_GETSEL
//            val selLength = selEnd.value - selStart.value
//            if (selLength > 0) {
//                val textBuffer = CharArray(selLength + 1)
//                val mem = Memory((selLength + 1) * 2L)
//                user32.SendMessage(hwnd, 0xB2, selStart.value, mem.peer) // EM_GETSELTEXT
//                mem.read(0, textBuffer, 0, selLength + 1)
//                val selectedText = String(textBuffer).trim()
//                println("Selected text: $selectedText")
//            }
//        }
//    }

//    fun main() = runBlocking {
//        println("🚀 启动全局鼠标监听")
//        withContext(Dispatchers.IO) {
//            GlobalMouseHook.start()
//        }
//    }
}


//fun observeTextSelection(coroutineScope: CoroutineScope): Flow = callbackFlow {
//    val callback = TextSelectionCallback { selection ->
//        trySend(selection)
//    }
//    registerCallback(callback)
//    awaitClose { unregisterCallback(callback) }
//}.flowOn(Dispatchers.IO)

interface EUser32 : StdCallLibrary, WinUser, WinNT {
    companion object {
        val INSTANCE: EUser32 = Native.load("user32", EUser32::class.java)
    }

    fun GetCursorPos(point: POINT): Boolean
    fun WindowFromPoint(point: POINT): WinDef.HWND
    fun CallNextHookEx(hhk: HHOOK?, nCode: Int, wParam: WPARAM, lparam: LPARAM): WinDef.LRESULT
    fun SetWindowsHookEx(idHook: Int, lpfn: HOOKPROC, hMod: HINSTANCE, dwThread: Int): HHOOK
    fun GetMessage(lpMsg: MSG, hwnd: HWND?, wMsgFilterMin: Int, wMsgFilterMax: Int): Int
    fun GetForegroundWindow(): HWND
    fun TranslateMessage(lpMsg: MSG): Boolean
    fun DispatchMessage(lpMsg: MSG): LRESULT
    fun UnhookWindowsHookEx(hhk: HHOOK): Boolean
    fun GetWindowText(hWnd: HWND, lpString: CharArray, nMaxCount: Int): Int
    fun GetWindowThreadProcessId(hWnd: HWND, lpdwProcessId: IntByReference): Int
//    fun GetWindowLong(hWnd: HWND, index: Int): Int
//    fun SendMessage():Int
}