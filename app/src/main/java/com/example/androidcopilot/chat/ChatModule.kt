package com.example.androidcopilot.chat

import androidx.room.Room
import androidx.room.RoomDatabase
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import com.example.androidcopilot.BuildConfig
import com.example.androidcopilot.app.ApplicationDependencies
import com.example.androidcopilot.chat.database.ChatDatabase
import com.example.androidcopilot.chat.openai.OpenaiChatClient
import com.example.androidcopilot.chat.repository.ChatRepository
import com.example.androidcopilot.chat.repository.local.LocalChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    fun provideChatDatabase(): ChatDatabase {
        return Room.databaseBuilder(
            ApplicationDependencies.applicationContext,
            ChatDatabase::class.java,
            "chat-database.db"
        ).build()
    }

    @Provides
    fun provideOpenAi(): OpenAI {
        return OpenAI(
            token = BuildConfig.OPENAI_TOKEN,
            host = OpenAIHost(
               BuildConfig.OPENAI_API
            )
        )
    }

    @Provides
    fun chatRepository(
        chatDatabase: ChatDatabase,
    ): ChatRepository {
        return LocalChatRepository(
            chatDatabase.chatDao()
        )
    }

    @Provides
    fun chatClient(
        openai: OpenAI,
        chatRepository: ChatRepository
    ): ChatClient {
        return OpenaiChatClient(
            openai,
            chatRepository
        )
    }
}