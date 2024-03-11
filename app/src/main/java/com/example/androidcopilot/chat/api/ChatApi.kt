package com.example.androidcopilot.chat.api

import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatApi {

    /**
     * send a message to llm and return a flow of generated response
     */
    suspend fun generate(
        request: Message,
        response: Message,
        conversation: Conversation,
        messages: List<Message>
    ): Flow<Message>

    suspend fun generateTitle(conversation: Conversation, messages: List<Message>): Flow<String>

}
