package com.example.androidcopilot.ui.chat.input

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf


interface ISpeechRecognizer {

    val speech: State<String>
    val state: State<RecognizerState>
    val rms: State<Float>

    fun start()

    fun stop()


}

sealed interface RecognizerState {

    object Started: RecognizerState

    object Stopped: RecognizerState

}

class AndroidSpeechRecognizer(
    private val context: Context
): ISpeechRecognizer {

    private val speechResult = mutableStateOf("")
    private val recognizerState = mutableStateOf<RecognizerState>(
        RecognizerState.Stopped
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
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }
    private val listener = object: RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) {
            log { "on ready speech: $params" }
        }

        override fun onBeginningOfSpeech() {
            log { "on speech begin" }
        }

        override fun onRmsChanged(rmsdB: Float) {
            voiceRms.value = rmsdB
            log { "on rms changed: $rmsdB" }
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            log { "on buffer received: $buffer" }
        }

        override fun onEndOfSpeech() {
            log { "on speech ended" }
        }

        override fun onError(error: Int) {
            log { "on speech error: $error" }
            stop()
        }

        override fun onResults(results: Bundle?) {
            log { "on speech result: ${results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)}" }
            val speeches =
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?: emptyList<String>()
            speechResult.value = speeches.firstOrNull()?:""
            recognizerState.value = RecognizerState.Stopped
        }

        override fun onPartialResults(partialResults: Bundle?) {
            log { "on partial speech result: ${partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)}" }
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
        speechResult.value = ""
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

    companion object {

        const val TAG = "AndroidSpeechRecognizer"

        fun log(error: Throwable? = null, message: () -> String = {""}) {
            Log.d(TAG, message(), error)
        }
    }

}