package com.example.androidcopilot.api.chat

import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatClient {

    suspend fun send(message: Message): Flow<Message>

    fun messages(conversationId: Long): Flow<List<Message>>

    suspend fun conversation(): Conversation

    suspend fun conversation(id: Long): Flow<Conversation>

    fun conversations(): Flow<List<Conversation>>

    suspend fun delete(conversation: Conversation)

}