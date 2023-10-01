package com.example.androidcopilot.chat.repository.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.androidcopilot.chat.model.ChatAttachment
import com.example.androidcopilot.chat.model.ChatConversation
import com.example.androidcopilot.chat.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomChatDao {

    @Insert
    suspend fun newConversation(conversation: ChatConversation): Long

    @Delete
    suspend fun deleteConversation(conversation: ChatConversation): Int

    @Update
    suspend fun updateConversation(conversation: ChatConversation): Int

    @Query("select * from ChatConversation where id = :id")
    suspend fun findConversationById(id: Long): ChatConversation?

    @Query("select * from ChatConversation where id = :id")
    fun conversationFlow(id: Long): Flow<ChatConversation>

    @Query("select * from ChatConversation order by updateAt desc")
    fun conversationsFlow(): Flow<List<ChatConversation>>

    @Query("select * from ChatConversation order by updateAt desc limit :limit offset :offset")
    suspend fun findConversations(offset: Int, limit: Int): List<ChatConversation>

    @Insert
    suspend fun newMessage(chatMessage: ChatMessage): Long

    @Delete
    suspend fun deleteMessage(chatMessage: ChatMessage): Int

    @Update
    suspend fun updateMessage(chatMessage: ChatMessage): Int

    @Query(
        "select * from ChatMessage " +
            "where conversation =:conversation " +
            "order by createAt desc " +
            "limit :limit offset :offset")
    suspend fun findConversationMessages(conversation: Long, offset: Int, limit: Int): List<ChatMessage>


    @Query(
        "select * from ChatMessage " +
            "where conversation =:conversation " +
            "order by createAt desc " +
            "limit :limit offset :offset")
    fun conversationMessageFlow(conversation: Long, offset: Int, limit: Int): Flow<List<ChatMessage>>

    @Query("select * from ChatMessage where id = :id")
    fun findMessage(id: Long): ChatMessage?


    @Insert
    suspend fun newAttachment(attachment: ChatAttachment): Long

    @Update
    fun updateAttachment(attachment: ChatAttachment): Int

    @Delete
    suspend fun deleteAttachment(attachment: ChatAttachment): Int

    @Query("select * from ChatAttachment where id =:id")
    suspend fun findAttachmentById(id: Long): ChatAttachment?

    @Query("select * from ChatAttachment where messageId = :messageId")
    suspend fun findMessageAttachment(messageId: Long): List<ChatAttachment>

    @Query("select * from ChatConversation order by updateAt desc")
    fun conversationPagingSource(): PagingSource<Int, ChatConversation>

    @Query("select * from ChatMessage where conversation = :id order by createAt desc")
    fun conversationMessagePagingSource(id: Long): PagingSource<Int, ChatMessage>
}