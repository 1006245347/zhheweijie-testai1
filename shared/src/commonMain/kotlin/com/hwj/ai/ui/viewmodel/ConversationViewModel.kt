package com.hwj.ai.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.TextContent
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolBuilder
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.chat.chatMessage
import com.aallam.openai.api.model.ModelId
import com.hwj.ai.checkSystem
import com.hwj.ai.data.http.handleAIException
import com.hwj.ai.data.http.toast
import com.hwj.ai.data.repository.ConversationRepository
import com.hwj.ai.data.repository.LLMChatRepository
import com.hwj.ai.data.repository.LLMRepository
import com.hwj.ai.data.repository.MessageRepository
import com.hwj.ai.except.ClipboardHelper
import com.hwj.ai.except.isMainThread
import com.hwj.ai.getPlatform
import com.hwj.ai.global.DATA_IMAGE_TITLE
import com.hwj.ai.global.DATA_SYSTEM_NAME
import com.hwj.ai.global.DATA_USER_NAME
import com.hwj.ai.global.Event
import com.hwj.ai.global.EventHelper
import com.hwj.ai.global.NotificationsManager
import com.hwj.ai.global.OsStatus
import com.hwj.ai.global.ToastUtils
import com.hwj.ai.global.answerUseZw
import com.hwj.ai.global.append
import com.hwj.ai.global.encodeImageToBase64
import com.hwj.ai.global.execute
import com.hwj.ai.global.getMills
import com.hwj.ai.global.getNowTime
import com.hwj.ai.global.onlyDesktop
import com.hwj.ai.global.printD
import com.hwj.ai.global.printE
import com.hwj.ai.global.printList
import com.hwj.ai.global.reasoning
import com.hwj.ai.global.thinking
import com.hwj.ai.global.toolWeather
import com.hwj.ai.global.workInSub
import com.hwj.ai.models.ConversationModel
import com.hwj.ai.models.GPTModel
import com.hwj.ai.models.MessageModel
import com.hwj.ai.models.TextCompletionsParam
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import kotlin.random.Random

/**
 * Used to communicate between screens.
 */
