@file:Suppress("UNCHECKED_CAST")

package com.hwj.ai.ui.viewmodel

import com.hwj.ai.data.repository.ConversationRepository
import com.hwj.ai.data.repository.LLMRepository
import com.hwj.ai.data.repository.MessageRepository
import com.hwj.ai.global.getMills
import com.hwj.ai.global.getNowTime
import com.hwj.ai.models.ConversationModel
import com.hwj.ai.models.MessageModel
import com.hwj.ai.models.MessageTurbo
import com.hwj.ai.models.TextCompletionsParam
import com.hwj.ai.models.TurboRole
import kotlinx.coroutines.flow.*

/**
 * Used to communicate between screens.
 */
class ConversationViewModel(
    private val conversationRepo: ConversationRepository,
    private val messageRepo: MessageRepository,
    private val openAIRepo: LLMRepository,
) : moe.tlaster.precompose.viewmodel.ViewModel() { //换掉这个viewModel
    private val _currentConversation: MutableStateFlow<String> =
        MutableStateFlow(getMills().toString())
    private val _conversations: MutableStateFlow<MutableList<ConversationModel>> = MutableStateFlow(
        mutableListOf()
    )
    private val _messages: MutableStateFlow<HashMap<String, MutableList<MessageModel>>> =
        MutableStateFlow(HashMap())
    private val _isFetching: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isFabExpanded = MutableStateFlow(false)

    val currentConversationState: StateFlow<String> = _currentConversation.asStateFlow()
    val conversationsState: StateFlow<MutableList<ConversationModel>> = _conversations.asStateFlow()
    val messagesState: StateFlow<HashMap<String, MutableList<MessageModel>>> =
        _messages.asStateFlow()
    val isFetching: StateFlow<Boolean> = _isFetching.asStateFlow()
    val isFabExpanded: StateFlow<Boolean> get() = _isFabExpanded

    private var stopReceivingResults = false


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

    suspend fun sendMessage(message: String) {
        stopReceivingResults = false
        if (getMessagesByConversation(_currentConversation.value).isEmpty()) {
            createConversationRemote(message)
        }

        val newMessageModel: MessageModel = MessageModel(
            question = message,
            answer = "Let me thinking...",
            conversationId = _currentConversation.value,
        )

        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        // Insert message to list
        currentListMessage.add(0, newMessageModel)
        setMessages(currentListMessage)

        // Execute API OpenAI
        val flow: Flow<String> = openAIRepo.textCompletionsWithStream(
            TextCompletionsParam(
                promptText = getPrompt(_currentConversation.value),
                messagesTurbo = getMessagesParamsTurbo(_currentConversation.value)
            )
        )
        var answerFromGPT: String = ""
        // When flow collecting updateLocalAnswer including FAB behavior expanded.
        // On completion FAB == false
        flow.onCompletion {
            setFabExpanded(false)
        }.collect { value ->
            if (stopReceivingResults) {
                setFabExpanded(false)
                return@collect
            }
            answerFromGPT += value
            updateLocalAnswer(answerFromGPT.trim())
            setFabExpanded(true)
        }

        // Save to FireStore
        messageRepo.createMessage(newMessageModel.copy(answer = answerFromGPT))
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
    }

    fun newConversation() {
        val conversationId: String = getNowTime().time.toString()

        _currentConversation.value = conversationId
    }

    private fun getMessagesByConversation(conversationId: String): MutableList<MessageModel> {
        if (_messages.value[conversationId] == null) return mutableListOf()

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
//            _messages.value.clone() as HashMap<String, MutableList<MessageModel>>
            _messages.value.mapValues { entry -> entry.value.toMutableList() } as HashMap<String, MutableList<MessageModel>>
        return messagesMap[conversationId]!!
    }

    private fun getPrompt(conversationId: String): String {
        if (_messages.value[conversationId] == null) return ""

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
//            _messages.value.clone() as HashMap<String, MutableList<MessageModel>>
            _messages.value.mapValues { entry -> entry.value.toMutableList() } as HashMap<String, MutableList<MessageModel>>
        var response: String = ""

        for (message in messagesMap[conversationId]!!.reversed()) {
            response += """Human:${message.question.trim()}
                |Bot:${
                if (message.answer == "Let me thinking...") ""
                else message.answer.trim()
            }""".trimMargin()
        }

        return response
    }

    private fun getMessagesParamsTurbo(conversationId: String): List<MessageTurbo> {
        if (_messages.value[conversationId] == null) return listOf()

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
//            _messages.value.clone() as HashMap<String, MutableList<MessageModel>>
            _messages.value.mapValues { entry -> entry.value.toMutableList() } as HashMap<String, MutableList<MessageModel>>
        val response: MutableList<MessageTurbo> = mutableListOf(
            MessageTurbo(
                role = TurboRole.system, content = "Markdown style if exists code"
            )
        )

        for (message in messagesMap[conversationId]!!.reversed()) {
            response.add(MessageTurbo(content = message.question))

            if (message.answer != "Let me thinking...") {
                response.add(MessageTurbo(content = message.answer, role = TurboRole.user))
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
//            _messages.value.clone() as HashMap<String, MutableList<MessageModel>>
            _messages.value.mapValues { entry -> entry.value.toMutableList() } as HashMap<String, MutableList<MessageModel>>
        messagesMap[_currentConversation.value] = messages

        _messages.value = messagesMap
    }

    fun stopReceivingResults() {
        stopReceivingResults = true
    }

    private fun setFabExpanded(expanded: Boolean) {
        _isFabExpanded.value = expanded
    }

    fun getFabStatus(): Boolean {
        return _isFabExpanded.value
    }
}