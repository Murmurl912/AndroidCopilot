package com.example.androidcopilot.chat.repository.local

import androidx.paging.PagingSource
import com.example.androidcopilot.chat.model.ChatAttachment
import com.example.androidcopilot.chat.model.ChatConversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.chat.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class LocalChatRepository(private val roomChatDao: RoomChatDao): ChatRepository {

    override fun conversationListFlow(): Flow<List<ChatConversation>> {
        return roomChatDao.conversationsFlow()
    }

    override suspend fun conversations(offset: Int, limit: Int): List<ChatConversation> {
        return roomChatDao.findConversations(offset, limit)
    }

    override fun conversation(id: Long): Flow<ChatConversation> {
        return roomChatDao.conversationFlow(id)
    }

    override suspend fun newConversation(conversation: ChatConversation): ChatConversation {
        val id = roomChatDao.newConversation(conversation)
        return roomChatDao.findConversationById(id)!!
    }

    override suspend fun deleteConversation(conversation: ChatConversation): ChatConversation? {
        val deleted = roomChatDao.findConversationById(conversation.id)
        roomChatDao.deleteConversation(conversation)
        return deleted
    }

    override suspend fun updateConversation(conversation: ChatConversation): ChatConversation? {
        roomChatDao.updateConversation(conversation)
        return roomChatDao.findConversationById(conversation.id)
    }

    override suspend fun findConversationById(id: Long): ChatConversation? {
        return roomChatDao.findConversationById(id)
    }

    override fun messageListFlow(conversation: ChatConversation): Flow<List<Message>> {
        return roomChatDao.conversationMessageFlow(conversation.id, 0, Int.MAX_VALUE)
    }

    override suspend fun messages(
        conversation: ChatConversation,
        offset: Int,
        limit: Int
    ): List<Message> {
        return roomChatDao.findConversationMessages(conversation.id, 0, Int.MAX_VALUE)
    }

    override suspend fun newMessage(message: Message): Message {
        val id = roomChatDao.newMessage(message)
        return roomChatDao.findMessage(id)!!
    }

    override suspend fun updateMessage(message: Message): Message? {
        roomChatDao.updateMessage(message)
        return roomChatDao.findMessage(message.id)
    }

    override suspend fun deleteMessage(message: Message): Message? {
        val deleted = roomChatDao.findMessage(message.id)
        roomChatDao.deleteMessage(message)
        return deleted
    }

    override suspend fun findMessageById(id: Long): Message? {
        return roomChatDao.findMessage(id)
    }

    override suspend fun newAttachment(attachment: ChatAttachment): ChatAttachment? {
        val id = roomChatDao.newAttachment(attachment)
        return roomChatDao.findAttachmentById(id)
    }

    override suspend fun deleteAttachment(attachment: ChatAttachment): ChatAttachment? {
        val deleted = roomChatDao.findAttachmentById(attachment.id)
        roomChatDao.deleteAttachment(attachment)
        return deleted
    }

    override suspend fun updateAttachment(attachment: ChatAttachment): ChatAttachment? {
        roomChatDao.updateAttachment(attachment)
        return roomChatDao.findAttachmentById(attachment.id)
    }

    override suspend fun findAttachmentById(id: Long): ChatAttachment? {
        return roomChatDao.findAttachmentById(id)
    }

    override suspend fun findAttachmentByMessage(id: Long): List<ChatAttachment> {
        return roomChatDao.findMessageAttachment(id)
    }

    override fun conversationPagingSource(): PagingSource<Int, ChatConversation> {
        return roomChatDao.conversationPagingSource()
    }

    override fun messagePagingSource(conversation: ChatConversation): PagingSource<Int, Message> {
        return roomChatDao.conversationMessagePagingSource(conversation.id)
    }
}