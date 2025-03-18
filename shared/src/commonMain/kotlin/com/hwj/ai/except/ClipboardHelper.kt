package com.hwj.ai.except

expect class ClipboardHelper {
    fun copyToClipboard(text:String)
    fun readFromClipboard():String?
}