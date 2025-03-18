package com.hwj.ai.except

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ClipboardHelper(private val context: Context) {

    actual fun copyToClipboard(text: String) {
        val chunkSize = 2 * 1020 * 1024 //2mb
        val safeTxt = if (text.length > chunkSize) {
            text.substring(0, chunkSize)
        } else text
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copy", safeTxt)
        clipboard.setPrimaryClip(clip)
    }

    actual fun readFromClipboard(): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        return clip?.getItemAt(0)?.text.toString()
    }

}