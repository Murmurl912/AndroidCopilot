package com.example.androidcopilot.chat.repository

import androidx.paging.PagingSource
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun tryLockConversation(conversationId: Long): Conversation?

    suspend fun unlockConversation(conversationId: Long)

    suspend fun trimMemoryOffset(conversationId: Long): Conversation?

    fun conversationListFlow(): Flow<List<Conversation>>

    suspend fun conversations(offset: Int, limit: Int): List<Conversation>

    fun conversation(id: Long): Flow<Conversation>

    suspend fun newConversation(conversation: Conversation): Conversation

    suspend fun findEmptyConversationOrNewConversation(conversation: Conversation): Conversation

    suspend fun deleteConversation(conversation: Conversation): Conversation?

    suspend fun updateConversation(conversation: Conversation): Conversation?

    suspend fun findConversationById(id: Long): Conversation?

    fun messageListFlow(conversationId: Long): Flow<List<Message>>

    suspend fun messages(conversation: Conversation, offset: Int, limit: Int): List<Message>

    suspend fun newMessage(message: Message): Message

    suspend fun updateMessage(message: Message): Message?

    suspend fun deleteMessage(message: Message): Message?

    suspend fun findMessageById(id: Long): Message?

    suspend fun newAttachment(attachment: Attachment): Attachment?

    suspend fun deleteAttachment(attachment: Attachment): Attachment?

    suspend fun updateAttachment(attachment: Attachment): Attachment?

    suspend fun findAttachmentById(id: Long): Attachment?

    suspend fun findAttachmentByMessage(id: Long): List<Attachment>

    fun conversationPagingSource(): PagingSource<Int, Conversation>

    fun messagePagingSource(conversation: Conversation): PagingSource<Int, Message>

}