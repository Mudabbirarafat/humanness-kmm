package com.example.humanness.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File

class AudioRecorderImpl(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var recordingFile: File? = null
    private var startTime: Long = 0
    private val recordingsDir = File(context.filesDir, "recordings").apply { mkdirs() }

    fun startRecording(outputPath: String = ""): Boolean {
        return try {
            val fileName = outputPath.ifEmpty { "recording_${System.currentTimeMillis()}.mp3" }
            recordingFile = File(recordingsDir, fileName)
            recordingFile?.delete()

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(128000)
                setOutputFile(recordingFile?.absolutePath)
                prepare()
                start()
            }
            startTime = System.currentTimeMillis()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            mediaRecorder?.release()
            mediaRecorder = null
            false
        }
    }

    fun stopRecording(): Boolean {
        return try {
            mediaRecorder?.apply {
                try {
                    stop()
                } catch (e: Exception) {
                    // Recording already stopped or never started
                }
                release()
            }
            mediaRecorder = null
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isRecording(): Boolean = mediaRecorder != null && startTime > 0

    fun getRecordingDurationMs(): Long {
        return if (isRecording()) {
            System.currentTimeMillis() - startTime
        } else {
            0L
        }
    }

    fun getRecordingFile(): File? = recordingFile
}
