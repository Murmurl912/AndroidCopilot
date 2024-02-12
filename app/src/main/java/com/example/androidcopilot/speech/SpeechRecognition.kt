package com.example.androidcopilot.speech

import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.StateFlow

interface ISpeechRecognizer {

    val speech: StateFlow<String>
    val state: StateFlow<RecognizerState>

    @RequiresPermission("android.permission.RECORD_AUDIO")
    fun start()

    fun stop()


}

sealed interface RecognizerState {

    object Started : RecognizerState

    object PermissionDenied : RecognizerState

    object Stopped : RecognizerState

}