package com.example.androidcopilot.chat.repository

import androidx.paging.PagingSource
import com.example.androidcopilot.chat.model.ChatAttachment
import com.example.androidcopilot.chat.model.ChatConversation
import com.example.androidcopilot.chat.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun conversationListFlow(): Flow<List<ChatConversation>>

    suspend fun conversations(offset: Int, limit: Int): List<ChatConversation>

    fun conversation(id: Long): Flow<ChatConversation>

    suspend fun newConversation(conversation: ChatConversation): ChatConversation?

    suspend fun deleteConversation(conversation: ChatConversation): ChatConversation?

    suspend fun updateConversation(conversation: ChatConversation): ChatConversation?

    suspend fun findConversationById(id: Long): ChatConversation?

    fun messageListFlow(conversation: ChatConversation): Flow<List<ChatMessage>>

    suspend fun messages(conversation: ChatConversation, offset: Int, limit: Int): List<ChatMessage>

    suspend fun newMessage(message: ChatMessage): ChatMessage?

    suspend fun updateMessage(message: ChatMessage): ChatMessage?

    suspend fun deleteMessage(message: ChatMessage): ChatMessage?

    suspend fun findMessageById(id: Long): ChatMessage?

    suspend fun newAttachment(attachment: ChatAttachment): ChatAttachment?

    suspend fun deleteAttachment(attachment: ChatAttachment): ChatAttachment?

    suspend fun updateAttachment(attachment: ChatAttachment): ChatAttachment?

    suspend fun findAttachmentById(id: Long): ChatAttachment?

    suspend fun findAttachmentByMessage(id: Long): List<ChatAttachment>

    fun conversationPagingSource(): PagingSource<Int, ChatConversation>

    fun messagePagingSource(conversation: ChatConversation): PagingSource<Int, ChatMessage>
}