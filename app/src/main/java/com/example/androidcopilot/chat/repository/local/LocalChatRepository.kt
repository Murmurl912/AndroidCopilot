package com.example.androidcopilot.chat.repository.local

import androidx.paging.PagingSource
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.chat.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class LocalChatRepository(private val roomChatDao: RoomChatDao): ChatRepository {

    override suspend fun tryLockConversation(conversationId: Long): Conversation? {
        return roomChatDao.tryLockConversation(conversationId)
    }

    override suspend fun unlockConversation(conversationId: Long) {
        roomChatDao.unlockConversation(conversationId)
    }

    override suspend fun refreshContextMessageOffset(conversationId: Long): Conversation? {
        return roomChatDao.updateConversationContextLimit(conversationId)
    }

    override suspend fun contextMessages(conversationId: Long): List<Message> {
        return roomChatDao.contextMessages(conversationId)
    }

    override fun conversationListFlow(): Flow<List<Conversation>> {
        return roomChatDao.conversationsFlow(listOf(
            Conversation.ConversationType.TypePersistent
        ))
    }

    override suspend fun conversations(offset: Int, limit: Int): List<Conversation> {
        return roomChatDao.findConversations(offset, limit)
    }

    override fun conversation(id: Long): Flow<Conversation> {
        return roomChatDao.conversationFlow(id)
    }

    override suspend fun newConversation(conversation: Conversation): Conversation {
        val id = roomChatDao.newConversation(conversation)
        return roomChatDao.findConversationById(id)!!
    }

    override suspend fun findEmptyConversationOrNewConversation(conversation: Conversation): Conversation {
        return roomChatDao.findEmptyConversationOrNewConversation(conversation)
    }

    override suspend fun deleteConversation(conversation: Conversation): Conversation? {
        val deleted = roomChatDao.findConversationById(conversation.id)
        roomChatDao.deleteConversation(conversation)
        return deleted
    }

    override suspend fun updateConversationTitle(conversationId: Long, title: String) {
        roomChatDao.updateConversationTitle(conversationId, title)
    }

    override suspend fun updateConversationType(
        conversationId: Long,
        type: Conversation.ConversationType
    ) {
        roomChatDao.updateConversationType(conversationId, type)
    }

    override suspend fun findConversationById(id: Long): Conversation? {
        return roomChatDao.findConversationById(id)
    }

    override fun messageListFlow(conversationId: Long): Flow<List<Message>> {
        return roomChatDao.conversationMessageFlow(conversationId, 0, Int.MAX_VALUE)
    }

    override suspend fun messages(
        conversation: Conversation,
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

    override suspend fun updateMessageStatusAndContent(
        messageId: Long,
        status: Message.MessageStatus,
        content: String
    ) {
        roomChatDao.updateMessageStatusAndContent(messageId, status, content)
    }

    override suspend fun updateMessageStatusChildAndToken(
        messageId: Long,
        status: Message.MessageStatus,
        token: Int,
        child: Long,
    ) {
        roomChatDao.updateMessageStatusAndToken(messageId, status, token, child)
    }

    override suspend fun deleteMessage(message: Message): Message? {
        val deleted = roomChatDao.findMessage(message.id)
        roomChatDao.deleteMessage(message)
        return deleted
    }

    override suspend fun findMessageById(id: Long): Message? {
        return roomChatDao.findMessage(id)
    }

    override suspend fun newAttachment(attachment: Attachment): Attachment? {
        val id = roomChatDao.newAttachment(attachment)
        return roomChatDao.findAttachmentById(id)
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

    override suspend fun findAttachmentById(id: Long): Attachment? {
        return roomChatDao.findAttachmentById(id)
    }

    override suspend fun findAttachmentByMessage(id: Long): List<Attachment> {
        return roomChatDao.findMessageAttachment(id)
    }

    override fun conversationPagingSource(): PagingSource<Int, Conversation> {
        return roomChatDao.conversationPagingSource()
    }

    override fun messagePagingSource(conversation: Conversation): PagingSource<Int, Message> {
        return roomChatDao.conversationMessagePagingSource(conversation.id)
    }
}