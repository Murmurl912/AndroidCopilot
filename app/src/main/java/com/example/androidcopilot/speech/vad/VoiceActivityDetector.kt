package com.example.androidcopilot.speech.vad

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.util.concurrent.atomic.AtomicInteger

interface VoiceActivityDetector {

    fun init(sampleRate: Int, sampleCount: Int, channels: Int)

    fun predict(audioFlow: Flow<ShortArray>): Flow<VoiceActivityCategory>

    fun close()

}

data class VoiceActivityCategory(
    val label: String,
    val probability: Float,
)

fun VoiceActivityCategory.isSpeech(): Boolean {
    return this.label == "Speech"
}

fun VoiceActivityCategory.isSilence(): Boolean {
    return this.label == "Silence"
}

fun VoiceActivityCategory.isNoise(): Boolean {
    return this.label == "Noise"
}

class YanmetVoiceActivityDetector(
    private val context: Context,
    private val modelPath: String,
) : VoiceActivityDetector {

    private lateinit var tensor: TensorAudio
    private lateinit var classifier: AudioClassifier

    override fun init(sampleRate: Int, sampleCount: Int, channels: Int) {
        this.classifier = AudioClassifier.createFromFileAndOptions(
            context, modelPath,
            AudioClassifier.AudioClassifierOptions.builder()
                .setMaxResults(1)
                .build()
        )
        this.tensor = TensorAudio.create(
            TensorAudio.TensorAudioFormat.builder()
                .setSampleRate(sampleRate)
                .setChannels(channels)
                .build(),
            sampleCount
        )
    }

    override fun predict(audioFlow: Flow<ShortArray>): Flow<VoiceActivityCategory> {
        return audioFlow.map { array ->
            tensor.load(array)
            val category = classifier.classify(tensor)
                .firstOrNull()
            VoiceActivityCategory(
                label = category?.categories?.firstOrNull()?.label ?: "Noise",
                probability = category?.categories?.firstOrNull()?.score ?: 0f,
            )
        }

    }

    override fun close() {
        classifier.close()
    }

}