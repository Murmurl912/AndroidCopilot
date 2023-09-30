package com.example.androidcopilot.app.startup

import android.content.Context
import androidx.startup.Initializer
import com.example.androidcopilot.app.ApplicationDependencies

class ApplicationDependenciesStartup: Initializer<ApplicationDependencies> {

    override fun create(context: Context): ApplicationDependencies {
        ApplicationDependencies.applicationContext = context
        return ApplicationDependencies
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}