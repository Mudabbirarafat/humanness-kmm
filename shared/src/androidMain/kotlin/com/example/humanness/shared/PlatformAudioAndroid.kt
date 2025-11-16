package com.example.humanness.shared

import android.media.MediaRecorder
import java.io.File

actual object PlatformAudio {
    private var recorder: MediaRecorder? = null
    private var startTs: Long = 0
    private var outPath: String? = null

    actual fun startRecording(): Boolean {
        try {
            val base = PlatformStorage.getBasePath() ?: return false
            val dir = File(base)
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "audio_${System.currentTimeMillis()}.mp4")
            outPath = file.absolutePath

            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outPath)
                prepare()
                start()
            }
            startTs = System.currentTimeMillis()
            return true
        } catch (e: Exception) {
            recorder = null
            outPath = null
            return false
        }
    }

    actual fun stopRecording(): RecordingResult? {
        try {
            val rec = recorder ?: return null
            rec.stop()
            rec.release()
            recorder = null
            val duration = ((System.currentTimeMillis() - startTs) / 1000).toInt()
            val path = outPath ?: return null
            outPath = null
            return RecordingResult(path = path, durationSec = duration)
        } catch (e: Exception) {
            recorder = null
            outPath = null
            return null
        }
    }

    actual fun isRecording(): Boolean = recorder != null
}
