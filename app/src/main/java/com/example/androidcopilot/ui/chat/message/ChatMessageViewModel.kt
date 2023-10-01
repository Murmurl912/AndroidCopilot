package com.example.androidcopilot.ui.chat.message

import androidx.lifecycle.ViewModel
import com.example.androidcopilot.chat.model.ChatAttachment
import com.example.androidcopilot.chat.model.ChatConversation
import com.example.androidcopilot.chat.model.ChatMessage
import com.example.androidcopilot.ui.chat.input.InputMode
import com.example.androidcopilot.ui.chat.input.MessageInputState
import com.example.androidcopilot.ui.chat.input.SendState
import kotlinx.coroutines.flow.MutableStateFlow

class ChatMessageViewModel: ViewModel() {


    val conversation: MutableStateFlow<ChatConversation> = MutableStateFlow(
        ChatConversation(
            0,
            "Android Copilot",
            0,
            0,
            0,
            0,
        )
    )

    val messages: MutableStateFlow<List<ChatMessage>> = MutableStateFlow(
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

    fun send(message: String, attachments: List<ChatAttachment>) {

    }

    fun pause() {

    }

    fun retry() {

    }

    fun input(text: String) {

    }
}