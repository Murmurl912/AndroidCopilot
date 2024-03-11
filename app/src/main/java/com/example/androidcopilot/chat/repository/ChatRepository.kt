package com.example.androidcopilot.chat.repository

import androidx.paging.PagingSource
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun conversationListFlow(): Flow<List<Conversation>>

    suspend fun conversations(offset: Int, limit: Int): List<Conversation>

    fun conversation(id: String): Flow<Conversation>

    suspend fun newConversation(conversation: Conversation): Conversation

    suspend fun findEmptyConversationOrNewConversation(conversation: Conversation): Conversation

    suspend fun deleteConversation(conversation: Conversation): Conversation?

    suspend fun updateConversationTitle(conversationId: String, title: String)

    suspend fun updateConversationType(conversationId: String, type: Conversation.ConversationType)

    suspend fun updateConversation(conversation: Conversation)

    suspend fun findConversationById(id: String): Conversation?

    fun messageListFlow(conversationId: String): Flow<List<Message>>


    suspend fun newMessage(message: Message, attachments: List<Attachment>): Message

    suspend fun updateMessage(message: Message): Message

    suspend fun updateMessageStatusAndContent(
        messageId: String,
        status: Message.MessageStatus,
        content: String
    )


    suspend fun deleteMessage(message: Message): Message?

    suspend fun findMessageById(id: String): Message?

    suspend fun newAttachment(attachment: Attachment): Attachment?

    suspend fun newAttachments(attachments: List<Attachment>): List<Attachment>

    suspend fun deleteAttachment(attachment: Attachment): Attachment?

    suspend fun updateAttachment(attachment: Attachment): Attachment?

    suspend fun findAttachmentById(id: String): Attachment?

    suspend fun findAttachmentByMessage(id: String): List<Attachment>

    fun conversationPagingSource(): PagingSource<Int, Conversation>


    suspend fun findConversationMessages(conversationId: String, limit: Int = Int.MAX_VALUE, offset: Int = 0): List<Message>

}
