package com.example.androidcopilot.chat

import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.example.androidcopilot.BuildConfig
import com.example.androidcopilot.app.ApplicationDependencies
import com.example.androidcopilot.chat.storage.database.ChatDatabase
import com.example.androidcopilot.chat.storage.repository.ChatRepository
import com.example.androidcopilot.chat.storage.repository.local.LocalChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    fun provideChatDatabase(): ChatDatabase {
        return ChatDatabase.create(ApplicationDependencies.applicationContext)
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

}