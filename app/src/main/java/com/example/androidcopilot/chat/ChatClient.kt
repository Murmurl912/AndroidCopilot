package com.example.androidcopilot.chat

import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatClient {

    suspend fun send(message: Message): Flow<Message>

    fun messages(conversation: Conversation): Flow<List<Message>>

    fun conversations(): Flow<List<Conversation>>

}