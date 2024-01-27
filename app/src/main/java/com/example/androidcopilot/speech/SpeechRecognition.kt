package com.example.androidcopilot.speech

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.MediaRecorder.AudioSource
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.audio.classifier.Classifications
import org.tensorflow.lite.task.core.BaseOptions
import kotlin.concurrent.thread

interface ISpeechRecognizer {

    val speech: StateFlow<String>
    val state: StateFlow<RecognizerState>
    val rms: StateFlow<Float>

    @RequiresPermission("android.permission.RECORD_AUDIO")
    fun start()

    fun stop()


}

sealed interface RecognizerState {

    object Started : RecognizerState

    object PermissionDenied : RecognizerState

    object Stopped : RecognizerState

}