package com.example.androidcopilot.chat.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidcopilot.chat.model.Attachment
import com.example.androidcopilot.chat.model.Conversation
import com.example.androidcopilot.chat.model.Message
import com.example.androidcopilot.chat.repository.local.RoomChatDao


@Database(
    entities = [
        Message::class,
        Conversation::class,
        Attachment::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChatDatabase: RoomDatabase() {

    abstract fun chatDao(): RoomChatDao
}