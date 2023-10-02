package com.example.androidcopilot.chat.repository.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import kotlinx.coroutines.flow.Flow

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

    @Query("select * from Conversation order by updateAt desc")
    fun conversationsFlow(): Flow<List<Conversation>>

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
}