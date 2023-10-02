package com.example.androidcopilot.chat

import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatClient {

    suspend fun newConversation(): Result<Conversation>

    suspend fun updateConversation(conversation: Conversation): Result<Conversation>

    suspend fun deleteConversation(conversation: Conversation): Result<Conversation>

    suspend fun send(message: Message): Flow<Message>

    fun messages(conversation: Conversation): Flow<List<Message>>

    fun conversations(): Flow<List<Conversation>>

    fun conversation(conversationId: Long): Flow<Conversation>
}