package com.example.androidcopilot.chat.model

import android.app.people.ConversationStatus

data class ChatConversation(
    val id: Long,
    val title: String,
    val createAt: Long,
    val updateAt: Long,
    val messageCount: Long,
    val tokenCount: Long,
    val token4kMessageId: Long,
    val token8kMessageId: Long,
    val token16KMessageId: Long,
    val token32kMessageId: Long,
    val latestMessageId: Long,

    val currentRequestMessageId: Long,
    val currentResponseMessageId: Long,
) {

}

