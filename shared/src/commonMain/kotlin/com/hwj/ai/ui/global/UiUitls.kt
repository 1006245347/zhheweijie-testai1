package com.hwj.ai.ui.global

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import com.hwj.ai.global.onlyDesktop

/**
 * @author by jason-何伟杰，2025/2/28
 * des:识别 键盘按键
 */
@Composable
fun Modifier.KeyEventEnter(enter: () -> Unit, shift: () -> Unit): Modifier {
    return this.then( //合并之前的样式，不然会覆盖
        Modifier.onPreviewKeyEvent { event: KeyEvent ->
            when {
                event.key == Key.Enter && event.isShiftPressed
                        && event.type == KeyEventType.KeyDown -> {
                    shift()
                    false
                }

                event.key == Key.Enter
                        && event.type == KeyEventType.KeyDown && onlyDesktop() -> {
                    enter()
                    true
                }

                else -> false
            }
        })
}


