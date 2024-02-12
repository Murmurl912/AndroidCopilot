package com.example.androidcopilot.speech

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import com.example.androidcopilot.BuildConfig
import com.example.androidcopilot.app.LogcatLogger
import com.example.androidcopilot.speech.vad.YanmetVoiceActivityDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OpenaiSpeechRecognizer(
    private val context: Context,
    private val scope: CoroutineScope,
) : ISpeechRecognizer {

    val tag = "OpenaiSpeechRecognizer"
    private val speechResult = MutableStateFlow("")
    private val recognizerState = MutableStateFlow<RecognizerState>(
        RecognizerState.Stopped
    )
    override val speech: StateFlow<String> = speechResult
    override val state: StateFlow<RecognizerState> = recognizerState

    private val sampleRate: Int = 16000
    private val sampleCount: Int = 15600
    private val channels: Int = 1
    private var recorder: AudioRecord? = null
    private var vad: YanmetVoiceActivityDetector? = null

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    private fun createRecorder(): AudioRecord {
        val minBufferSize = maxOf(
            AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ),
            2 * sampleCount
        )
        return AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufferSize
        )
    }

    private fun createVad(): YanmetVoiceActivityDetector {
        val modelAsset = "yamnet.tflite"
        val modelFile = context.filesDir.resolve(modelAsset)
        context.assets.open(modelAsset)
            .copyTo(modelFile.outputStream())
        val vad = YanmetVoiceActivityDetector(context, modelAsset)
        vad.init(sampleRate, sampleCount, channels)
        return vad
    }

    private fun record(recorder: AudioRecord) = flow {
        this@OpenaiSpeechRecognizer.recorder = recorder
        recorder.startRecording()
        val buffer = ShortArray(sampleCount / 2)
        try {
            while (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    emit(buffer)
                }
                if (read < 0) {
                    break
                }
            }
        } catch (e: Exception) {
            releaseRecorder()
        }
    }.flowOn(Dispatchers.IO)

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    override fun start() {
        recognizerState.value = RecognizerState.Started
        scope.launch {
            detect()
        }.invokeOnCompletion {
            recognizerState.value = RecognizerState.Stopped
            LogcatLogger.info(tag) { "recognizer stopped" }
        }
    }

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    suspend fun detect() {
        val recorder = createRecorder()
        val vad = createVad()
        val recordingFlow = record(recorder)
        val openai = OpenAI(
            token = BuildConfig.OPENAI_TOKEN,
            host = OpenAIHost(
                BuildConfig.OPENAI_API
            )
        )
        vad.predict(recordingFlow)
            .onEach { category ->
                LogcatLogger.debug(tag) { "voice category: $category" }
            }
            .collect()
    }


    override fun stop() {
        releaseRecorder()
    }

    private fun releaseRecorder() {
        try {
            recorder?.stop()
            recorder?.release()
            recorder = null
        } catch (e: Exception) {
            LogcatLogger.warn(tag, e) { "stop recorder error: ${e.message}" }
        }
    }
}