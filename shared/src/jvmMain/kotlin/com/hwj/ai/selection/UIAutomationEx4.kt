package com.hwj.ai.selection

import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.COM.COMUtils
import com.sun.jna.platform.win32.COM.Unknown
import com.sun.jna.platform.win32.Guid
import com.sun.jna.platform.win32.Ole32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WTypes
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.POINT
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.ptr.PointerByReference
import com.sun.jna.win32.StdCallLibrary


/**
 * @author by jason-何伟杰，2025/3/26
 * des:划词
 */
fun UIAutomationEx4() {

    Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED)

    // 获取鼠标所在点
    val point = WinDef.POINT()
    User32.INSTANCE.GetCursorPos(point)
//MyUser32.INSTANCE.GetCursorPos(point)
//    val hwnd = User32.INSTANCE.WindowFromPoint(point)//要自己扩展接口，内部没有暴露
    val hwnd = MyUser32.INSTANCE.WindowFromPoint(point)
    printD("window>$hwnd")

    // 创建 UIAutomation 实例
    val pAutomation = PointerByReference()
    val hr = Ole32.INSTANCE.CoCreateInstance(
        CLSID_CUIAutomation,
        null,
        WTypes.CLSCTX_INPROC_SERVER,
        IID_IUIAutomation,
        pAutomation
    )
    // ! ?反了
    check(!COMUtils.SUCCEEDED(hr.toInt())) { "UIAutomation 创建失败" }

    val automation = IUIAutomation(pAutomation.value)

    // 通过鼠标坐标获取 AutomationElement
    val elementRef = PointerByReference()
    val pt = WinDef.POINT.ByValue().apply {
        x = point.x
        y = point.y
    }
    automation.ElementFromPoint(pt, elementRef)
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
            printD("选中文本：${textRef.value.getWideString(0)}")
        } else {
            printD("未检测到选中文本")
        }
    } else {
        printD("当前元素不支持 TextPattern")
    }

    Ole32.INSTANCE.CoUninitialize()
}

fun workNewThread(block: (String?) -> Unit) {
    Thread {
        Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_APARTMENTTHREADED)
        try {
//            block(
////                captureSelectedTextUnderCursor()
//            )
        }finally {
            Ole32.INSTANCE.CoUninitialize()
        }

    }.start()
}

//要夸进程使用
//fun captureSelectedTextUnderCursor(): String? {
////    Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_APARTMENTTHREADED)
//
//    // 获取鼠标所在点
//    val mousePos = WinDef.POINT()
//    MyUser32.INSTANCE.GetCursorPos(mousePos)
//    printD("cursor>${mousePos.x} ${mousePos.y}")
//
////    val hwnd = User32.INSTANCE.WindowFromPoint(point)//要自己扩展接口，内部没有暴露
//    val hwnd = MyUser32.INSTANCE.WindowFromPoint(mousePos)
//    if (hwnd == null ) {
//
//        printD("window handle>$hwnd") //句柄没有
//        return null
//    }
//
//
//
//    // 创建 UIAutomation 实例
//    val pAutomation = PointerByReference()
//    val hr = Ole32.INSTANCE.CoCreateInstance(
//        CLSID_CUIAutomation,
//        null,
//        WTypes.CLSCTX_INPROC_SERVER,
//        IID_IUIAutomation,
//        pAutomation
//    )
//
////    check(COMUtils.SUCCEEDED(hr.toInt())) { "UIAutomation 创建失败" }
//    if (!COMUtils.SUCCEEDED(hr.toInt())) {
//        printD("UIAutomation install failed")
//        return null
//    }
//
//    val automation = IUIAutomation(pAutomation.value)
//
//    // 通过鼠标坐标获取 AutomationElement
//    val elementRef = PointerByReference()
////    val pt = WinDef.POINT.ByValue().apply {
////        x = point.x
////        y = point.y
////    }
////    automation.ElementFromPoint(pt, elementRef)
//    try {
//
//    automation.ElementFromPoint(mousePos, elementRef)
//    }catch (e:Exception){
//        printE(e)
//        return null
//    }
//    if (elementRef.value == Pointer.NULL) {
//        printD("ElementFromPoint get failed")
//        return null
//    }
//    val element = IUIAutomationElement(elementRef.value)
//
//    // 尝试获取 TextPattern
//    val patternRef = PointerByReference()
//    val patternHr = element.GetCurrentPattern(UIA_PatternIds.UIA_TextPatternId, patternRef)
//    if (COMUtils.SUCCEEDED(patternHr.toInt())) {
//        val textPattern = IUIAutomationTextPattern(patternRef.value)
//
//        val selectionRef = PointerByReference()
//        textPattern.GetSelection(selectionRef)
//        val selectionArray = IUIAutomationTextRangeArray(selectionRef.value)
//        val length = selectionArray.getLength()
//
//        if (length > 0) {
//            val textRange = selectionArray.GetElement(0)
//            val textRef = PointerByReference()
//            textRange.GetText(-1, textRef)
//            printD("selected text：${textRef.value.getWideString(0)}")
//            return textRef.value.getWideString(0)
//        } else {
//            printD("find not thing")
//        }
//    } else {
//        printD("do not support TextPattern")
//    }
//
//    return null
//}

