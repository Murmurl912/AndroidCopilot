package com.example.androidcopilot.chat.storage.repository.local

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
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomChatDao {

    @Insert
    suspend fun newConversation(conversation: Conversation)

    @Delete
    suspend fun deleteConversation(conversation: Conversation): Int

    @Update
    suspend fun updateConversation(conversation: Conversation): Int

    @Query("select * from Conversation where id = :id")
    suspend fun findConversationById(id: String): Conversation?

    @Query("select * from Conversation where id = :id")
    fun watchConversation(id: String): Flow<Conversation>

    @Query("select * from Conversation where type in (:types) order by updateAt desc")
    fun watchConversations(types: List<Conversation.ConversationType>): Flow<List<Conversation>>

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
                "limit :limit offset :offset"
    )
    suspend fun findMessagesByConversations(
        conversation: String,
        offset: Int,
        limit: Int
    ): List<Message>


    @Query(
        "select * from Message " +
                "where conversation =:conversation " +
                "order by createAt desc " +
                "limit :limit offset :offset"
    )
    fun watchConversationMessages(
        conversation: String,
        offset: Int,
        limit: Int
    ): Flow<List<Message>>

    @Query("select * from Message where id = :id")
    fun findMessage(id: String): Message?

    @Query("update Message set status = :status, content = :content where id = :messageId")
    suspend fun updateMessageStatusAndContent(
        messageId: String,
        status: Message.MessageStatus,
        content: String
    )

    @Insert
    suspend fun newAttachments(attachment: List<Attachment>): List<Long>

    @Update
    fun updateAttachment(attachment: Attachment): Int

    @Delete
    suspend fun deleteAttachment(attachment: Attachment): Int

    @Query("select * from Attachment where id =:id")
    suspend fun findAttachmentById(id: String): Attachment?

    @Query("select * from Attachment where message = :messageId")
    suspend fun findMessageAttachment(messageId: String): List<Attachment>

    @Query("select * from Conversation order by updateAt desc")
    fun conversationPagingSource(): PagingSource<Int, Conversation>

    @Query("select * from Message where conversation = :id order by createAt desc")
    fun conversationMessagePagingSource(id: Long): PagingSource<Int, Message>

    @Query("select * from Conversation where messageCount == 0 limit 1")
    fun findEmptyConversation(): Conversation?

    @Transaction
    suspend fun findEmptyConversationOrNewConversation(conversation: Conversation): Conversation {
        val emptyConversation = findEmptyConversation()
        if (emptyConversation != null) {
            return emptyConversation
        }
        val id = newConversation(conversation)
        return findConversationById(conversation.id)!!
    }

    @Query("update Conversation set title = :title where id = :conversationId")
    suspend fun updateConversationTitle(conversationId: String, title: String)

    @Query("update Conversation set type = :type where id = :conversationId")
    suspend fun updateConversationType(conversationId: String, type: Conversation.ConversationType)
}