package com.example.androidcopilot.ui.home

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcopilot.chat.ChatClient
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.navigation.AppScreens
import com.example.androidcopilot.navigation.Navigator
import com.example.androidcopilot.ui.chat.input.InputValue
import com.example.androidcopilot.ui.chat.input.TextSpeechInputState
import com.example.androidcopilot.ui.chat.input.asText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatClient: ChatClient
): ViewModel() {

    private val sendMessageState = MutableStateFlow(false)
    val isSendingMessage = sendMessageState.asStateFlow()

    fun send(input: InputValue, attachments: List<Attachment>): Boolean {
        if (!sendMessageState.compareAndSet(false, true)) {
            return false
        }
        viewModelScope.launch {
            val conversation = chatClient.conversation()
            val message = input.asText()
            Navigator.navigate(
                AppScreens.MessageScreen.createRoute(conversation.id, message, attachments.map { it.id })
            )
            delay(500)
        }
        return true
    }

    fun stop() {

    }

}