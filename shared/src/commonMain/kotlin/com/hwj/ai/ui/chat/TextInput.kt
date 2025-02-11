package com.hwj.ai.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hwj.ai.ui.viewmodel.ConversationViewModel
import androidx.compose.material3.TextFieldDefaults
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

@OptIn(ExperimentalMaterial3Api::class)
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
            Divider(Modifier.height(0.2.dp))
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
                        placeholder = { Text("Ask me anything", fontSize = 12.sp) },
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .weight(1f),
//                        colors = TextFieldDefaults.outlinedTextFieldColors(
////                        colors = androidx.compose.material3.TextFieldDefaults.(
//                            focusedBorderColor = Color.Transparent,
//                            unfocusedBorderColor = Color.Transparent,
//                            focusedTextColor = Color.White,
//                        ),
                    )
                    IconButton(onClick = {

                        scope.launch {
                            val textClone = text.text.toString()
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
