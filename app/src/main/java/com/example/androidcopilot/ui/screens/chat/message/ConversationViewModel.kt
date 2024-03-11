package com.example.androidcopilot.ui.screens.chat.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcopilot.chat.api.ChatManager
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.ui.screens.chat.input.InputValue
import com.example.androidcopilot.ui.screens.chat.input.asText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(private val conversationManager: ChatManager) : ViewModel() {

    private val conversationIdState: MutableStateFlow<Long?> = MutableStateFlow(null)
    val conversation: MutableStateFlow<Conversation?> = MutableStateFlow(null)
    val messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    private val sendMessageState = MutableStateFlow(false)
    val isSendingMessage = sendMessageState.asStateFlow()

    init {
        watchMessages()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun watchMessages() {
        viewModelScope.launch {
            conversationIdState.flatMapConcat {
                if (it != null) {
                    conversationManager.conversation(it)
                } else {
                    emptyFlow()
                }
            }.collect {
                conversation.value = it
            }
        }
        viewModelScope.launch {
            conversationIdState.flatMapConcat {
                if (it != null) {
                    conversationManager.messages(it)
                } else {
                    emptyFlow()
                }
            }.collect {
                messages.value = it
            }
        }

    }

    fun switchConversation(conversationId: Long) {
        conversationIdState.value = conversationId
    }

    fun send(input: InputValue, attachments: List<Attachment>): Boolean {
        val conversation = conversation.value
            ?: // create conversation
            return false
        if (!sendMessageState.compareAndSet(false, true)) {
            return false
        }
        viewModelScope.launch {
            sendChecked(conversation, input.asText(), attachments)
        }.invokeOnCompletion {
            sendMessageState.value = false
        }
        return true
    }

    private suspend fun sendChecked(conversation: Conversation, input: String, attachments: List<Attachment>) {
        conversationManager.send(
            Message(
                conversation = conversation.id,
                content = input,
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
    }

    fun stop() {

    }

    fun retry() {

    }


    fun onNewConversation() {

    }

    fun onDeleteConversation() {

    }

}