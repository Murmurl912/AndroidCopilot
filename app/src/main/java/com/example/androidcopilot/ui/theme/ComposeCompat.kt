package com.example.androidcopilot.ui.theme

import android.app.Activity
import android.view.Window
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf


val LocalWindow = staticCompositionLocalOf<Window?> {
    null
}

val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass?> {
    null
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun Activity.CompatComposeContent(content: @Composable () -> Unit) {
    val windowSize = calculateWindowSizeClass(activity = this)
    CompositionLocalProvider(
        LocalWindow provides window,
        LocalWindowSizeClass provides windowSize,
        content = content
    )
}