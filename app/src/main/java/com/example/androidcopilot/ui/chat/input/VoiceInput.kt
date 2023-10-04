package com.example.androidcopilot.ui.chat.input

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale


@Stable
interface VoiceRecognizer {

    val speech: String
    val rms: Float
    val amplitudes: List<Int>
    val state: RecognizerState
    val permissionGranted: Boolean

    fun requestPermission(start: Boolean = false)

    fun start()

    fun stop()

    fun destroy()

    enum class RecognizerState {
        StateStarted,
        StateRecognizing,
        StateStopped,
        StateError,
    }

}


class AndroidSpeechRecognizer(private val context: Context): VoiceRecognizer {

    private val speechState = mutableStateOf("")
    private val voiceRmsState = mutableStateOf(0F)
    private val voiceAmplitudesState = mutableStateOf<List<Int>>(emptyList())
    internal val voiceRecognizerState = mutableStateOf(VoiceRecognizer.RecognizerState.StateStopped)
    private val listener = object: DefaultRecognitionListener() {

        override fun onError(error: Int) {
            super.onError(error)
            voiceRecognizerState.value = VoiceRecognizer.RecognizerState.StateError
        }

        override fun onBeginningOfSpeech() {
            voiceRecognizerState.value = VoiceRecognizer.RecognizerState.StateRecognizing
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val speeches =
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?: emptyList<String>()
            val confidences =
                partialResults?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
                    ?: FloatArray(0)
            if (speeches.isNotEmpty()) {
                speechState.value = speeches[0]
            }
        }

        override fun onResults(results: Bundle?) {
            val speeches =
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?: emptyList<String>()
            val confidences =
                results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
                    ?: FloatArray(0)
            if (speeches.isNotEmpty()) {
                speechState.value = speeches[0]
            }
        }

        override fun onEndOfSpeech() {
            voiceRecognizerState.value = VoiceRecognizer.RecognizerState.StateStopped
        }

        override fun onRmsChanged(rmsdB: Float) {
            voiceRmsState.value = rmsdB
        }
    }
    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
    }
    private var destroyed = false
    private val recognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(listener)
        }
    }
    internal val micPermission = mutableStateOf(false)
    internal var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    internal var startVoiceInputAfterPermissionGranted = false
    override val permissionGranted: Boolean by micPermission
    override val speech: String by speechState
    override val amplitudes: List<Int> by voiceAmplitudesState
    override val rms: Float by voiceRmsState
    override val state: VoiceRecognizer.RecognizerState by voiceRecognizerState

    init {
        checkMicPermission()
    }
    override fun requestPermission(start: Boolean) {
        startVoiceInputAfterPermissionGranted = true
        requestPermissionLauncher?.launch(Manifest.permission.RECORD_AUDIO)
    }

    override fun start() {
        if (destroyed) {
            return
        }
        if (state == VoiceRecognizer.RecognizerState.StateStarted
            || state == VoiceRecognizer.RecognizerState.StateRecognizing) {
            return
        }
        recognizer.startListening(intent)
        voiceRecognizerState.value = VoiceRecognizer.RecognizerState.StateStarted
    }

    override fun stop() {
        if (destroyed) {
            return
        }
        if (state == VoiceRecognizer.RecognizerState.StateStarted
            || state == VoiceRecognizer.RecognizerState.StateRecognizing) {
            recognizer.stopListening()
        }
    }

    override fun destroy() {
        destroyed = true
        recognizer.destroy()
    }

    internal fun checkMicPermission() {
        micPermission.value = context.checkSelfPermission(
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!micPermission.value) {
            stop()
        }
    }

    private open class DefaultRecognitionListener: RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {

        }

        override fun onBeginningOfSpeech() {

        }

        override fun onRmsChanged(rmsdB: Float) {

        }

        override fun onBufferReceived(buffer: ByteArray?) {

        }

        override fun onEndOfSpeech() {

        }

        override fun onError(error: Int) {

        }

        override fun onResults(results: Bundle?) {

        }

        override fun onPartialResults(partialResults: Bundle?) {

        }

        override fun onEvent(eventType: Int, params: Bundle?) {

        }
    }

}


@Composable
fun rememberAndroidVoiceRecognizer(
    onSpeechChanged: (String) -> Unit = {},
    onRecognizerStateChange: (VoiceRecognizer.RecognizerState) -> Unit = {},
): VoiceRecognizer {
    val context = LocalContext.current.applicationContext
    val voiceRecognizer = remember {
        AndroidSpeechRecognizer(context)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            voiceRecognizer.micPermission.value = it
            if (voiceRecognizer.startVoiceInputAfterPermissionGranted) {
                voiceRecognizer.startVoiceInputAfterPermissionGranted = false
                voiceRecognizer.start()
            }
        }
    )

    voiceRecognizer.requestPermissionLauncher = launcher
    SideEffect {
        voiceRecognizer.checkMicPermission()
    }

    LaunchedEffect(voiceRecognizer.speech) {
        onSpeechChanged(voiceRecognizer.speech)
    }
    LaunchedEffect(voiceRecognizer.state) {
        onRecognizerStateChange(voiceRecognizer.state)
    }
    DisposableEffect(Unit) {
        onDispose {
            voiceRecognizer.stop()
        }
    }
    return voiceRecognizer
}

@Composable
fun VoiceInput(
    modifier: Modifier = Modifier,
    recognizer: VoiceRecognizer = rememberAndroidVoiceRecognizer(),
    onPermissionDenied: (@Composable () -> Unit)? = null,
    onStop: () -> Unit = {
        recognizer.stop()
    },
    onStart: () -> Unit = {
        recognizer.start()
    },
    onSwitchInput: (InputMode) -> Unit = {}
) {
    if (recognizer.permissionGranted) {
        Column(modifier) {
            Spacer(modifier = Modifier.weight(1F))
            IconButton(onClick =
                if (recognizer.state == VoiceRecognizer.RecognizerState.StateStopped
                    || recognizer.state == VoiceRecognizer.RecognizerState.StateError) {
                    onStart
                } else {
                    onStart
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(56.dp)
                    .clip(CircleShape)
                ,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(Icons.Default.KeyboardVoice, "")
            }
            Text(text = recognizer.speech)
            Spacer(modifier = Modifier.weight(1F))
            IconButton(onClick = {
                onSwitchInput(InputMode.TextInput)
            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Icon(Icons.Default.Keyboard, contentDescription = "")
            }
        }

    } else {
        onPermissionDenied?.invoke()
    }
}