package com.example.androidcopilot.service

import com.example.androidcopilot.chat.model.ChatMessage

interface ChatAgent {

    fun send(message: ChatMessage.HumanMessage)



}

