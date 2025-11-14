package com.example.humanness.audio

import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.delay
import kotlin.math.log10

class NoiseDetectorImpl {
    private var mediaRecorder: MediaRecorder? = null
    private val maxAmplitude: Float
        get() = (mediaRecorder?.maxAmplitude?.toFloat() ?: 0f) / 32768f

    suspend fun startTest(): NoiseLevel {
        return try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(null)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile("/dev/null")
                prepare()
                start()
            }

            // Record for 3 seconds and calculate average
            var totalDb = 0f
            var readings = 0

            repeat(30) {
                val db = getCurrentDecibelLevel()
                totalDb += db
                readings++
                delay(100)
            }

            stopTest()

            val avgDb = totalDb / readings
            if (avgDb < 40) NoiseLevel.GOOD else NoiseLevel.NEEDS_QUIET
        } catch (e: Exception) {
            stopTest()
            NoiseLevel.GOOD // Default to allow proceeding
        }
    }

    suspend fun getCurrentDecibelLevel(): Int {
        return try {
            val amplitude = maxAmplitude
            val db = if (amplitude > 0) {
                (20 * log10(amplitude.toDouble())).toInt()
            } else {
                0
            }
            (db + 90).coerceIn(0, 60) // Map to 0-60 dB range
        } catch (e: Exception) {
            0
        }
    }

    fun stopTest() {
        try {
            mediaRecorder?.apply {
                try {
                    stop()
                } catch (e: Exception) {
                    // Already stopped
                }
                release()
            }
        } catch (e: Exception) {
            // Ignore
        }
        mediaRecorder = null
    }
}
