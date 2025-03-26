//package com.hwj.ai.selection
//
//import com.sun.jna.Pointer
//import com.sun.jna.platform.win32.COM.COMUtils
//import com.sun.jna.platform.win32.COM.util.Factory
////import com.sun.jna.platform.win32.COM.util.IComInterface
//import com.sun.jna.platform.win32.COM.util.annotation.ComObject
//import com.sun.jna.platform.win32.COM.util.annotation.ComProperty
//import com.sun.jna.platform.win32.COM.util.annotation.ComMethod
//import com.sun.jna.platform.win32.COM.util.IUnknown
//import com.sun.jna.platform.win32.Ole32
//import com.sun.jna.platform.win32.User32
//import com.sun.jna.platform.win32.WinDef.POINT
//import com.sun.jna.platform.win32.WinDef.HWND
//
//// 以下接口仅为示例，实际中需要完整定义各个 COM 接口
//
//// 定义 UIAutomation 的 COM 接口（部分方法）
//interface IUIAutomation : IUnknown {
//    @ComMethod
//    fun ElementFromPoint(point: POINT, ppElement: Pointer): Int
//
//    // 其他方法……
//}
//
//// 定义 UIAutomationElement 接口
//interface IUIAutomationElement : IUnknown {
//    @ComMethod
//    fun GetCurrentPattern(patternId: Int, ppInterface: Pointer): Int
//
//    // 其他方法……
//}
//
//// 定义 UIAutomationTextPattern 接口
//interface IUIAutomationTextPattern : IUnknown {
//    @ComMethod
//    fun GetSelection(ppRetVal: Pointer): Int
//
//    // 其他方法……
//}
//
//// 定义 UIAutomationTextRange 接口
//interface IUIAutomationTextRange : IUnknown {
//    @ComMethod
//    fun GetText(maxLength: Int, pRetVal: Pointer): Int
//}
//
//// 定义 COM 对象，借助 JNA COMUtil 工具类（需要添加 jna-platform 依赖）
//@ComObject(clsId = "{FF48DBA4-60EF-4201-AA87-54103EEF594E}") // CUIAutomation CLSID
//interface CUIAutomation : IUIAutomation
//
//// 常量，UIA_TextPatternId 为 10014（参考官方文档）
//const val UIA_TextPatternId = 10014
//
///**
// * 利用 UI Automation COM 接口获取鼠标所在控件的选中文字
// */
//fun getSelectedTextUsingUIAutomation(hwnd: HWND): String {
//    // 初始化 COM 库
//    Ole32.INSTANCE.CoInitializeEx(Pointer.NULL, Ole32.COINIT_MULTITHREADED)
//    val factory = Factory() // COM 工厂（确保项目中引入 JNA COMUtil 支持库）
//    try {
//        // 创建 UIAutomation 对象
//        val automation = factory.createObject(CUIAutomation::class.java) as IUIAutomation
//
//        // 获取当前鼠标位置
//        val point = POINT()
//        User32.INSTANCE.GetCursorPos(point)
//
//        // 从鼠标位置获取 UIAutomationElement
//        // 注意：实际调用时需要传入 PointerByReference，这里为了示例直接使用一个 Pointer 对象占位
//        val pElement = Pointer.NULL // 这里需要通过 PointerByReference 获取返回的 element
//        val hrElement = automation.ElementFromPoint(point, pElement)
//        if (COMUtils.FAILED(hrElement) || pElement == Pointer.NULL) {
//            println("无法通过 UIAutomation 获取元素")
//            return ""
//        }
//        // 此处将 pElement 转换为 IUIAutomationElement 接口的实例
//        val element = factory.createProxy(IUIAutomationElement::class.java, pElement)
//
//        // 查询 TextPattern 接口
//        val pTextPattern = Pointer.NULL // 同样需要 PointerByReference
//        val hrPattern = element.GetCurrentPattern(UIA_TextPatternId, pTextPattern)
//        if (COMUtils.FAILED(hrPattern) || pTextPattern == Pointer.NULL) {
//            println("当前元素不支持 TextPattern")
//            return ""
//        }
//        val textPattern = factory.createProxy(IUIAutomationTextPattern::class.java, pTextPattern)
//
//        // 获取当前选中区域（通常可能有多个范围，这里简单处理第一个范围）
//        val pTextRanges = Pointer.NULL // 同样需要通过 PointerByReference 获取文本范围数组
//        val hrRanges = textPattern.GetSelection(pTextRanges)
//        if (COMUtils.FAILED(hrRanges) || pTextRanges == Pointer.NULL) {
//            println("无法获取选中区域")
//            return ""
//        }
//        // 假设 pTextRanges 指向一个 IUIAutomationTextRange 对象
//        val textRange = factory.createProxy(IUIAutomationTextRange::class.java, pTextRanges)
//
//        // 获取文本内容，传入 -1 表示获取全部文本
//        val pText = Pointer.NULL // 需要 PointerByReference 接收返回的 BSTR 字符串
//        val hrText = textRange.GetText(-1, pText)
//        if (COMUtils.FAILED(hrText) || pText == Pointer.NULL) {
//            println("无法获取文本内容")
//            return ""
//        }
//        // 这里需要将 BSTR 转换为 Kotlin String，假设有一个 helper 函数 convertBSTRToString
//        val selectedText = convertBSTRToString(pText)
//        return selectedText
//    } finally {
//        factory.disposeAll()
//        Ole32.INSTANCE.CoUninitialize()
//    }
//}
//
///**
// * 模拟将 COM 返回的 BSTR 字符串转换为 Kotlin String
// * 真实实现需要调用 SysStringLen 和对应的 JNA 方法进行转换
// */
//fun convertBSTRToString(bstrPtr: Pointer): String {
//    // 这里仅作示例，真实实现请参考 JNA 的 BSTR 操作
//    return bstrPtr.getWideString(0)
//}
