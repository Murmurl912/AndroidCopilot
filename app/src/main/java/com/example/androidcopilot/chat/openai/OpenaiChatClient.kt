package com.example.androidcopilot.chat.openai

import com.aallam.openai.client.OpenAI
import com.example.androidcopilot.chat.model.ChatConversation
import com.example.androidcopilot.chat.model.ChatMessage
import com.example.androidcopilot.chat.ChatClient
import com.example.androidcopilot.chat.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class OpenaiChatClient(
    private val conversation: ChatConversation,
    private val openai: OpenAI,
    private val chatRepository: ChatRepository
): ChatClient {

    private val conversationStateFlow = MutableStateFlow(conversation)


    override suspend fun send(message: ChatMessage): Flow<ChatMessage> {
        TODO()
    }

    override fun messages(): Flow<List<ChatMessage>> {
        return chatRepository.messageListFlow(conversation)
    }

    override fun conversation(): Flow<ChatConversation> = conversationStateFlow
}