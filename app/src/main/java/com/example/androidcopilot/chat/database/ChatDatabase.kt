package com.example.androidcopilot.chat.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.androidcopilot.app.ApplicationDependencies
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

    companion object {

        private val callback = object: Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }

        fun create(context: Context): ChatDatabase {
            return Room.databaseBuilder(
                context,
                ChatDatabase::class.java,
                "chat-database.db"
            ).addCallback(callback).build()
        }
    }
}