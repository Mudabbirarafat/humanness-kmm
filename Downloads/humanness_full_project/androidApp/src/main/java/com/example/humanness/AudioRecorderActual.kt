package shared

import com.example.humanness.audio.AudioRecorderImpl as AndroidAudioRecorderImpl
import android.content.Context

actual class AudioRecorder(private val context: Context) {
    private val impl = AndroidAudioRecorderImpl(context)

    actual fun startRecording(outputPath: String): Boolean = impl.startRecording(outputPath)
    actual fun stopRecording(): Boolean = impl.stopRecording()
    actual fun isRecording(): Boolean = impl.isRecording()
    actual suspend fun getRecordingDurationMs(): Long = impl.getRecordingDurationMs().toLong()
}
