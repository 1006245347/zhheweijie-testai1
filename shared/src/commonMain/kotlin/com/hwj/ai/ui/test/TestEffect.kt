package com.hwj.ai.ui.test

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.hwj.ai.global.getMills
import com.hwj.ai.global.printD
import kotlinx.coroutines.delay

private fun study1() {
    /* compose重组的一些必要理解，副作用
    如果某个可组合函数包含对其他可组合函数的调用，这些函数可以按任何顺序运行
    *
    * */
}

@Composable
private fun sideEffect1() {
    //主线程，重组后同步执行，日志、埋点，不能用于耗时
    //强调compose函数重组成功后再指向effect
    SideEffect {
        printD("side - effect> run")
    }

    Text("show>")
    printD("side - ui>")
}


@Composable
private fun LaunchEffect1() {
    //协程异步操作，首次重组时执行，轮询任务、网络请求、数据库读取
    LaunchedEffect(Unit) {
        printD("launch>${getMills()}")
    }
}


@Composable
private fun DisEffect1() {
    //Composable退出时自动释放，加强版的sideEffect
    DisposableEffect(Unit) {
        onDispose {
            //释放资源、反注册
        }
    }
}

//LaunchedEffect会在参数key变化的时候启动一个协程，但有的时候我们并不希望协程中断，所以只
//要能够实时获取到最新的状态就可以了，因此可以借助于rememberUpdateState API来实现。
@Composable
private fun UpdateEffect1(onTimeOut : ()->Unit) {
    var count by remember { mutableStateOf(0) }
    val shouldUpdate = rememberUpdatedState(count)


    val currentonTimeOut by rememberUpdatedState(onTimeOut)
    LaunchedEffect(Unit) {
//        delay(1000)
//        currentOnTimeOut()//这样总是能够取到最新的onTimeOut
    }
}

//将异步操作（如网络请求、数据库查询）或非 Compose 状态（如 Flow、LiveData）转换
// 为 Compose 可观察的 State 对象，触发 UI 重组。
@Composable
private fun produceState1(){
    val timeState = produceState(initialValue = 0) {
        while (true) {
            delay(1000)
            value++ // 更新状态
        }
    }
    Text("Seconds: ${timeState.value}")
}

//可以过滤高频状态，避免无效重组
@Composable
private fun derivedState1(){
// 示例：滚动时显示按钮（避免频繁重组）
    val lazyState = rememberLazyListState()
    val showButton by remember {
        derivedStateOf {
            lazyState.firstVisibleItemIndex > 0 // 仅当结果变化时重组
        }
    }
    AnimatedVisibility(showButton) {
        //scroolButtonTop()
    }
}