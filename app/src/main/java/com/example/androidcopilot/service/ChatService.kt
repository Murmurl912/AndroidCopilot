package com.example.androidcopilot.service

import com.example.androidcopilot.chat.model.ChatMessage

interface ChatAgent {

    suspend fun send(message: ChatMessage.HumanMessage): ChatMessage


}

