package com.example.androidcopilot.app

import android.content.Context
import com.example.androidcopilot.app.log.AppLogger
import com.example.androidcopilot.app.log.LogcatLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAppLogger(): AppLogger {
        return LogcatLogger
    }

    @Provides
    fun provideAppContext(): Context {
        return ApplicationDependencies.applicationContext
    }

}