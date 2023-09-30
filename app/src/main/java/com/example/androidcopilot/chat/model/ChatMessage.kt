package com.example.androidcopilot.chat.model
sealed interface ChatMessage {

    fun id(): Long
    
    data class AssistantMessage(
        val id: Long,
        val message: String
    ): ChatMessage {
        override fun id() = id
    }

    data class HumanMessage(
        val id: Long,
        val message: String
    ): ChatMessage {
        override fun id() = id

    }

    data class SystemMessage(
        val id: Long,
        val message: String,
    ): ChatMessage {
        override fun id() = id
    }
}