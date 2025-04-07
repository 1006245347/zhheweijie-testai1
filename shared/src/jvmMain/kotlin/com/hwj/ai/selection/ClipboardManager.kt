package com.hwj.ai.selection

import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.ClipboardOwner
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.File
import javax.swing.SwingUtilities

class ClipboardManager() : ClipboardOwner {

    val clipboard = Toolkit.getDefaultToolkit().systemClipboard

    //主线程恢复
    fun restoreClipFile(files: List<File>?) {
        if (files.isNullOrEmpty())return
        val transferable = object : Transferable {
            override fun getTransferDataFlavors(): Array<DataFlavor> {
//            return new DataFlavor[]{DataFlavor.javaFileListFlavor};
                return arrayOf(DataFlavor.javaFileListFlavor)
            }

            override fun isDataFlavorSupported(p0: DataFlavor?): Boolean {
                return p0 == DataFlavor.javaFileListFlavor
            }

            override fun getTransferData(p0: DataFlavor?): Any {
                if (isDataFlavorSupported(p0)) {
                    return files
                }
                throw UnsupportedFlavorException(p0)
            }
        }
        // 先清空剪贴板现有内容
//        clipboard.setContents(object : Transferable {
//            override fun getTransferDataFlavors(): Array<DataFlavor> {
//                return emptyArray()
//            }
//
//            override fun isDataFlavorSupported(flavor: DataFlavor?): Boolean {
//                return false
//            }
//
//            override fun getTransferData(flavor: DataFlavor?): Any {
//                throw UnsupportedFlavorException(flavor)
//            }
//        }, null)
//        println("restoreClipFile>")
        clipboard.setContents(transferable, this)//ClipboardOwner
        try{
            val readData= clipboard.getContents(null)
            if (readData!=null&&
                readData.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
                val readFiles=readData.getTransferData(DataFlavor.javaFileListFlavor) as List<File>?
                if (readFiles.isNullOrEmpty()){
                    println("failed write to clip！！！")
                }else{
                    println("suc write!")
                }
            }
        }catch (e:Exception){
            println("failed write to clip！！！")
        }
    }

    override fun lostOwnership(p0: Clipboard?, p1: Transferable?) {
        println("lostOwnership>>${p1?.isDataFlavorSupported(DataFlavor.javaFileListFlavor)}")
        p1?.let {
            repeat(1) { //说是被其他应用锁住，多试几次.不应该必须执行几次，没有判断
                if (p1.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    try {
                        SwingUtilities.invokeLater { //主线程

                            val list =
                                p1.getTransferData(DataFlavor.javaFileListFlavor) as List<File>?
                            if (!list.isNullOrEmpty()) {
//                                clear()
                                //忽然发现不备份文件，所有识别文本都是正常
                                restoreClipFile(list)//换主线程
                            }
                            println("lost-list>${list?.size}")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Thread.sleep(50)
                    }
                }

            }
        }
    }

    fun fetchClipFile(): List<File>? {
        val transferable = clipboard.getContents(null)
        if (null != transferable && transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            try { //标准做法
                return transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<File>?
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

//        try {//非常规做法
//            val more = clipboard.getData(DataFlavor.javaFileListFlavor) as List<File>?
//            if (more != null) {
//                return more
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        return null
    }


    fun backupClipTxt(): String? {
        val contents = clipboard.getContents(null)
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return contents.getTransferData(DataFlavor.stringFlavor) as String
        }
        return null
    }

    fun restoreClipboardTxt(text: String) {
        return try {
            val string = StringSelection(text)
            clipboard.setContents(string, this)
        } catch (e: Exception) {

        }
    }


    fun clear() {
        try {
            clipboard.setContents(StringSelection(""), this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}