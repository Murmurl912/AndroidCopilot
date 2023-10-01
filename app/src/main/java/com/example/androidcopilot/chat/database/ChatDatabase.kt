package com.example.androidcopilot.chat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidcopilot.chat.model.ChatAttachment
import com.example.androidcopilot.chat.model.ChatConversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.chat.repository.local.RoomChatDao


@Database(
    entities = [
        Message::class,
        ChatConversation::class,
        ChatAttachment::class
    ],
    version = 1
)
abstract class ChatDatabase: RoomDatabase() {

    abstract fun chatDao(): RoomChatDao
}