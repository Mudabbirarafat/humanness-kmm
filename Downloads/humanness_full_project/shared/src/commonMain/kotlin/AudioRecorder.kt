package shared

expect class AudioRecorder {
    fun startRecording(outputPath: String): Boolean
    fun stopRecording(): Boolean
    fun isRecording(): Boolean
    suspend fun getRecordingDurationMs(): Long
}

expect class AudioPlayer {
    fun playAudio(audioPath: String): Boolean
    fun stopPlayback(): Boolean
    fun isPlaying(): Boolean
}
