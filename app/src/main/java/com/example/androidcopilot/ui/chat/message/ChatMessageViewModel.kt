package com.example.androidcopilot.ui.chat.message

import androidx.lifecycle.ViewModel
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.ui.chat.input.InputMode
import com.example.androidcopilot.ui.chat.input.MessageInputState
import com.example.androidcopilot.ui.chat.input.SendState
import kotlinx.coroutines.flow.MutableStateFlow

class ChatMessageViewModel: ViewModel() {


    val conversation: MutableStateFlow<Conversation> = MutableStateFlow(
        Conversation(
            0,
            "Android Copilot",
            0,
            0,
            0,
            0,
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