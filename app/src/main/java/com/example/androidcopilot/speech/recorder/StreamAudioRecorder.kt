import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.nio.ByteBuffer


class StreamAudioRecorder {

    private var audioRecord: AudioRecord? = null

    val isRecording: Boolean
        get() = audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    fun start(
        audioSource: Int = MediaRecorder.AudioSource.MIC,
        sampleRate: Int = 44100,
        channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
        audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT,
        bufferSize: Int = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    ): Flow<ByteArray> {
        if (isRecording) {
            throw IllegalStateException("Already recording");
        }

        val audioRecord =
            createRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize)
        this.audioRecord = audioRecord
        return record(audioRecord, bufferSize)
    }

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    private fun createRecord(
        audioSource: Int,
        sampleRate: Int,
        channelConfig: Int,
        audioFormat: Int,
        bufferSize: Int
    ): AudioRecord {
        return AudioRecord(
            audioSource,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )
    }

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    private fun record(
        audioRecord: AudioRecord,
        bufferSize: Int
    ): Flow<ByteArray> {
        return flow {
            val buffer = ByteBuffer.allocateDirect(bufferSize)
            try {
                audioRecord.startRecording()
                while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    val read = audioRecord.read(buffer, buffer.capacity())
                    if (read > 0) {
                        buffer.flip()
                        val data = ByteArray(buffer.remaining())
                        buffer.get(data)
                        emit(data)
                    } else if (read < 0) {
                        break;
                    }
                }
            } finally {
                audioRecord.stop()
                audioRecord.release()
            }
        }.flowOn(Dispatchers.IO)
    }

    fun stop() {
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

}