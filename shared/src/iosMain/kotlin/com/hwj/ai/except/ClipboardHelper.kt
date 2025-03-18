package com.hwj.ai.except

import platform.UIKit.UIPasteboard

actual class ClipboardHelper {


    actual fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string=text
    }

    actual fun readFromClipboard(): String? {
        return UIPasteboard.generalPasteboard.string()
    }

}