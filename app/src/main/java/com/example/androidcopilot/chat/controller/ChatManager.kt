package com.example.androidcopilot.chat.controller

import com.example.androidcopilot.chat.controller.api.ChatControllerApi
import com.example.androidcopilot.chat.model.Conversation
import kotlinx.coroutines.flow.Flow

class ChatManager {

    fun conversations(): Flow<List<Conversation>> {
        TODO()
    }

    fun controller(conversationId: String): ChatControllerApi {
        TODO()
    }

}