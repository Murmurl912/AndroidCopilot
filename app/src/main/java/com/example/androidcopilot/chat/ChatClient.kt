package com.example.androidcopilot.chat

import com.example.androidcopilot.chat.model.ChatConversation
import com.example.androidcopilot.chat.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatClient {

    suspend fun newConversation(): Result<ChatConversation>

    suspend fun updateConversation(conversation: ChatConversation): Result<ChatConversation>

    suspend fun deleteConversation(conversation: ChatConversation): Result<ChatConversation>

    suspend fun send(message: Message): Flow<Message>

    fun messages(): Flow<List<Message>>

    fun conversations(): Flow<List<ChatConversation>>

    fun watchConversation(conversationId: Long): Flow<ChatConversation>
}