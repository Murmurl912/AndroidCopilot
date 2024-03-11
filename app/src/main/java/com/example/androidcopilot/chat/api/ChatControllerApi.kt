package com.example.androidcopilot.chat.api

import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import kotlinx.coroutines.flow.StateFlow

interface ChatControllerApi {

    val conversation: StateFlow<Conversation>

    val messages: StateFlow<List<Message>>

    suspend fun send(message: Message, attachments: List<Attachment>)

    suspend fun stop(message: Message)

    suspend fun retry(message: Message)

}