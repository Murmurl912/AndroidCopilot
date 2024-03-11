package com.example.androidcopilot.chat.provider.openai

import com.aallam.openai.client.OpenAI
import com.example.androidcopilot.chat.provider.ChatApi
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import kotlinx.coroutines.flow.Flow

class OpenaiChatApi(
    private val openAi: OpenAI,
): ChatApi {
    override suspend fun generate(
        request: Message,
        response: Message,
        conversation: Conversation,
        messages: List<Message>
    ): Flow<Message> {
        TODO("Not yet implemented")
    }

    override suspend fun generateTitle(
        conversation: Conversation,
        messages: List<Message>
    ): Flow<String> {
        TODO("Not yet implemented")
    }

}