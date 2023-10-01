package com.example.androidcopilot.chat.model

import androidx.lifecycle.ViewModel
import com.example.androidcopilot.ui.chat.Attachment
import com.example.androidcopilot.ui.chat.InputMode
import com.example.androidcopilot.ui.chat.MessageInputState
import com.example.androidcopilot.ui.chat.SendState
import kotlinx.coroutines.flow.MutableStateFlow

class ChatMessageViewModel: ViewModel() {

    val conversationList = MutableStateFlow<List<ChatConversation>>(
        emptyList()
    )

    val conversation = MutableStateFlow(
        ChatConversation(
            0,
            "Android Copilot",
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
        )
    )
    val messageList = MutableStateFlow<List<ChatMessage>>(
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

    fun switchInput(mode: InputMode) {

    }

    fun send(message: String, attachments: List<Attachment>) {

    }

    fun pause() {

    }

    fun retry() {

    }

    fun input(text: String) {

    }
}