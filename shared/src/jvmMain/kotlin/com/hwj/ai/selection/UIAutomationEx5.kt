package com.hwj.ai.selection

import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.COM.COMUtils
import com.sun.jna.platform.win32.COM.Unknown
import com.sun.jna.platform.win32.Ole32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WTypes
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.ptr.PointerByReference
import kotlin.concurrent.thread

//要夸进程使用
fun fetch5(automation: IUIAutomation): String? {

    // 获取鼠标所在点
    val mousePos = WinDef.POINT()
    val suc = User32.INSTANCE.GetCursorPos(mousePos)
    if (!suc) return null
    printD("mousePos>${mousePos.x} ${mousePos.y}")

    val hwnd = EUser32.INSTANCE.WindowFromPoint(mousePos)
    printD("window hwnd>$hwnd")
    if (hwnd == null) {
        return null
    }

    // 通过鼠标坐标获取 AutomationElement
    val elementRef = PointerByReference()

    try { //这里蹦
        val pp = WinDef.POINT.ByValue(mousePos.x, mousePos.y)
        automation.ElementFromPoint(
            pp,
            elementRef
        ) //Exception in thread "ui-hook" java.lang.Error: Invalid memory access
    } catch (e: Exception) {
        printE(e)
        return null
    }
    if (elementRef.value == Pointer.NULL) {
        printD("ElementFromPoint get failed")
        return null
    }
    val element = IUIAutomationElement(elementRef.value)

    // 尝试获取 TextPattern
    val patternRef = PointerByReference()
    val patternHr = element.GetCurrentPattern(UIA_PatternIds.UIA_TextPatternId, patternRef)
    if (COMUtils.SUCCEEDED(patternHr.toInt())) {
        val textPattern = IUIAutomationTextPattern(patternRef.value)

        val selectionRef = PointerByReference()
        textPattern.GetSelection(selectionRef)
        val selectionArray = IUIAutomationTextRangeArray(selectionRef.value)
        val length = selectionArray.getLength()

        if (length > 0) {
            val textRange = selectionArray.GetElement(0)
            val textRef = PointerByReference()
            textRange.GetText(-1, textRef)
            printD("selected text：${textRef.value.getWideString(0)}")
            return textRef.value.getWideString(0)
        } else {
            printD("find not thing")
        }
    } else {
        printD("do not support TextPattern")
    }

    return null
}


// COM 接口封装（关键！）
class IUIAutomation(pointer: Pointer) : Unknown(pointer) {
    fun ElementFromPoint(pt: WinDef.POINT.ByValue, element: PointerByReference): WinNT.HRESULT =
        _invokeNativeObject(
            3,
            arrayOf(this.pointer, pt, element),
            WinNT.HRESULT::class.java
        ) as WinNT.HRESULT
}

//一个ui元素，如按钮
class IUIAutomationElement(pointer: Pointer) : Unknown(pointer) {
    fun GetCurrentPattern(patternId: Int, patternObject: PointerByReference): WinNT.HRESULT =
        _invokeNativeObject(
            7,
            arrayOf(this.pointer, patternId, patternObject),
            WinNT.HRESULT::class.java
        ) as WinNT.HRESULT
}

class IUIAutomationTextPattern(pointer: Pointer) : Unknown(pointer) {
    fun GetSelection(selection: PointerByReference): WinNT.HRESULT =
        _invokeNativeObject(
            3,
            arrayOf(this.pointer, selection),
            WinNT.HRESULT::class.java
        ) as WinNT.HRESULT
}

class IUIAutomationTextRangeArray(pointer: Pointer) : Unknown(pointer) {
    fun getLength(): Int = _invokeNativeInt(3, arrayOf(this.pointer))
    fun GetElement(index: Int): IUIAutomationTextRange {
        val pRange = PointerByReference()
        _invokeNativeObject(4, arrayOf(this.pointer, index, pRange), WinNT.HRESULT::class.java)
        return IUIAutomationTextRange(pRange.value)
    }
}

class IUIAutomationTextRange(pointer: Pointer) : Unknown(pointer) {
    fun GetText(maxLength: Int, text: PointerByReference): WinNT.HRESULT =
        _invokeNativeObject(
            8,
            arrayOf(this.pointer, maxLength, text),
            WinNT.HRESULT::class.java
        ) as WinNT.HRESULT
}

private fun createUIAutomation(): IUIAutomation {
    val pAutomation = PointerByReference()
    val hr = Ole32.INSTANCE.CoCreateInstance(
        CLSID_CUIAutomation,
        null,
        WTypes.CLSCTX_INPROC_SERVER,
        IID_IUIAutomation,
        pAutomation
    )
    COMUtils.checkRC(hr)
    return IUIAutomation(pAutomation.value)
}

object UIMonitor {

    @Volatile
    private var running = true
    private var workerThread: Thread? = null
    private var automation: IUIAutomation? = null

    fun start(block: (String?) -> Unit) {
        if (workerThread?.isAlive == true) {
            return
        }
        running = true

        workerThread = thread(name = "ui-hook") {
            // 初始化 COM 线程
            val isSuc = Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_APARTMENTTHREADED)
           printD("isSuc>${isSuc.toLong()}")
            automation = createUIAutomation()
            while (running) {
                try {
                    block(fetch5(automation!!))
//                    printD("running?")
                    Thread.sleep(500)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Ole32.INSTANCE.CoUninitialize()
        }
    }

    fun stop() {
        running = false
        workerThread?.join()
    }
}


