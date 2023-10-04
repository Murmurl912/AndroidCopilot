package com.example.androidcopilot.ui.chat.input

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.util.Locale


interface ISpeechRecognizer {

    val speech: State<String>
    val state: State<RecognizerState>
    val rms: State<Float>

    fun start()

    fun stop()


}

sealed interface RecognizerState {

    object Started: RecognizerState

    object SpeechStarted: RecognizerState

    object SpeechEnded: RecognizerState

    object Stopped: RecognizerState

}

class AndroidSpeechRecognizer(
    private val context: Context
): ISpeechRecognizer {

    private val speechResult = mutableStateOf("")
    private val recognizerState = mutableStateOf<RecognizerState>(
        RecognizerState.SpeechStarted
    )
    private val voiceRms = mutableStateOf(0F)
    override val speech: State<String> = speechResult
    override val state: State<RecognizerState> = recognizerState
    override val rms: State<Float> = voiceRms

    private var speechRecognizer: SpeechRecognizer? = null
        get() {
            return if (field != null) {
                field
            } else {
                field = createRecognizer()
                field
            }
        }

    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
    }
    private val listener = object: RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) {

        }

        override fun onBeginningOfSpeech() {
            recognizerState.value = RecognizerState.SpeechStarted
        }

        override fun onRmsChanged(rmsdB: Float) {
            voiceRms.value = rmsdB
        }

        override fun onBufferReceived(buffer: ByteArray?) {

        }

        override fun onEndOfSpeech() {
            recognizerState.value = RecognizerState.SpeechEnded
        }

        override fun onError(error: Int) {
            recognizerState.value = RecognizerState.Stopped
            speechRecognizer?.stopListening()
        }

        override fun onResults(results: Bundle?) {
            val speeches =
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?: emptyList<String>()
            speechResult.value = speeches.firstOrNull()?:""
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val speeches =
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?: emptyList<String>()
            speechResult.value = speeches.firstOrNull()?:""
        }

        override fun onEvent(eventType: Int, params: Bundle?) {

        }
    }

    private fun createRecognizer(): SpeechRecognizer {
        return SpeechRecognizer.createSpeechRecognizer(context)
            .apply {
                setRecognitionListener(listener)
            }
    }

    override fun start() {
        if (recognizerState.value != RecognizerState.Stopped) {
            return
        }
        recognizerState.value = RecognizerState.Started
        speechRecognizer?.startListening(intent)
    }

    override fun stop() {
        if (recognizerState.value == RecognizerState.Stopped) {
            return
        }
        speechRecognizer?.let {
            it.stopListening()
            it.setRecognitionListener(null)
            it.destroy()
            recognizerState.value = RecognizerState.Stopped
        }
        speechRecognizer = null
    }


}