package com.hwj.ai.android

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.hwj.ai.models.MessageModel
import com.hwj.ai.ui.chat.MessageCard

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
//    testPreview1()
//    AppBar(onClickMenu = {})

//    AppDrawerIn(
//        onChatClicked = {},
//        onNewChatClicked = {},
//        onIconClicked = {},
//        conversationViewModel = {},
//        deleteConversation = {},
//        conversationState = mutableListOf(),
//        currentConversationState = String(),
//        onConversation = { _: ConversationModel -> },
//        navigator = null
//    )

}

//@Entity
//data class  BB(var id: Long=0)


//预览桌面端界面
@Composable
fun PreviewDesktop() {
//myobjectb

}

@Preview(showBackground = true)
@Composable
fun MessageCardPreviewHuman() {
    MessageCard(
        message = MessageModel(
            id = "",
            conversationId = "",
            question = "question text field by Human ",
            answer = "question text field by Human ",
            createdAt = null
        ),
        isHuman = true,
        isLast = false

    )
}

@Preview(showBackground = true)
@Composable
fun MessageCardPreviewBot() {
    MessageCard(
        message = MessageModel(
            id = "",
            conversationId = "",
            question = "answer text field by Bot ",
            answer = "answer text field by Bot ",
            createdAt = null

        ),
        isHuman = false,
        isLast = false

    )
}