package com.example.androidcopilot.chat.database

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.Room
import androidx.room.RoomDatabase
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
                // trigger for message count update
                db.execSQL(
                    """
                CREATE TRIGGER update_message_count
                AFTER INSERT ON Message
                BEGIN
                    UPDATE Conversation
                    SET messageCount = messageCount + 1
                    WHERE id = NEW.conversation;
                END;
                
                CREATE TRIGGER update_message_count_on_delete
                AFTER DELETE ON Message
                BEGIN
                    UPDATE Conversation
                    SET messageCount = messageCount - 1
                    WHERE id = OLD.conversation;
                END;
                    """.trimIndent()
                )
            }
        }

        fun create(context: Context) {
            Room.databaseBuilder(
                context,
                ChatDatabase::class.java,
                "chat-database.db"
            ).addCallback(callback).build()
        }
    }
}