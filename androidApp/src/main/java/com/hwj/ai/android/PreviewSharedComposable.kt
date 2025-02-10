package com.hwj.ai.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hwj.ai.test.testPreview1


/**
 * @author by jason-何伟杰，2025/2/10
 * des:@Preview是Android特有的，在/shared模块下无法预览composable函数，
 * 所以单独创建一个PreviewSharedComposable.kt文件来预览
 */
@Preview
@Composable
fun PreviewSharedComposable() {
    PreviewMobile()
//    PreviewDesktop()
}

//预览手机端界面
@Composable
fun PreviewMobile() {
    testPreview1()
}

//预览桌面端界面
@Composable
fun PreviewDesktop() {

}