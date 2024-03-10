package com.example.androidcopilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.androidcopilot.ui.main.AndroidCopilotMain
import com.example.androidcopilot.ui.theme.CompatComposeContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CompatComposeContent {
                AndroidCopilotMain()
            }
        }
    }
}
