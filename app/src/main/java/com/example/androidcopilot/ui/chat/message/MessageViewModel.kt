package com.example.androidcopilot.ui.chat.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcopilot.api.chat.ChatClient
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.ui.chat.input.InputValue
import com.example.androidcopilot.ui.chat.input.asText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@HiltViewModel
class MessageViewModel  @Inject constructor (private val chatClient: ChatClient): ViewModel() {

    private val conversationIdState: MutableStateFlow<Long> = MutableStateFlow(
        0L
    )
    val conversation: MutableStateFlow<Conversation> = MutableStateFlow(
        Conversation(title = "Android Copilot")
    )
    val messages: MutableStateFlow<List<Message>> = MutableStateFlow(
        emptyList()
    )
    private val sendMessageState = MutableStateFlow(false)
    val isSendingMessage = sendMessageState.asStateFlow()
    private val scrollCommandState = MutableStateFlow<ScrollCommand>(ScrollCommand.Noop)
    val scrollCommand = scrollCommandState.asStateFlow()

    init {
        watchMessages()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun watchMessages() {
        viewModelScope.launch {
            conversationIdState.flatMapConcat {
                if (it != 0L) {
                    chatClient.conversation(it)
                } else {
                    emptyFlow()
                }
            }.collect {
                conversation.value = it
            }
        }
        viewModelScope.launch {
            conversationIdState.flatMapConcat {
                if (it != 0L) {
                    chatClient.messages(it)
                } else {
                    emptyFlow()
                }
            }.collect {
                val lastMessage = it.lastOrNull()
                val oldLastMessage = messages.value.lastOrNull()
                if (lastMessage?.id != oldLastMessage?.id) {
                    // new message receive, scroll to bottom
                    scrollCommandState.value = ScrollCommand.ScrollTo(
                        it.size
                    )
                } else if (lastMessage != oldLastMessage) {
                    // content changed,
                    scrollCommandState.value = ScrollCommand.ScrollTo(
                        it.size
                    )
                }
                messages.value = it
            }
        }

    }
    fun conversation(conversation: Long) {
        conversationIdState.value = conversation
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun sendWithAttachmentId(message: String, attachments: List<Long>) {
        val messageDecoded = Base64.decode(message)
            .decodeToString()
        send(InputValue.TextInputValue(messageDecoded), emptyList())
    }

    fun send(input: InputValue, attachments: List<Attachment>): Boolean {
        if (!sendMessageState.compareAndSet(false, true)) {
            return false
        }
        viewModelScope.launch {
            val conversationId = conversationIdState.value
            if (conversationId == 0L) {
                return@launch
            }
            chatClient.send(
                Message(
                    conversation = conversationIdState.value,
                    content = input.asText(),
                    type = Message.MessageType.TypeHuman
                )
            ).collect {
                when (it.status) {
                    Message.MessageStatus.StatusSuccess -> {
                        sendMessageState.value = false
                    }
                    Message.MessageStatus.StatusError -> {
                        sendMessageState.value = false
                    }
                    Message.MessageStatus.StatusStopped -> {
                        sendMessageState.value = false

                    }
                    else -> {
                        sendMessageState.value = true
                    }
                }
            }
        }.invokeOnCompletion {
            sendMessageState.value = false
        }
        return true
    }

    fun stop() {

    }

    fun retry() {

    }


    sealed interface ScrollCommand {

        object Noop: ScrollCommand

        // cannot use data class
        class ScrollTo(val index: Int, val offset: Int = 0, stickUntilScrolled: Boolean = true): ScrollCommand

    }
}