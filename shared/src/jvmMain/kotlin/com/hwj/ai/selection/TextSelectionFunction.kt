package com.hwj.ai.selection

import com.hwj.ai.global.printD
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.Psapi
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.LPARAM
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinUser
import com.sun.jna.ptr.IntByReference

fun WindowsSelection(onTextSelected:(String,String)->Unit){

}

fun MacSelection(onTextSelected:(String,String)->Unit){

}

object GlobalMouseHook{
    private var hHook: WinUser.HHOOK? = null
    private val user32 = User32.INSTANCE
    private val kernel32 = Kernel32.INSTANCE
    private val psapi = Psapi.INSTANCE
    private const val WM_LBUTTONUP = 0x0202


    private val mouseProc = WinUser.LowLevelMouseProc { nCode, wParam, lParam ->
        if(nCode>=0){
            val wParamInt = wParam.toInt()
            if (wParamInt == WM_LBUTTONUP) {
                printD("鼠标左键抬起，尝试获取前台窗口信息 ${lParam.pt.y}")
            fetchForegroundAppInfo()
            }
        }
        user32.CallNextHookEx(hHook, nCode, wParam, LPARAM(Pointer.nativeValue(lParam.pointer))) // Found: WinUser.MSLLHOOKSTRUCT! Required: WinDef.LPARAM!
    }

    fun start() {
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
            println("🛑 Hook 已卸载")
        }
    }

    private fun fetchForegroundAppInfo() {
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
        psapi.GetModuleFileNameExW(hProcess, null, exePath, 1024)
        kernel32.CloseHandle(hProcess)

        println("application info：")
        println("title: $title")
        println("️ process ID: $pid")
        println("application path: ${String(exePath).trim()}")
    }

//    fun main() = runBlocking {
//        println("🚀 启动全局鼠标监听")
//        withContext(Dispatchers.IO) {
//            GlobalMouseHook.start()
//        }
//    }
}