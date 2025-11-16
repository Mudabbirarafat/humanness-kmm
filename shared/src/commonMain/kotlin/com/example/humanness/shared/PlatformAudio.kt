package com.example.humanness.shared

data class RecordingResult(val path: String, val durationSec: Int)

expect object PlatformAudio {
    fun startRecording(): Boolean
    fun stopRecording(): RecordingResult?
    fun isRecording(): Boolean
}
