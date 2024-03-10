package com.example.androidcopilot.chat

import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.chat.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

sealed interface ChatState {

    object Initializing: ChatState

    data class Initialized(
        val conversation: Conversation,
        val messages: List<Message>
    ): ChatState
}

class ChatController(
    private val chatRepository: ChatRepository,
    private val conversationId: Long,
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main,
): CoroutineScope {

    private val conversationState: MutableStateFlow<Conversation> = MutableStateFlow(
        Conversation(id = conversationId)
    )
    private val messageListState: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())

    val conversation = conversationState.asStateFlow()
    val messages = messageListState.asStateFlow()

    init {
        init()
    }

    private fun init() {

    }

    private suspend fun prepareChat() {
        val conversation = chatRepository.findConversationById(conversationId)
        val messages = chatRepository.contextMessages(conversationId)
    }

    suspend fun send(message: String, attachments: List<Attachment>): Message {
        TODO()
    }

    suspend fun stop(message: Message): Message {
        TODO()
    }

    suspend fun retry(message: Message): Message {
        TODO()
    }


}