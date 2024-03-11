package com.example.androidcopilot.chat.repository.local

import androidx.paging.PagingSource
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.chat.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class LocalChatRepository(private val roomChatDao: RoomChatDao) : ChatRepository {

    override fun conversationListFlow(): Flow<List<Conversation>> {
        return roomChatDao.watchConversations(
            listOf(
                Conversation.ConversationType.TypePersistent
            )
        )
    }

    override suspend fun conversations(offset: Int, limit: Int): List<Conversation> {
        return roomChatDao.findConversations(offset, limit)
    }

    override fun conversation(id: String): Flow<Conversation> {
        return roomChatDao.watchConversation(id)
    }

    override suspend fun newConversation(conversation: Conversation): Conversation {
        roomChatDao.newConversation(conversation)
        return roomChatDao.findConversationById(conversation.id)!!
    }

    override suspend fun findEmptyConversationOrNewConversation(conversation: Conversation): Conversation {
        return roomChatDao.findEmptyConversationOrNewConversation(conversation)
    }

    override suspend fun deleteConversation(conversation: Conversation): Conversation? {
        val deleted = roomChatDao.findConversationById(conversation.id)
        roomChatDao.deleteConversation(conversation)
        return deleted
    }

    override suspend fun updateConversationTitle(conversationId: String, title: String) {
        roomChatDao.updateConversationTitle(conversationId, title)
    }

    override suspend fun updateConversationType(
        conversationId: String,
        type: Conversation.ConversationType
    ) {
        roomChatDao.updateConversationType(conversationId, type)
    }

    override suspend fun updateConversation(conversation: Conversation) {
        roomChatDao.updateConversation(conversation)
    }

    override suspend fun findConversationById(id: String): Conversation? {
        return roomChatDao.findConversationById(id)
    }

    override fun messageListFlow(conversationId: String): Flow<List<Message>> {
        return roomChatDao.watchConversationMessages(conversationId, 0, Int.MAX_VALUE)
    }


    override suspend fun newMessage(message: Message, attachments: List<Attachment>): Message {
        roomChatDao.newMessage(message)
        return roomChatDao.findMessage(message.id)!!
    }

    override suspend fun updateMessage(message: Message): Message {
        roomChatDao.updateMessage(message)
        return roomChatDao.findMessage(message.id)!!
    }

    override suspend fun updateMessageStatusAndContent(
        messageId: String,
        status: Message.MessageStatus,
        content: String
    ) {
        roomChatDao.updateMessageStatusAndContent(messageId, status, content)
    }

    override suspend fun deleteMessage(message: Message): Message? {
        val deleted = roomChatDao.findMessage(message.id)
        roomChatDao.deleteMessage(message)
        return deleted
    }

    override suspend fun findMessageById(id: String): Message? {
        return roomChatDao.findMessage(id)
    }

    override suspend fun newAttachment(attachment: Attachment): Attachment? {
        val id = roomChatDao.newAttachments(listOf(attachment)).firstOrNull() ?: return null
        return roomChatDao.findAttachmentById(attachment.id)
    }

    override suspend fun newAttachments(attachments: List<Attachment>): List<Attachment> {
        roomChatDao.newAttachments(attachments)
        return attachments.mapNotNull { roomChatDao.findAttachmentById(it.id) }
    }

    override suspend fun deleteAttachment(attachment: Attachment): Attachment? {
        val deleted = roomChatDao.findAttachmentById(attachment.id)
        roomChatDao.deleteAttachment(attachment)
        return deleted
    }

    override suspend fun updateAttachment(attachment: Attachment): Attachment? {
        roomChatDao.updateAttachment(attachment)
        return roomChatDao.findAttachmentById(attachment.id)
    }

    override suspend fun findAttachmentById(id: String): Attachment? {
        return roomChatDao.findAttachmentById(id)
    }

    override suspend fun findAttachmentByMessage(id: String): List<Attachment> {
        return roomChatDao.findMessageAttachment(id)
    }

    override fun conversationPagingSource(): PagingSource<Int, Conversation> {
        return roomChatDao.conversationPagingSource()
    }

    override suspend fun findConversationMessages(
        conversationId: String,
        limit: Int,
        offset: Int
    ): List<Message> {
        return roomChatDao.findMessagesByConversations(conversationId, limit, offset)
    }

}