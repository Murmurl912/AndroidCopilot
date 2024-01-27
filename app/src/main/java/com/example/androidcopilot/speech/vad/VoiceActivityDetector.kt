package com.example.androidcopilot.speech.vad

import android.content.Context
import org.tensorflow.lite.support.audio.TensorAudio
import org.tensorflow.lite.task.audio.classifier.AudioClassifier

interface VoiceActivityDetector {

    val sampleRate: Int

    val frameSize: Int

    fun predict(data: FloatArray): VoiceActivityCategory

    fun close()

}

data class VoiceActivityCategory(
    val label: String,
    val probability: Float
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
    context: Context,
    private val modelPath: String,
    override val sampleRate: Int = 16000, // 16000 Hz fixed cannot change
    override val frameSize: Int = 15600 // 0.975 sec fixed cannot change
) : VoiceActivityDetector {

    private val tensor: TensorAudio
    private val classifier: AudioClassifier

    init {
        this.classifier = AudioClassifier.createFromFileAndOptions(
            context, modelPath,
            AudioClassifier.AudioClassifierOptions.builder()
                .setMaxResults(1)
                .build()
        )
        this.tensor = TensorAudio.create(
            TensorAudio.TensorAudioFormat.builder()
                .setSampleRate(sampleRate)
                .build(),
            frameSize
        )
    }

    override fun predict(data: FloatArray): VoiceActivityCategory {
        tensor.load(data)
        val result = classifier.classify(tensor)
        return result.map {
            VoiceActivityCategory(it.categories[0].label, it.categories[0].score)
        }.first()
    }

    override fun close() {
        classifier.close()
    }

}