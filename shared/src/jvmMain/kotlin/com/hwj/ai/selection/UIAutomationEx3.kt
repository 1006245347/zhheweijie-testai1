//package com.hwj.ai.selection
//
//import com.hwj.ai.global.printD
//import com.sun.jna.Native
//import com.sun.jna.Pointer
//import com.sun.jna.platform.win32.COM.COMUtils
//import com.sun.jna.platform.win32.COM.IUnknown
//import com.sun.jna.platform.win32.Guid
//import com.sun.jna.platform.win32.Ole32
//import com.sun.jna.platform.win32.User32
//import com.sun.jna.platform.win32.WTypes
//import com.sun.jna.platform.win32.WinDef
//import com.sun.jna.platform.win32.WinDef.HWND
//import com.sun.jna.platform.win32.WinNT
//import com.sun.jna.ptr.PointerByReference
//
//fun TextEx3(){
//
//    Ole32.INSTANCE.CoInitializeEx(Pointer.NULL,Ole32.COINIT_MULTITHREADED)
//
//    // 获取鼠标所在点
//    val point = WinDef.POINT()
//    User32.INSTANCE.GetCursorPos(point)
//
////    val hwnd = User32.INSTANCE.WindowFromPoint(point)//要自己扩展接口，内部没有暴露
//    val hwnd =MyUser32.INSTANCE.WindowFromPoint(point)
//    printD("window>$hwnd")
//
//    val uia: IUIAutomation = createUIAutomation()
//    val elementRef = PointerByReference()
//    uia.ElementFromPoint(tagPOINT(point), elementRef)
//    val element = IUIAutomationElement(elementRef.value)
//
//    println("控件信息: " + getElementInfo(element))
//
//    // 获取TextPattern
//    val patternRef = PointerByReference()
//    val hr = element.GetCurrentPattern(IUIAutomationTextPattern.IID, patternRef)
//    if (COMUtils.SUCCEEDED(hr)) {
//        val textPattern = com.hwj.ai.selection.IUIAutomationTextPattern(patternRef.value)
//        val selectionRef = PointerByReference()
//        textPattern.GetSelection(selectionRef)
//        val selection = IUIAutomationTextRangeArray(selectionRef.value)
//        if (selection.length > 0) {
//            val textRange = selection.get(0)
//            val textPtr = PointerByReference()
//            textRange.GetText(-1, textPtr)
//            println("选中文本: ${textPtr.value.getWideString(0)}")
//        } else {
//            println("未检测到选中文本")
//        }
//    } else {
//        println("当前控件不支持 TextPattern")
//    }
//
//    Ole32.INSTANCE.CoUninitialize()
//}
//
//fun createUIAutomation(): IUIAutomation {
//    val pAutomation = PointerByReference()
//    val hr = Ole32.INSTANCE.CoCreateInstance(
//        CLSID_CUIAutomation,
//        null,
//        WTypes.CLSCTX_INPROC_SERVER,
//        IUIAutomation.IID,
//        pAutomation
//    )
//    if (COMUtils.FAILED(hr)) throw RuntimeException("创建UIAutomation失败")
//// return  object :IUIAutomation(){}
//    return com.hwj.ai.selection.IUIAutomation(pAutomation.value)
//}
//
//fun getElementInfo(element: IUIAutomationElement): String {
//    val name = WTypes.BSTRByReference()
//    element.get_CurrentName(name)
//    return "Name: ${name.value}, ControlType: ${element.get_CurrentControlType()}"
//}
//
//
//
//// --- 关键 COM 接口封装 ---
//
//val CLSID_CUIAutomation1 = Guid.CLSID("{FF48DBA4-60EF-4201-AA87-54103EEF594E}")
//
//interface IUIAutomation : IUnknown {
//    companion object {
//        val IID = Guid.IID("{30CBE57D-D9D0-452A-AB13-7AC5AC4825EE}")
//    }
//    fun ElementFromPoint(pt: WinDef.POINT, element: PointerByReference): WinNT.HRESULT
//}
//
//interface IUIAutomationElement : IUnknown {
//    fun GetCurrentPattern(patternId: Guid.GUID, pattern: PointerByReference): WinNT.HRESULT
//    fun get_CurrentName(name: WTypes.BSTRByReference): WinNT.HRESULT
//    fun get_CurrentControlType(): Int
//}
//
//interface IUIAutomationTextPattern : IUnknown {
//    companion object {
//        val IID = Guid.IID("{32eba289-3583-42c9-9c59-3b2dd59f1b56}")
//    }
//    fun GetSelection(selection: PointerByReference): WinNT.HRESULT
//}
//
//interface IUIAutomationTextRangeArray : IUnknown {
//    val length: Int
//    fun get(index: Int): IUIAutomationTextRange
//}
//
//interface IUIAutomationTextRange : IUnknown {
//    fun GetText(maxLength: Int, text: PointerByReference): WinNT.HRESULT
//}
//
//fun tagPOINT(pt: WinDef.POINT) = WinDef.POINT.ByValue().also {
//    it.x = pt.x
//    it.y = pt.y
//}
//
//
//interface  MyUser32:User32{
//    fun WindowFromPoint (pointer: WinDef.POINT):HWND
//
//    companion object{
//        val INSTANCE : MyUser32 =Native.load("user32",MyUser32::class.java)
//    }
//}