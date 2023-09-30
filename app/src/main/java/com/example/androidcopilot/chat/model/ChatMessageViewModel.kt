package com.example.androidcopilot.chat.model

import androidx.lifecycle.ViewModel
import com.example.androidcopilot.ui.chat.InputMode
import com.example.androidcopilot.ui.chat.SendState
import kotlinx.coroutines.flow.MutableStateFlow

class ChatMessageViewModel: ViewModel() {

    val messageList = MutableStateFlow<List<ChatMessage>>(
        emptyList()
    )

    val inputText = MutableStateFlow("")
    val inputMode = MutableStateFlow(InputMode.TextInput)
    val inputHint = MutableStateFlow("Ask me anything...")
    val inputSendState = MutableStateFlow(SendState.StateSend)

    fun switchInput(mode: InputMode) {
        inputMode.value = mode
    }

    fun send() {

    }

    fun pause() {

    }

    fun retry() {

    }

    fun input(text: String) {
        inputText.value = text
    }
}