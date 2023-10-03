package com.example.androidcopilot.ui.chat.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidcopilot.chat.ChatClient
import com.example.androidcopilot.chat.model.Conversation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatConversationViewModel  @Inject constructor (
    private val client: ChatClient
): ViewModel() {

    val currentConversation = MutableStateFlow<Conversation?>(null)
    val conversations = MutableStateFlow<List<Conversation>>(emptyList())

    init {
        viewModelScope.launch {
            client.conversations()
                .stateIn(this)
                .collect {
                    conversations.value = it
                }
        }
    }

    fun onNewConversation() {

    }

    fun onSwitchConversation(conversation: Conversation) {

    }

    fun onOpenSetting() {

    }


}