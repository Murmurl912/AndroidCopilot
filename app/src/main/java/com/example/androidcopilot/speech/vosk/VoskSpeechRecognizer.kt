package com.example.androidcopilot.speech.vosk

import com.example.androidcopilot.speech.ISpeechRecognizer
import com.example.androidcopilot.speech.RecognizerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.vosk.android.SpeechService
import org.vosk.android.SpeechStreamService

class VoskSpeechRecognizer: ISpeechRecognizer {

    private val speechResult = MutableStateFlow("")
    private val recognizerState = MutableStateFlow<RecognizerState>(
        RecognizerState.Stopped
    )
    private val voiceRms = MutableStateFlow(0F)
    override val speech: StateFlow<String> = speechResult
    override val state: StateFlow<RecognizerState> = recognizerState
    override val rms: StateFlow<Float> = voiceRms

    private val speechStreamService: SpeechStreamService? = null

    override fun start() {
    }

    override fun stop() {

    }


}