package com.example.androidcopilot.chat.repository.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Dao
interface RoomChatDao {

    @Insert
    suspend fun newConversation(conversation: Conversation): Long

    @Delete
    suspend fun deleteConversation(conversation: Conversation): Int

    @Update
    suspend fun updateConversation(conversation: Conversation): Int

    @Query("select * from Conversation where id = :id")
    suspend fun findConversationById(id: Long): Conversation?

    @Query("select * from Conversation where id = :id")
    fun conversationFlow(id: Long): Flow<Conversation>

    @Query("select * from Conversation where type in (:types) order by updateAt desc")
    fun conversationsFlow(types: List<Conversation.ConversationType>): Flow<List<Conversation>>

    @Query("select * from Conversation order by updateAt desc limit :limit offset :offset")
    suspend fun findConversations(offset: Int, limit: Int): List<Conversation>

    @Insert
    suspend fun newMessage(message: Message): Long

    @Delete
    suspend fun deleteMessage(message: Message): Int

    @Update
    suspend fun updateMessage(message: Message): Int

    @Query(
        "select * from Message " +
            "where conversation =:conversation " +
            "order by createAt desc " +
            "limit :limit offset :offset")
    suspend fun findConversationMessages(conversation: Long, offset: Int, limit: Int): List<Message>


    @Query(
        "select * from Message " +
            "where conversation =:conversation " +
            "order by createAt desc " +
            "limit :limit offset :offset")
    fun conversationMessageFlow(conversation: Long, offset: Int, limit: Int): Flow<List<Message>>

    @Query("select * from Message where id = :id")
    fun findMessage(id: Long): Message?

    @Query("update Message set status = :status, content = :content where id = :messageId")
    suspend fun updateMessageStatusAndContent(messageId: Long, status: Message.MessageStatus, content: String)

    @Query("update Message set status = :status, token = :token, child = :child where id = :messageId")
    suspend fun updateMessageStatusAndToken(
        messageId: Long,
        status: Message.MessageStatus,
        token: Int,
        child: Long
    )

    @Insert
    suspend fun newAttachment(attachment: Attachment): Long

    @Update
    fun updateAttachment(attachment: Attachment): Int

    @Delete
    suspend fun deleteAttachment(attachment: Attachment): Int

    @Query("select * from Attachment where id =:id")
    suspend fun findAttachmentById(id: Long): Attachment?

    @Query("select * from Attachment where messageId = :messageId")
    suspend fun findMessageAttachment(messageId: Long): List<Attachment>

    @Query("select * from Conversation order by updateAt desc")
    fun conversationPagingSource(): PagingSource<Int, Conversation>

    @Query("select * from Message where conversation = :id order by createAt desc")
    fun conversationMessagePagingSource(id: Long): PagingSource<Int, Message>

    @Transaction
    suspend fun tryLockConversation(conversationId: Long): Conversation?  = withContext(NonCancellable) {
        val currentConversation = findConversationById(conversationId)?.takeIf {
            it.status == Conversation.ConversationStatus.StatusUnlocked
        }?: return@withContext null

        val updateCount = updateConversation(currentConversation)
        if (updateCount > 0) {
            currentConversation
        } else {
            null
        }
    }

    @Transaction
    suspend fun unlockConversation(conversationId: Long) {
        findConversationById(conversationId)?.copy(status = Conversation.ConversationStatus.StatusUnlocked)?.let {
                updateConversation(it)
            }
    }

    @Query("select COALESCE(sum(token),0) " +
            "from Message " +
            "where conversation =:conversationId " +
            "and status in (:messageStatuses)" +
            "order by createAt desc " +
            "limit :size"
    )
    suspend fun sumMessageToken(
        conversationId: Long,
        size: Int,
        messageStatuses: List<Message.MessageStatus>
    ): Int

    @Transaction
    suspend fun updateConversationContextLimit(conversationId: Long): Conversation? {
        val conversation = findConversationById(conversationId) ?: return null
        val tokenLimit = conversation.contextTokenSizeLimit
        var lowerLimit = 0
        var upperLimit = conversation.messageCount

        var middle = conversation.contextMessageOffset
        var currentSize: Int = conversation.contextTokenSize
        do {
            sumMessageToken(
                conversationId,
                middle,
                listOf(Message.MessageStatus.StatusSuccess)
            )
            if (currentSize > tokenLimit) {
                upperLimit = middle - 1
            } else {
                lowerLimit = middle + 1
            }
            middle = (upperLimit + lowerLimit) / 2
        } while (lowerLimit < upperLimit)
        updateConversation(conversation.copy(
            contextMessageOffset = lowerLimit,
            contextTokenSize = currentSize
        ))
        return findConversationById(conversationId)
    }

    suspend fun contextMessages(conversationId: Long): List<Message> {
        val conversation = findConversationById(conversationId)?: return emptyList()
        return findConversationMessages(conversationId, 0, conversation.contextMessageOffset)
    }

    @Query("select * from Conversation where messageCount == 0 limit 1")
    fun findEmptyConversation(): Conversation?

    @Transaction
    suspend fun findEmptyConversationOrNewConversation(conversation: Conversation): Conversation {
        val emptyConversation = findEmptyConversation()
        if (emptyConversation != null) {
            return emptyConversation
        }
        val id = newConversation(conversation)
        return findConversationById(id)!!
    }

    @Query("update Conversation set title = :title where id = :conversationId")
    suspend fun updateConversationTitle(conversationId: Long, title: String)

    @Query("update Conversation set type = :type where id = :conversationId")
    suspend fun updateConversationType(conversationId: Long, type: Conversation.ConversationType)
}