package com.hwj.ai.except

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

actual class ClipboardHelper {

    private val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    actual fun copyToClipboard(text: String) {
        val stringSelection = StringSelection(text)
        clipboard.setContents(stringSelection, null)
    }


    actual fun readFromClipboard(): String? {
        return clipboard.getData(DataFlavor.stringFlavor) as? String
    }
}