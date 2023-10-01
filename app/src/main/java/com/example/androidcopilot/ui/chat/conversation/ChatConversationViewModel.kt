package com.example.androidcopilot.ui.chat.conversation

import androidx.lifecycle.ViewModel
import com.example.androidcopilot.chat.model.ChatConversation
import kotlinx.coroutines.flow.MutableStateFlow

class ChatConversationViewModel: ViewModel() {

    val currentConversation = MutableStateFlow<ChatConversation?>(null)
    val conversations = MutableStateFlow<List<ChatConversation>>(emptyList())

    fun onNewConversation() {

    }

    fun onSwitchConversation(conversation: ChatConversation) {

    }

    fun onOpenSetting() {

    }


}