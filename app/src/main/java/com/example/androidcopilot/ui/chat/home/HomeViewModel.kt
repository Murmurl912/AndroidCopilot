package com.example.androidcopilot.ui.chat.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcopilot.chat.ChatClient
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.navigation.AppScreens
import com.example.androidcopilot.navigation.Navigator
import com.example.androidcopilot.ui.chat.input.InputMode
import com.example.androidcopilot.ui.chat.input.MessageInputState
import com.example.androidcopilot.ui.chat.input.SendState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatClient: ChatClient
): ViewModel() {

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
            val conversation = chatClient.conversation()
            Navigator.navigate(
                AppScreens.MessageScreen.createRoute(conversation.id, message, attachments.map { it.id })
            )
            delay(500)
            inputState.update {
                it.copy(sendState = SendState.StateIdle, attachments = emptyList())
            }
        }
    }

}