package com.hwj.ai.ui.test

import androidx.compose.runtime.Composable

/**
 * @author by jason-何伟杰，2025/6/23
 * des:有时候创建函数定义参数时，各种奇怪闭包对象翻查，这里总结下
 */
@Composable
fun TestFunction() {

    f1(sendMsg = { text ->
        //结果输出
    })

    f2(sendBlock = {
        //do sth
    })
    f3(onWindowChange = { b, s ->

    })
}

@Composable
fun f1(sendMsg: (String?) -> Unit) {
    sendMsg("xx") //进行 传入参
}

@Composable
fun f2(sendBlock: () -> Unit) {
    sendBlock()
}

@Composable
fun f3(onWindowChange: @Composable (Boolean, String?) -> Unit) {
    onWindowChange(true, "s")
}