import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext


class StreamAudioRecorder {

    private var audioRecord: AudioRecord? = null

    val isRecording: Boolean
        get() = audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    fun <T> start(
        audioSource: Int = MediaRecorder.AudioSource.MIC,
        sampleRate: Int = 44100,
        channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
        audioFormat: Int = AudioFormat.ENCODING_PCM_16BIT
    ): Flow<ByteArray> {
        if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            throw IllegalStateException("Already recording");
        }
        return record(audioSource, sampleRate, channelConfig, audioFormat)
    }

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    private fun record(
        audioSource: Int,
        sampleRate: Int,
        channelConfig: Int,
        audioFormat: Int
    ): Flow<ByteArray> {
        val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        val audioRecord = AudioRecord(
            audioSource,
            sampleRate,
            channelConfig,
            audioFormat,
            minBufSize
        )
        this.audioRecord = audioRecord;
        return flow {
            withContext(Dispatchers.IO) {
                try {
                    val buffer = ByteArray(minBufSize)
                    audioRecord.startRecording()
                    while (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING && isActive) {
                        val read = audioRecord.read(buffer, 0, buffer.size)
                        if (read > 0) {
                            emit(buffer.copyOf(read))
                        } else if (read < 0) {
                            break;
                        }
                    }
                } finally {
                    audioRecord.stop()
                    audioRecord.release()
                    this@StreamAudioRecorder.audioRecord = null;
                }
            }
        }
    }

    fun stop() {
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

}