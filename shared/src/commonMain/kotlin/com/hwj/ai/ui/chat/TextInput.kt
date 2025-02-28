package com.hwj.ai.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.ui.global.KeyEventEnter
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import kotlinx.coroutines.launch


@Composable
fun TextInput(
    conversationViewModel: ConversationViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    TextInputIn(
        sendMessage = { text ->
            //判断是否在生成消息不让点击事件
            if (!conversationViewModel.getFabStatus()) {
                coroutineScope.launch {
                    conversationViewModel.sendMessage(text)
                }
            }
        }
    )
}

@Composable
fun TextInputIn(
    sendMessage: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        // Use navigationBarsPadding() imePadding() and , to move the input panel above both the
        // navigation bar, and on-screen keyboard (IME)
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Column {
            HorizontalDivider(Modifier.height(0.2.dp))
            Box(
                Modifier
                    .padding(horizontal = 4.dp)
                    .padding(top = 6.dp, bottom = 10.dp)
            ) {
                Row {
                    TextField(
                        value = text,
                        onValueChange = {
                            text = it
                        },
                        label = null,
                        placeholder = {
                            Text(
                                "Ask me anything",
                                fontSize = 12.sp
                            )
                        },
//                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .background(Color.Transparent)
                            .weight(1f).KeyEventEnter {
                                scope.launch {
                                    val textClone = text.text
                                    text = TextFieldValue("")
                                    sendMessage(textClone)
                                }
                            },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent, //去除边框
                            unfocusedContainerColor = Color.Transparent,
                            unfocusedTextColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onTertiary,
                            focusedIndicatorColor = Color.Transparent, //去除底部横线
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    IconButton(onClick = {
                        scope.launch {
                            val textClone = text.text
                            text = TextFieldValue("")
                            sendMessage(textClone)
                        }
                    }) {
                        Icon(
                            Icons.Filled.Send,
                            "sendMessage",
                            modifier = Modifier.size(26.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}
