package com.example.androidcopilot.ui.chat.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.ui.chat.input.InputMode
import com.example.androidcopilot.ui.chat.input.MessageInputState
import com.example.androidcopilot.ui.chat.input.SendState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

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

    }

    fun pause() {

    }

    fun retry() {

    }
}