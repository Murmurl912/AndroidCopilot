package com.example.androidcopilot.speech

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.audio.classifier.Classifications
import org.tensorflow.lite.task.core.BaseOptions


interface ISpeechRecognizer {

    val speech: StateFlow<String>
    val state: StateFlow<RecognizerState>
    val rms: StateFlow<Float>

    @RequiresPermission("android.permission.RECORD_AUDIO")
    fun start()

    fun stop()


}

sealed interface RecognizerState {

    object Started: RecognizerState

    object PermissionDenied: RecognizerState

    object Stopped: RecognizerState

}


class LocalSpeechRecognizer(private val context: Context): ISpeechRecognizer {

    private val modelFile: String = ""
    private val speechResult = MutableStateFlow("")
    private val recognizerState = MutableStateFlow<RecognizerState>(
        RecognizerState.Stopped
    )
    private val voiceRms = MutableStateFlow(0F)
    override val speech: StateFlow<String> = speechResult
    override val state: StateFlow<RecognizerState> = recognizerState
    override val rms: StateFlow<Float> = voiceRms
    private lateinit var audioRecord: AudioRecord

    @SuppressLint("MissingPermission")
    private fun initRecorder() {
        //Define AudioRecord Object and other parameters
        val RECORDER_SAMPLE_RATE = 44100
        val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
        val RAW_AUDIO_SOURCE = MediaRecorder.AudioSource.UNPROCESSED
        val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO
        val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        val BUFFER_SIZE_RECORDING = AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        audioRecord = AudioRecord(AUDIO_SOURCE, RECORDER_SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE_RECORDING)
    }


    private fun ensureAudioRecordCreated() {
        if (!this::audioRecord.isInitialized) {
            initRecorder()
        }
    }

    override fun start() {
        ensureAudioRecordCreated()
        if (audioRecord.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.startRecording()
        }
        // Initialization
        // Initialization
        val options: AudioClassifier.AudioClassifierOptions = AudioClassifier.AudioClassifierOptions.builder()
            .setBaseOptions(BaseOptions.builder().useGpu().build())
            .setMaxResults(1)
            .build()
        val classifier: AudioClassifier =
            AudioClassifier.createFromFileAndOptions(context, modelFile, options)
        val record: AudioRecord = classifier.createAudioRecord()
        record.startRecording()
        val audioTensor: TensorAudio = classifier.createInputTensorAudio()
        audioTensor.load(record)
        val results: List<Classifications> = classifier.classify(audioTensor)
    }


    override fun stop() {
        TODO("Not yet implemented")
    }
}

class StreamAudioRecorder {

    val audioRecord: AudioRecord = TODO()

    fun start() {
        audioRecord.startRecording()

    }

}