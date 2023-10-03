package com.example.androidcopilot.ui.chat.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcopilot.chat.ChatClient
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.ui.chat.input.InputMode
import com.example.androidcopilot.ui.chat.input.MessageInputState
import com.example.androidcopilot.ui.chat.input.SendState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    val inputState = MutableStateFlow(
        MessageInputState(
            "",
            InputMode.TextInput,
            SendState.StateIdle,
            emptyList()
        )
    )

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
                messages.value = it
            }
        }

    }
    fun conversation(conversation: Long) {
        conversationIdState.value = conversation
    }

    fun mode(mode: InputMode) {
        inputState.update {
            it.copy(mode = mode)
        }
    }

    fun input(text: String) {
        inputState.update {
            it.copy(input = text)
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun sendWithAttachmentId(message: String, attachments: List<Long>) {
        val messageDecoded = Base64.decode(message)
            .decodeToString()
        send(messageDecoded, emptyList())
    }

    fun send(message: String, attachments: List<Attachment>) {
        viewModelScope.launch {
            val oldState = inputState.value
            if (oldState.sendState == SendState.StateSending) {
                return@launch
            }
            val conversationId = conversationIdState.value
            if (conversationId == 0L) {
                inputState.update {
                    it.copy(input = "", attachments = emptyList())
                }
                return@launch
            }
            inputState.update {
                it.copy(input = "", sendState = SendState.StateSending, attachments = emptyList())
            }
            chatClient.send(
                Message(
                    conversation = conversationIdState.value,
                    content = message,
                    type = Message.MessageType.TypeHuman
                )
            ).collect {
                when (it.status) {
                    Message.MessageStatus.StatusSuccess -> {
                        inputState.update {
                            it.copy(sendState = SendState.StateIdle)
                        }
                    }
                    Message.MessageStatus.StatusError -> {
                        inputState.update {
                            it.copy(sendState = SendState.StateError)
                        }
                    }
                    Message.MessageStatus.StatusStopped -> {
                        inputState.update {
                            it.copy(sendState = SendState.StatePause)
                        }
                    }
                    else -> {
                        inputState.update {
                            it.copy(sendState = SendState.StateSending)
                        }
                    }
                }
            }
        }
    }

    fun pause() {

    }

    fun retry() {

    }

}