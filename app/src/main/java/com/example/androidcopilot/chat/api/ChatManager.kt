package com.example.androidcopilot.chat.api

import com.example.androidcopilot.chat.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ChatManager {

    fun conversations(): Flow<List<Conversation>>

    suspend fun updateConversation(conversation: Conversation)

    suspend fun conversation(id: String): Conversation?

    suspend fun newConversation(conversation: Conversation): Conversation

    suspend fun deleteConversation(conversation: Conversation): Conversation?

    fun client(conversationId: String): ChatControllerApi

}