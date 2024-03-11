package com.example.androidcopilot.chat.provider

import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.provider.ChatApi

interface ChatApiProvider {

    fun create(conversation: Conversation): Result<ChatApi>

}