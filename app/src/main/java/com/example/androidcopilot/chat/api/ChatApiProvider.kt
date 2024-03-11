package com.example.androidcopilot.chat.api

import com.example.androidcopilot.chat.model.Conversation

interface ChatApiProvider {

    fun create(conversation: Conversation): Result<ChatApi>

}