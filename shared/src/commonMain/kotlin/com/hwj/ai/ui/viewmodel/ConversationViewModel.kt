package com.hwj.ai.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatMessage
import com.hwj.ai.data.repository.ConversationRepository
import com.hwj.ai.data.repository.LLMChatRepository
import com.hwj.ai.data.repository.LLMRepository
import com.hwj.ai.data.repository.MessageRepository
import com.hwj.ai.getPlatform
import com.hwj.ai.global.DATA_IMAGE_TITLE
import com.hwj.ai.global.DATA_SYSTEM_NAME
import com.hwj.ai.global.DATA_USER_NAME
import com.hwj.ai.global.NotificationsManager
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.encodeImageToBase64
import com.hwj.ai.global.getMills
import com.hwj.ai.global.getNowTime
import com.hwj.ai.global.onlyDesktop
import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.hwj.ai.global.printList
import com.hwj.ai.global.thinking
import com.hwj.ai.global.workInSub
import com.hwj.ai.models.ConversationModel
import com.hwj.ai.models.MessageModel
import com.hwj.ai.models.TextCompletionsParam
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import moe.tlaster.precompose.viewmodel.ViewModel
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Used to communicate between screens.
 */
class ConversationViewModel(
    private val conversationRepo: ConversationRepository,
    private val messageRepo: MessageRepository,
    private val openAIRepo: LLMRepository, private val openRepo: LLMChatRepository,
    private val toastManager: NotificationsManager
) : ViewModel() { //换掉这个viewModel
    private val _currentConversation: MutableStateFlow<String> =
        MutableStateFlow(getMills().toString())
    private val _conversations: MutableStateFlow<MutableList<ConversationModel>> = MutableStateFlow(
        mutableListOf()
    )

    //所有会话记录
    private val _messages: MutableStateFlow<HashMap<String, MutableList<MessageModel>>> =
        MutableStateFlow(HashMap())
    private val _isFetching: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isFabExpandObs = MutableStateFlow(false)

    val currentConversationState: StateFlow<String> = _currentConversation.asStateFlow()
    val conversationsState: StateFlow<MutableList<ConversationModel>> = _conversations.asStateFlow()
    val messagesState: StateFlow<HashMap<String, MutableList<MessageModel>>> =
        _messages.asStateFlow()
    val isFetching: StateFlow<Boolean> = _isFetching.asStateFlow()
    val isFabExpanded: StateFlow<Boolean> get() = _isFabExpandObs

    //停止接收回答
    private val _stopReceivingObs = MutableStateFlow(false)
    val stopReceivingState = _stopReceivingObs.asStateFlow()

    //检测下当前环境状态，正常再发送，如网络、权限、登录
    private val _isUsefulObs: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isUsefulState: StateFlow<Boolean> = _isUsefulObs.asStateFlow()

    //处理选中图片源的解析
    private val _imageListObs = mutableStateListOf<PlatformFile>() //自动过滤重复？
    val imageListState = MutableStateFlow(_imageListObs).asStateFlow()

    //桌面端图片处理，多轮对话可以多次引用图片，手机则发了就结束
    private val _isStopUseImageObs = MutableStateFlow(false)
    val isStopUseImageState = _isStopUseImageObs.asStateFlow()

    suspend fun initialize() {
        _isFetching.value = true

        _conversations.value = conversationRepo.fetchConversations()

        if (_conversations.value.isNotEmpty()) {
            _currentConversation.value = _conversations.value.first().id
            fetchMessages()
        }

        _isFetching.value = false
    }

    suspend fun onConversation(conversation: ConversationModel) {
        _isFetching.value = true
        _currentConversation.value = conversation.id

        fetchMessages()
        _isFetching.value = false
    }

    suspend fun sendTxtMessage(input: String) {
        _stopReceivingObs.value = false
        if (getMessagesByConversation(_currentConversation.value).isEmpty()) {
            createConversationRemote(input)
        }

        val newMessageModel: MessageModel = MessageModel(
            question = input,
            answer = thinking,
            conversationId = _currentConversation.value,
        )

        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        // Insert message to list
        currentListMessage.add(0, newMessageModel)
        setMessages(currentListMessage)

        //直接调用api接口方式
//        // Execute API OpenAI ,返回数据
//        val flow: Flow<String> = openAIRepo.textCompletionsWithStream(
//            TextCompletionsParam(
//                promptText = getPrompt(_currentConversation.value),
//                messagesTurbo = getMessagesParamsTurbo(_currentConversation.value)
//            )
//        )
//
//        var answerFromGPT: String = ""
//        // When flow collecting updateLocalAnswer including FAB behavior expanded.
//        // On completion FAB == false
//        flow.onCompletion {
//            setFabExpanded(false)
//        }.collect { value ->
//            if (stopReceivingResults) {
//                setFabExpanded(false)
//                return@collect
//            }
//            answerFromGPT += value
//            updateLocalAnswer(answerFromGPT.trim())
//            setFabExpanded(true)
//        }
        updateTextMsg(newMessageModel)
    }

    suspend fun updateTextMsg(newMessageModel: MessageModel) {
        //openAi sdk
        withContext(Dispatchers.Default) {
            val flowControl = openRepo.receiveAIMessage( //调用大模型接口
                TextCompletionsParam(
                    promptText = getPrompt(_currentConversation.value),
                    messagesTurbo = getMessagesParamsTurbo(_currentConversation.value)
                )
            )

            var answerFromGPT = ""
            try {
                flowControl?.onCompletion {
                    printD("done>")
                    setFabExpanded(false)
                }?.collect { chunk -> //被强制类型
                    if (_stopReceivingObs.value) {
                        setFabExpanded(false)
                        return@collect
                    }
                    try {
                        chunk.choices.first().delta?.content?.let {
                            answerFromGPT += it
//                            printD(answerFromGPT.trim()) //打印
                            updateLocalAnswer(answerFromGPT.trim())
                            setFabExpanded(true)
                        }
                    } catch (e: Exception) {
//                    printE(e)
                    }
                }
            } catch (e: Exception) {
//            printE(e) //gpt-4o 返回的数据格式好多异常
            }
            // Save to FireStore
            messageRepo.createMessage(newMessageModel.copy(answer = answerFromGPT))
        }
    }

    //对多张图片进行解析？多轮对话后不再续传图片，当AI反复查询细节则要
    suspend fun sendAnalyzeImageMsg(imagePaths: List<PlatformFile>, input: String? = null) {
        _stopReceivingObs.value = false

        if (getMessagesByConversation(_currentConversation.value).isEmpty()) {
            createConversationRemote(DATA_IMAGE_TITLE) //创建新的会话
        }
        //一轮对话两条问答消息
        val newMessageModel = MessageModel(
            question = if (input.isNullOrEmpty()) "" else input,
            answer = thinking, conversationId = _currentConversation.value,
            imagePath = imagePaths
        )

        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        currentListMessage.add(0, newMessageModel)
        setMessages(currentListMessage)
        updateImageMsg(newMessageModel)
    }

    private fun updateImageMsg(newMessageModel: MessageModel) {
        workInSub {
            val flowControl = openRepo.AnalyzeImage(
                TextCompletionsParam(
                    promptText = getPrompt(_currentConversation.value),
                    messagesTurbo = getMessagesParamsTurbo(_currentConversation.value)
                )
            )
            var answerFromGPT = ""
            try {
                flowControl?.onCompletion {
                    setFabExpanded(false)
                    if (_isStopUseImageObs.value) //如果每次都传图参，那就不删
                        deleteImage(0, true)
                }?.collect { chunk ->
                    if (_stopReceivingObs.value) {
                        setFabExpanded(false)
                        return@collect
                    }

                    try {
                        chunk.choices.first().delta?.content?.let {
                            answerFromGPT += it
                            updateLocalAnswer(answerFromGPT.trim())
                            setFabExpanded(true)
                        }
                    } catch (e: Exception) {
                    }
                }
            } catch (e: Exception) {
                printE(e)
            }
            messageRepo.createMessage(newMessageModel.copy(answerFromGPT))
        }
    }

    private fun createConversationRemote(title: String) {
        val newConversation: ConversationModel = ConversationModel(
            id = _currentConversation.value,
            title = title,
            createdAt = getNowTime(),
        )

        conversationRepo.newConversation(newConversation)

        val conversations = _conversations.value.toMutableList()
        conversations.add(0, newConversation)

        _conversations.value = conversations

        if (onlyDesktop()) {
            setImageUseStatus(false)
        } else {
            setImageUseStatus(true)
        }
    }

    fun newConversation() {
        val conversationId: String = getNowTime().time.toString()

        _currentConversation.value = conversationId
    }

    private fun getMessagesByConversation(conversationId: String): MutableList<MessageModel> {
        if (_messages.value[conversationId] == null) return mutableListOf()

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.mapValues { entry -> entry.value.toMutableList() } as HashMap<String, MutableList<MessageModel>>
        return messagesMap[conversationId]!!
    }

    private fun getPrompt(conversationId: String): String {
        if (_messages.value[conversationId] == null) return ""

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.mapValues { entry -> entry.value.toMutableList() } as HashMap<String, MutableList<MessageModel>>
        var response: String = ""

        for (message in messagesMap[conversationId]!!.reversed()) {
            response += """Human:${message.question.trim()}
                |Bot:${
                if (message.answer == thinking) ""
                else message.answer.trim()
            }""".trimMargin()
        }
//        printD("getPrompt>$response")//Human:青菜汤
        return response
    }

    @OptIn(ExperimentalEncodingApi::class)
    private suspend fun getMessagesParamsTurbo(conversationId: String): List<ChatMessage> {
        if (_messages.value[conversationId] == null) return listOf()

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.mapValues { entry -> entry.value.toMutableList() } as HashMap<String, MutableList<MessageModel>>
        val response: MutableList<ChatMessage> = mutableListOf(
            chatMessage {
                role = ChatRole.System
                name = DATA_SYSTEM_NAME
                content = "Markdown style if exists code"
            }
        )

        for (index in messagesMap[conversationId]!!.reversed().indices) { //拆解两条，按时间排序
            val message = messagesMap[conversationId]!!.reversed()[index]
            var partsReq = mutableListOf<String>()

            if (!_isStopUseImageObs.value) { //一直引用图
                partsReq = coverBase64Data(message.imagePath)
            } else {
                if (index == messagesMap[conversationId]!!.size - 1) { //必须是当轮问答图片才传
                    partsReq = coverBase64Data(message.imagePath)
                }
            }
            response.add(
                chatMessage { //用户提问
                    role = ChatRole.User
                    name = DATA_USER_NAME
                    if (message.imagePath == null) {
                        content = message.question
                    } else {
//                        如果是图片 , 后续多轮对话不再传图片, 怎么区分新旧, 这里是新问题发起，全是旧的
                        if (index == messagesMap[conversationId]!!.size - 1) { //最后一个又有图片，那么就转base64,不然都是历史图片
                            content {
                                text("$DATA_IMAGE_TITLE，" + message.question)
                                partsReq.forEach { part ->
                                    image(part)//base64或url
                                }
                            }
                        } else {
                            content = message.question
                        }
                    }
                }
            )

            if (message.answer != thinking) { //AI回答中
                response.add(
                    chatMessage {
                        role = ChatRole.Assistant
                        name = DATA_SYSTEM_NAME
                        content = message.answer
                    }
                )
            }
        }
        return response.toList()
    }

    suspend fun deleteConversation(conversationId: String) {
        // Delete remote
        conversationRepo.deleteConversation(conversationId)

        // Delete local
        val conversations: MutableList<ConversationModel> = _conversations.value.toMutableList()
        val conversationToRemove = conversations.find { it.id == conversationId }

        if (conversationToRemove != null) {
            conversations.remove(conversationToRemove)
            _conversations.value = conversations
        }
    }

    private suspend fun fetchMessages() {
        if (_currentConversation.value.isEmpty() ||
            _messages.value[_currentConversation.value] != null
        ) return

        //调用接口获取大模型数据
        val flow: Flow<List<MessageModel>> = messageRepo.fetchMessages(_currentConversation.value)

        flow.collectLatest {
            setMessages(it.toMutableList())
        }
    }

    private fun updateLocalAnswer(answer: String) {
        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        currentListMessage[0] = currentListMessage[0].copy(answer = answer)

        setMessages(currentListMessage)
    }

    private fun setMessages(messages: MutableList<MessageModel>) {
        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.mapValues { entry -> entry.value.toMutableList() } as HashMap<String, MutableList<MessageModel>>
        messagesMap[_currentConversation.value] = messages//赋值
        _messages.value = messagesMap
    }

    suspend fun generateMsgAgain() {
        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()
        //给的数据是倒序，第一条就是最新的
        currentListMessage[0] = currentListMessage[0].copy(answer = thinking)
        setMessages(currentListMessage)
        //重新生成，就应该删除最新的回答,还要分是文字问还是图问？
        if (currentListMessage[0].imagePath == null) {
            updateTextMsg(currentListMessage[0])
        } else {
            updateImageMsg(currentListMessage[0])
        }
    }

    fun stopReceivingResults() {
        _stopReceivingObs.value = true
    }

    private fun setFabExpanded(expanded: Boolean) {
        _isFabExpandObs.value = expanded
    }

    fun getFabStatus(): Boolean {
        return _isFabExpandObs.value
    }

    suspend fun selectImage() {
        if (getPlatform().os == OsStatus.ANDROID
            || getPlatform().os == OsStatus.IOS
        ) {
            val model = FileKitMode.Multiple(2)
            val files = FileKit.openFilePicker(mode = model, type = FileKitType.Image)
            files?.let {
                _imageListObs.addAll(it)
                printList(_imageListObs.toList())
            }
        } else {
            val file = FileKit.openFilePicker(mode = FileKitMode.Single, type = FileKitType.Image)
            file?.let { _imageListObs.add(file) }
        }
    }

    fun deleteImage(index: Int, isRemoveAll: Boolean = false) {
        workInSub {
            if (isRemoveAll) {
//                printD("clear-all>")
                _imageListObs.clear()
            } else {
                _imageListObs.removeAt(index)
            }
        }
    }

    suspend fun checkUsefulStatus() {

    }

    suspend fun coverBase64Data(imagePaths: List<PlatformFile>?): MutableList<String> {
        val list = mutableListOf<String>()
        imagePaths?.let {
            it.forEach { p ->
                list.add(encodeImageToBase64(p))
                //测试图片
//                list.add("https://qcloudimg.tencent-cloud.cn/raw/42c198dbc0b57ae490e57f89aa01ec23.png")
            }
        }
        return list
    }

    fun toast(title: String, des: String) {
        toastManager.showNotification(title, des)
    }

    fun setImageUseStatus(flag: Boolean) {
        _isStopUseImageObs.value = flag
    }
}