package com.example.androidcopilot.ui.chat.conversation

import androidx.lifecycle.ViewModel
import com.example.androidcopilot.chat.model.Conversation
import kotlinx.coroutines.flow.MutableStateFlow

class ChatConversationViewModel: ViewModel() {

    val currentConversation = MutableStateFlow<Conversation?>(null)
    val conversations = MutableStateFlow<List<Conversation>>(emptyList())

    fun onNewConversation() {

    }

    fun onSwitchConversation(conversation: Conversation) {

    }

    fun onOpenSetting() {

    }


}