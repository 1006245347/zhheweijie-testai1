package com.hwj.ai.selection

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.COM.util.Factory
import com.sun.jna.platform.win32.COM.util.IComInterface
import com.sun.jna.platform.win32.COM.util.annotation.ComObject
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty
import com.sun.jna.platform.win32.Ole32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.ptr.PointerByReference

// ========== 定义 UI Automation 的 COM 接口 ==========


// IUIAutomation 接口（UI Automation 入口）
interface IUIAutomation : IComInterface {
    @ComMethod
    fun ElementFromPoint(pt: WinDef.POINT, element: PointerByReference): Int
}

// IUIAutomationElement（UI 元素）
interface IUIAutomationElement : IComInterface {
    @ComMethod
    fun GetCurrentPattern(patternId: Int, patternObject: PointerByReference): Int
}

// IUIAutomationTextPattern（文本模式）
interface IUIAutomationTextPattern : IComInterface {
    @ComMethod
    fun GetSelection(textRange: PointerByReference): Int
}

// IUIAutomationTextRange（文本范围）
interface IUIAutomationTextRange : IComInterface {
    @ComMethod
    fun GetText(maxLength: Int, text: PointerByReference): Int
}

// 定义 UI Automation COM 对象（等价于 `CUIAutomation`）
@ComObject(clsId = "{FF48DBA4-60EF-4201-AA87-54103EEF594E}") // UI Automation COM CLSID
interface CUIAutomation : IUIAutomation

// UI Automation 常量
const val UIA_TextPatternId = 10014  // 文本模式 ID

/**
 * **获取鼠标所在控件的选中文字**
 */
fun getSelectedTextUsingUIAutomation(hwnd: WinDef.HWND): String {
    // **1. 初始化 COM**
    Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED)
    val factory = Factory() // JNA COM 工厂（用于创建 COM 对象）

    try {
        // **2. 创建 UIAutomation COM 实例**
        val automation = factory.createObject(CUIAutomation::class.java) as IUIAutomation

        // **3. 获取鼠标当前位置**
        val point = WinDef.POINT()
        User32.INSTANCE.GetCursorPos(point)

        // **4. 通过鼠标位置找到 UIAutomationElement**
        val pElement = PointerByReference()
        val hrElement = automation.ElementFromPoint(point, pElement)
        if (hrElement != 0 || pElement.value == Pointer.NULL) {
            println("❌ 无法通过 UI Automation 获取 UI 元素")
            return ""
        }
        val element = factory.createProxy(IUIAutomationElement::class.java, pElement.value)

        // **5. 检查元素是否支持 TextPattern**
        val pTextPattern = PointerByReference()
        val hrPattern = element.GetCurrentPattern(UIA_TextPatternId, pTextPattern)
        if (hrPattern != 0 || pTextPattern.value == Pointer.NULL) {
            println("❌ 当前 UI 元素不支持 TextPattern")
            return ""
        }
        val textPattern = factory.createProxy(IUIAutomationTextPattern::class.java, pTextPattern.value)

        // **6. 获取选中文本范围**
        val pTextRanges = PointerByReference()
        val hrRanges = textPattern.GetSelection(pTextRanges)
        if (hrRanges != 0 || pTextRanges.value == Pointer.NULL) {
            println("❌ 无法获取选中区域")
            return ""
        }
        val textRange = factory.createProxy(IUIAutomationTextRange::class.java, pTextRanges.value)

        // **7. 读取选中的文本**
        val pText = PointerByReference()
        val hrText = textRange.GetText(-1, pText)
        if (hrText != 0 || pText.value == Pointer.NULL) {
            println("❌ 无法获取选中文本")
            return ""
        }

        // **8. 转换 COM BSTR 到 Kotlin String**
        return convertBSTRToString(pText.value)
    } finally {
        factory.disposeAll()
        Ole32.INSTANCE.CoUninitialize()
    }
}

/**
 * **转换 COM BSTR 到 Kotlin String**
 */
fun convertBSTRToString(bstrPtr: Pointer): String {
    return bstrPtr.getWideString(0) // JNA 提供的 API
}
