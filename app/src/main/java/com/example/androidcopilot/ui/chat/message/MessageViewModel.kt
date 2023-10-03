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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel  @Inject constructor (private val chatClient: ChatClient): ViewModel() {

    val conversation: MutableStateFlow<Conversation> = MutableStateFlow(
        Conversation(
            0,
            "Android Copilot",
        )
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

    fun send(message: String, attachments: List<Attachment>) {
        viewModelScope.launch {
            val oldState = inputState.value
            if (oldState.sendState == SendState.StateSending) {
                return@launch
            }
            inputState.update {
                it.copy(input = "", sendState = SendState.StateSending, attachments = emptyList())
            }

            var currentConversation = conversation.value
            if (currentConversation.id == 0L) {
                currentConversation = chatClient.conversation()
            }
            chatClient.send(
                Message(
                    conversation = currentConversation.id,
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