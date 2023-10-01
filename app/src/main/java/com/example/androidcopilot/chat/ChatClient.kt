package com.example.androidcopilot.chat

import com.example.androidcopilot.chat.model.ChatConversation
import com.example.androidcopilot.chat.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatClient {

    suspend fun send(message: ChatMessage): Flow<ChatMessage>

    fun messages(): Flow<List<ChatMessage>>

    fun conversation(): Flow<ChatConversation>

}