class ConversationViewModel(
    private val conversationRepo: ConversationRepository,
    private val messageRepo: MessageRepository,
    private val openAIRepo: LLMRepository, private val openRepo: LLMChatRepository,
    private val toastManager: NotificationsManager, private val clipboardHelper: ClipboardHelper
) : ViewModel() {
    private val _currentConversation: MutableStateFlow<String> =
        MutableStateFlow(getMills().toString())
    private val _conversations: MutableStateFlow<MutableList<ConversationModel>> = MutableStateFlow(
        mutableListOf()
    )

    //所有会话记录
    private val _messages: MutableStateFlow<HashMap<String, MutableList<MessageModel>>> =
        MutableStateFlow(HashMap())
    private val _isFetching: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isAutoScroll: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isFabExpandObs = MutableStateFlow(false)

    val currentConversationState: StateFlow<String> = _currentConversation.asStateFlow()
    val conversationsState: StateFlow<MutableList<ConversationModel>> = _conversations.asStateFlow()
    val messagesState: StateFlow<HashMap<String, MutableList<MessageModel>>> =
        _messages.asStateFlow()
    val isFetching: StateFlow<Boolean> = _isFetching.asStateFlow()
    val isAutoScroll: StateFlow<Boolean> = _isAutoScroll.asStateFlow()

    //    val isFabExpanded: StateFlow<Boolean> get() = _isFabExpandObs
    val isFabExpanded = _isFabExpandObs.asStateFlow()

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

    var curJob: kotlinx.coroutines.Job? = null

    //放在vm是因为拍照回来，输入过的文字会被丢失
    var inputTxt by mutableStateOf(TextFieldValue(""))

    //深度思考
    val _thinkAiObs = MutableStateFlow(false)
    val thinkAiState = _thinkAiObs.asStateFlow()


    suspend fun initialize() {
        _isFetching.value = true

        _conversations.value = conversationRepo.fetchConversations()

        if (_conversations.value.isNotEmpty()) {
            _currentConversation.value = _conversations.value.first().id
            fetchMessages()
        }

        _isFetching.value = false
    }

    //全局事件监听 , 应用顶层前置
    val eventObs = viewModelScope.launch {
        EventHelper.events.collect { event ->
            when (event) {
                is Event.SelectionEvent -> {
                    if (event.code == 0) {
                        curJob?.cancel()
                        newConversation()
                        printD(event.txt)
                        sendTxtMessage("搜索如下内容，${event.txt}")

                    } else if (event.code == 1) {
                        curJob?.cancel()
                        newConversation()
                        sendTxtMessage("总结如下内容，${event.txt}")
                    }
                }

                is Event.AnalyzePicEvent -> {
                    curJob?.cancel()
                    newConversation()
//                    addCameraImage(PlatformFile(event.path)) //怎么两张
                    sendAnalyzeImageMsg(_imageListObs, "图片内容分析")
                }

                is Event.RefreshEvent -> {
//                    newConversation()
                }

                is Event.DeleteConversationEvent -> {     //列表也没刷新，不是当前的不需要停止呀
                    if (event.conversationId == _currentConversation.value) {
                        stopReceivingResults()
                    }
                    newConversation()
                }

                else -> {}
            }
        }
    }

    fun onInputChange(newTxt: String, selection: TextRange = TextRange(newTxt.length)) {
        inputTxt = TextFieldValue(text = newTxt, selection = selection)
    }

    //ViewModel 中直接接收完整的 TextFieldValue,不然丢失composition：输入法组合状态（拼音区）
    fun onInputChange(textFieldValue: TextFieldValue) {
        inputTxt = textFieldValue
    }

    suspend fun onConversation(conversation: ConversationModel) {
        _isFetching.value = true
        _currentConversation.value = conversation.id

        fetchMessages()
        _isFetching.value = false
    }

    fun sendTxtMessage(input: String) {
        _stopReceivingObs.value = false
        if (getMessagesByConversation(_currentConversation.value).isEmpty()) {
            workInSub {
                createConversationRemote(input)
            }
        }

        val newMessageModel = MessageModel(
            question = input,
            answer = thinking,
            conversationId = _currentConversation.value,
        )

        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        // Insert message to list
        currentListMessage.add(0, newMessageModel)
        setMessages(currentListMessage)

//        curJob = viewModelScope.launch(Dispatchers.Default) { //        //直接调用api接口方式
////        // Execute API OpenAI ,返回数据
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
//            if (_stopReceivingObs.value) {
//                stopReceiveMsg(newMessageModel)
//                return@collect
//            }
//            answerFromGPT += value
//            updateLocalAnswer(answerFromGPT.trim())
//            setFabExpanded(true)
//        }
//        //数据库保存
//        messageRepo.createMessage(newMessageModel.copy(answer = answerFromGPT))
//    }
        updateTextMsg(newMessageModel)
    }

    private fun updateTextMsg(newMessageModel: MessageModel) {
        //openAi sdk
        curJob = viewModelScope.launch(Dispatchers.Default) {
            val params = TextCompletionsParam(
                promptText = getPrompt(_currentConversation.value),
                messagesTurbo = getMessagesParamsTurbo(_currentConversation.value),
                model = GPTModel.DeepSeekV3
            )//调用大模型接口

            var flowControl: Flow<ChatCompletionChunk>? = null
            try {
                flowControl = openRepo.receiveAIMessage(params, useThink = _thinkAiObs.value)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            var answerFromGPT = ""
            try {
                flowControl?.onStart {
                    setFabExpanded(true)
                }?.onCompletion {
                    setFabExpanded(false)
                }?.catch { e: Throwable ->
                    e.printStackTrace() //内部错误，请稍后重试 这句话是接口的
                    handleAIException(toastManager, e) { //异常处理
                        setFabExpanded(false)
                        if (answerFromGPT == "") {

                            updateLocalAnswer("异常导致回复中断")
                            answerFromGPT = "异常导致回复中断" //不加消息数量会缺失
                        }
                    }
                }?.collect { chunk -> //被强制类型
                    if (_stopReceivingObs.value) {
                        stopReceiveMsg(newMessageModel)
                        return@collect
                    }
                    try {
                        //不行，上面的chunk结构就不对！ reasoning_content
//                        chunk.choices.first().reasoning()?.let {
//                            answerFromGPT += it
//                            updateLocalAnswer(answerFromGPT.trim())
//                        }
                        chunk.choices.first().delta?.content?.let {
                            answerFromGPT += it
//                                printD(it) //打印答案
                            updateLocalAnswer(answerFromGPT.trim())
//                            setFabExpanded(true) //有数据才显示终止
                        }
                    } catch (e: Exception) {
                        printE(e, "121")
                    }
                }
            } catch (e: Exception) {
                printE(e, "131") //gpt-4o 返回的数据格式好多异常
            }
            // Save to dataStore
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
            imagePath = imagePaths.map { it.path } //复制一份
        )

        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        currentListMessage.add(0, newMessageModel)
        setMessages(currentListMessage)

        updateImageMsg(newMessageModel)
    }


    //怎么多轮连贯调用tool
    fun sendTxtToolMessage(
        input: String,
        tool: Tool
    ) {
        _stopReceivingObs.value = false
        if (getMessagesByConversation(_currentConversation.value).isEmpty()) {
            workInSub { createConversationRemote(input) }
        }
        val newMessageModel = MessageModel(
            question = input,
            answer = thinking,
            conversationId = _currentConversation.value,
        )

        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        // Insert message to list
        currentListMessage.add(0, newMessageModel)
        setMessages(currentListMessage)

        curJob = viewModelScope.launch(Dispatchers.Default) {
            val curChatList = getMessagesParamsTurbo(_currentConversation.value).toMutableList()
            val params = TextCompletionsParam(
                promptText = getPrompt(_currentConversation.value),
                model = GPTModel.QwenTool,
                messagesTurbo = curChatList, stream = false //没用到？
            )
            var firstChat: ChatCompletion? = null
            firstChat = openRepo.toolAICall(params, tool) //移到lambda

            val message: ChatMessage = firstChat.choices.first().message
            curChatList.append(message)

            for (toolCall in message.toolCalls.orEmpty()) { //偶尔只返回一个
                require(toolCall is ToolCall.Function) { "Tool call is not a function" }
                val funcRep = toolCall.execute()
                printD("f>$funcRep")
                curChatList.append(toolCall, funcRep)//role - Tool
            }
//            printList(curChatList, "req>")
            try {
                val secondResponse = openRepo.receiveAICompletion(params, curChatList)
                secondResponse.choices.first().message.content?.let {
                    updateLocalAnswer(it)
                    messageRepo.createMessage(newMessageModel.copy(answer = it))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    suspend fun sendFileMsg() {
//        openRepo.AnalyzeImage()
    }

    private suspend fun updateImageMsg(newMessageModel: MessageModel) {
//        printD("updateImageMsg1>")
//        curJob = viewModelScope.launch() {   //加线程切换，无法执行？
//        printD("updateImageMsg2>")
//        if (onlyDesktop()) //测试手机也行
        setImageUseStatus(false) //重置
        val params = TextCompletionsParam( //这里要抽出来顺序执行，openRepo内部又异步，当数据大导致参数属性竟然被扣了。。
            promptText = getPrompt(_currentConversation.value),
            messagesTurbo = getMessagesParamsTurbo(_currentConversation.value)
        )
        var answerFromGPT = ""
        val flowControl = openRepo.analyzeImage(params)
        try {
            flowControl.onStart { setFabExpanded(true) }
                .onCompletion {
                    setFabExpanded(false)
                    if (_isStopUseImageObs.value) {
                    } //如果每次都传图参，那就不删
                    deleteImage(0, true)
                }.catch { e ->
                    handleAIException(toastManager, e) {
                        printE("imgErr>${e.message}")
                        setFabExpanded(false)
                        if (answerFromGPT == "") {
                            updateLocalAnswer("异常导致消息中断")
                        }
                    }
                }
                .collect { chunk ->
                    if (_stopReceivingObs.value) {
                      stopReceiveMsg(newMessageModel)
                        return@collect
                    }

                    try {
                        chunk.choices.first().delta?.content?.let {
                            answerFromGPT += it
//                            printD(it)
                            updateLocalAnswer(answerFromGPT.trim())
//                        setFabExpanded(true)
                        }
                    } catch (e: Exception) {
                    }
                }
        } catch (e: Exception) {
            printE(e)
        }
        //数据库保存
        messageRepo.createMessage(newMessageModel.copy(answer = answerFromGPT))
    }

    private suspend fun createConversationRemote(title: String) {
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
//            setImageUseStatus(true)
            setImageUseStatus(false) //测试
        }
    }

    fun newConversation() { //构建新的会话ID
        val conversationId: String = (getMills() + Random.nextInt(100)).toString()

        _currentConversation.value = conversationId
        _imageListObs.clear()
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

    private suspend fun getMessagesParamsTurbo(conversationId: String): List<ChatMessage> {
        if (_messages.value[conversationId] == null) return listOf()

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.mapValues { entry -> entry.value.toMutableList() } as HashMap<String, MutableList<MessageModel>>
        val response: MutableList<ChatMessage> = mutableListOf(
            chatMessage {
                role = ChatRole.System
                name = DATA_SYSTEM_NAME
//                content = "如果有代码，用Markdown样式回复，用中文回答"
                content = "Markdown style if exists code"
            }
        )
        val testPic = "https://qcloudimg.tencent-cloud.cn/raw/42c198dbc0b57ae490e57f89aa01ec23.png"

        for (index in messagesMap[conversationId]!!.reversed().indices) { //拆解两条，按时间排序
            val message = messagesMap[conversationId]!!.reversed()[index]
            val partsReq = mutableListOf<String>() //所有的图片

//            printList(message.imagePath,des="moreList")
            if (!_isStopUseImageObs.value) { //一直引用图
                try {
                    message.imagePath?.let { pics ->
                        pics.forEach { picS ->
                            val pic = PlatformFile(picS)
                            if (pic.size() > 1000 * 1000 * 5) { //压缩再base64
                                val newPic =
                                    encodeImageToBase64(FileKit.compressImage(pic, quality = 70))
                                partsReq.add(newPic)
                            } else {
                                partsReq.add(encodeImageToBase64(pic))
//                                partsReq.add(testPic) //测试
                            }
                        }

                    }
                } catch (e: Exception) {
                    printE(e)
                }
            } else {
                if (index == messagesMap[conversationId]!!.size - 1) { //必须是当轮问答图片才传
                    try {
                        message.imagePath?.let { pics ->
                            pics.forEach { picS ->
                                val pic = PlatformFile(picS)
                                if (pic.size() > 1000 * 1000 * 5) { //压缩再base64
                                    val newPic =
                                        encodeImageToBase64(
                                            FileKit.compressImage(
                                                pic,
                                                quality = 70
                                            )
                                        )
                                    partsReq.add(newPic)
                                } else {
                                    partsReq.add(encodeImageToBase64(pic))
//                                    partsReq.add(testPic)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        printE(e)
                    }
                }
            }
            response.add(
                chatMessage { //用户提问
                    role = ChatRole.User
                    name = DATA_USER_NAME
                    if (message.imagePath == null) {
                        content = message.question + answerUseZw
                    } else {
//                        如果是图片 , 后续多轮对话不再传图片, 怎么区分新旧, 这里是新问题发起，全是旧的
                        if (index == messagesMap[conversationId]!!.size - 1) { //最后一个又有图片，那么就转base64,不然都是历史图片
                            content { //偶尔包装的数据打印出来有缺失字母，是打印问题还是包装问题。。。
                                text("$DATA_IMAGE_TITLE，" + message.question + answerUseZw)
                                partsReq.forEach { part ->
                                    image(part)//base64或url
                                }
                            }

                        } else {
                            content = message.question
                        }
                    }
                }//.also { printD("cc> ${it.messageContent}") }
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
//            .also { printList(it, des = "getMessagesParamsTurbo") }
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

        val flow: Flow<List<MessageModel>> = messageRepo.fetchMessages(_currentConversation.value)

        flow.collectLatest {
            setMessages(it.toMutableList())
        }
        _isAutoScroll.value = true
//        printD("fetchMessages>${_isAutoScroll.value}")
        delay(1000)
        _isAutoScroll.value = false
    }

    private fun updateLocalAnswer(answer: String) {
        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        currentListMessage[0] = currentListMessage[0].copy(answer = answer)

        setMessages(currentListMessage)
    }

    private suspend fun stopReceiveMsg(newMessageModel: MessageModel) {
        setFabExpanded(false)
        messageRepo.createMessage(newMessageModel)
        curJob?.cancel()
        curJob = null
    }

    private fun setMessages(messages: MutableList<MessageModel>) {
        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.mapValues { entry -> entry.value.toMutableList() } as HashMap<String, MutableList<MessageModel>>
        messagesMap[_currentConversation.value] = messages//赋值
        _messages.value = messagesMap
    }

    fun generateMsgAgain() {
        viewModelScope.launch {
            printD("generateMsgAgain>")
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

    fun setThinkUsed(flag: Boolean) { //深度思考不支持图，区分？其他应用又支持
        _thinkAiObs.value = flag
    }

    fun checkSelectedImg(): Boolean {
        return _imageListObs.size != 2
    }

    suspend fun selectImage() {
        if (_imageListObs.size == 2) {
            ToastUtils.show("最多处理两张图片")
            return //最多两张图
        }
        if (getPlatform().os == OsStatus.ANDROID
            || getPlatform().os == OsStatus.IOS
        ) {
            val model = FileKitMode.Multiple(2)
            //需要对选取的图片进行压缩不
            val files = FileKit.openFilePicker(mode = model, type = FileKitType.Image)
            files?.let {
                _imageListObs.addAll(it)
//                printList(_imageListObs.toList())
            }
        } else {
            val file = FileKit.openFilePicker(mode = FileKitMode.Single, type = FileKitType.Image)
            file?.let { _imageListObs.add(file) }
        }
    }

    suspend fun addCameraImage(pic: PlatformFile) {
        _imageListObs.add(pic)
    }

    fun deleteImage(index: Int, isRemoveAll: Boolean = false) {
        if (isRemoveAll) {
            _imageListObs.clear()
        } else {
            _imageListObs.removeAt(index)
        }
    }

    suspend fun checkUsefulStatus() {

    }


    fun setImageUseStatus(flag: Boolean) {
        _isStopUseImageObs.value = flag
    }

    fun copyToClipboard(text: String) {
        try {
            clipboardHelper.copyToClipboard(text)
        } catch (e: Exception) {
            toast(toastManager, "err", e.message.toString())
        }
    }

    fun toast(title: String, des: String) {
        viewModelScope.launch(Dispatchers.Main) {
            if (checkSystem() == OsStatus.ANDROID) {
                if (des != "toast") {
                    toastManager.showNotification(title, des)
                } else {
                    toastManager.showNotification(title, "toast")
                }
            } else {
                toastManager.showNotification(title, des)
            }
        }

    }

    fun readFromClipboard(): String? {
        return try {
            clipboardHelper.readFromClipboard()
        } catch (e: Exception) {
            null
        }
    }
}