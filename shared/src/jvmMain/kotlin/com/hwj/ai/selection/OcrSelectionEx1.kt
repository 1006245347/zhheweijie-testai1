//package com.hwj.ai.selection
//
//import org.bytedeco.leptonica.global.leptonica
//import org.bytedeco.tesseract.TessBaseAPI
//import java.awt.Rectangle
//import java.awt.Robot
//import java.io.File
//import javax.imageio.ImageIO
//
//fun OcrSelectionEx1():String? {
//
//   return recognizeTxtFromScreen(100,100,800,800)
//}
//
//// 准确度有点差，还要配环境变量，下载语言包
////怎么解决多行截图矩形框跟选中区域的问题
//
//fun recognizeTxtFromScreen(x: Int, y: Int, width: Int, height: Int): String? {
//    val screenRect = Rectangle(x, y, width, height)
//    val screenImage = Robot().createScreenCapture(screenRect)
//    val cacheDir = getPlatformCacheImgDir()
//    if (!cacheDir.exists()) cacheDir.mkdirs()
//    val tempFile = File(cacheDir, "ocr_${System.currentTimeMillis()}.png")
//    ImageIO.write(screenImage, "PNG", tempFile)
//
//    // 设置 TESSDATA_PREFIX 环境变量 D:\androidstudy\testai1\tessdata
//    val tessDataDir = "D:\\androidstudy\\testai1\\tessdata" // 替换为实际的 tessdata 目录路径
//    System.setProperty("TESSDATA_PREFIX", tessDataDir)
//
//    val tess = TessBaseAPI()
//    tess.Init(tessDataDir, "eng+chi_sim") //中英
//    val pix = leptonica.pixRead(tempFile.absolutePath)
//    tess.SetImage(pix)
//
//    val text = tess.GetUTF8Text()?.string ?: ""
//
//    tess.End()
//    tempFile.delete()
//
//    println("ocr>> $text")
//    return text
//}
//
//private fun getPlatformCacheImgDir(): File {
//    val osName = System.getProperty("os.name").lowercase()
//    return if (osName.contains("mac")) {
//        File(System.getProperty("user.home"), "Library/Caches/com.hwj.ai.capture/ocr")
//    } else if (osName.contains("win")) {
//        File(System.getenv("LOCALAPPDATA"), "com.hwj.ai.capture/cache/ocr")
//    } else {
//        File(System.getProperty("user.home"), ".cache/com.hwj.ai.capture/ocr")
//    }
//}