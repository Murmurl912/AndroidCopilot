package com.example.androidcopilot.chat.model
sealed interface ChatMessage {

    fun id(): Long
    
    data class AssistantMessage(
        val id: Long,
        val conversation: Long,
        val parent: Long,
        val message: String,
        val token: Int,
        val createAt: Long,
        val status: Status
    ): ChatMessage {
        override fun id() = id
    }

    data class HumanMessage(
        val id: Long,
        val conversation: Long,
        val parent: Long,
        val message: String,
        val token: Int,
        val createAt: Long,
        val status: Status,
    ): ChatMessage {
        override fun id() = id

    }

    data class FunctionCallRequestMessage(
        val id: Long,
        val conversation: Long,
        val parent: Long,
        val message: String,
        val functionName: String,
        val functionArgs: String,
        val createAt: Long,
        val token: Int,
        val status: Status
    ): ChatMessage {

        override fun id() = id

    }

    data class FunctionCallResponseMessage(
        val id: Long,
        val conversation: Long,
        val parent: Long,
        val message: String,
        val functionName: String,
        val functionArgs: String,
        val functionResponse: String,
        val createAt: Long,
        val token: Int,
        val status: Status
    ): ChatMessage {

        override fun id() = id

    }

    data class SystemMessage(
        val id: Long,
        val conversation: Long,
        val parent: Long,
        val message: String,
        val token: Int,
        val createAt: Long
    ): ChatMessage {
        override fun id() = id
    }

    sealed interface Status {
        object StatusSuccess: Status

        object StatusRequesting: Status

        object StatusReceiving: Status

        object StatusStopped: Status

        object StatusError: Status
    }
}