// CLSID for CUIAutomation
val CLSID_CUIAutomation = Guid.CLSID("{FF48DBA4-60EF-4201-AA87-54103EEF594E}")
val IID_IUIAutomation = Guid.IID("{30CBE57D-D9D0-452A-AB13-7AC5AC4825EE}")

// UIA Pattern IDs
object UIA_PatternIds {
    const val UIA_TextPatternId = 10014
}

//// COM 接口封装（关键！）
//class IUIAutomation(pointer: Pointer) : Unknown(pointer) {
//    fun ElementFromPoint(pt: WinDef.POINT.ByValue, element: PointerByReference): WinNT.HRESULT =
//        _invokeNativeObject(
//            3,
//            arrayOf(this.pointer, pt, element),
//            WinNT.HRESULT::class.java
//        ) as WinNT.HRESULT
//}
//
//class IUIAutomationElement(pointer: Pointer) : Unknown(pointer) {
//    fun GetCurrentPattern(patternId: Int, patternObject: PointerByReference): WinNT.HRESULT =
//        _invokeNativeObject(
//            7,
//            arrayOf(this.pointer, patternId, patternObject),
//            WinNT.HRESULT::class.java
//        ) as WinNT.HRESULT
//}
//
//class IUIAutomationTextPattern(pointer: Pointer) : Unknown(pointer) {
//    fun GetSelection(selection: PointerByReference): WinNT.HRESULT =
//        _invokeNativeObject(
//            3,
//            arrayOf(this.pointer, selection),
//            WinNT.HRESULT::class.java
//        ) as WinNT.HRESULT
//}
//
//class IUIAutomationTextRangeArray(pointer: Pointer) : Unknown(pointer) {
//    fun getLength(): Int = _invokeNativeInt(3, arrayOf(this.pointer))
//    fun GetElement(index: Int): IUIAutomationTextRange {
//        val pRange = PointerByReference()
//        _invokeNativeObject(4, arrayOf(this.pointer, index, pRange), WinNT.HRESULT::class.java)
//        return IUIAutomationTextRange(pRange.value)
//    }
//}
//
//class IUIAutomationTextRange(pointer: Pointer) : Unknown(pointer) {
//    fun GetText(maxLength: Int, text: PointerByReference): WinNT.HRESULT =
//        _invokeNativeObject(
//            8,
//            arrayOf(this.pointer, maxLength, text),
//            WinNT.HRESULT::class.java
//        ) as WinNT.HRESULT
//}

//interface MyUser32 : User32 {
//    fun WindowFromPoint(pointer: WinDef.POINT): WinDef.HWND
//
//    companion object {
//        val INSTANCE: MyUser32 = Native.load("user32", MyUser32::class.java)
//    }
//}


interface MyUser32 : StdCallLibrary {

    companion object {
        val INSTANCE: MyUser32 = Native.load("user32", MyUser32::class.java)
    }

    fun GetCursorPos(point: POINT): Boolean
    fun WindowFromPoint(point: POINT): WinDef.HWND
}