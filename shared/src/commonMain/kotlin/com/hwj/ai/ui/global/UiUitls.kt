package com.hwj.ai.ui.global

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent

/**
 * @author by jason-何伟杰，2025/2/28
 * des:识别 键盘按键
 */
@Composable
fun Modifier.KeyEventEnter(block: () -> Unit): Modifier {
    return this.then( //合并之前的样式，不然会覆盖
        Modifier.onKeyEvent { event: KeyEvent ->
            when {
                event.key == Key.Enter && event.isShiftPressed -> {
                    block()
                    true
                }

                event.key == Key.Enter -> {
                    block()
                    true
                }

                else -> false
            }
        })
}