package com.example.humanness.audio

import android.media.AudioManager
import android.media.MediaPlayer
import java.io.File

class AudioPlayerImpl {
    private var mediaPlayer: MediaPlayer? = null

    fun playAudio(audioPath: String): Boolean {
        return try {
            if (!File(audioPath).exists()) {
                return false
            }

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(audioPath)
                prepare()
                start()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun stopPlayback(): Boolean {
        return try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying ?: false
        } catch (e: Exception) {
            false
        }
    }
}
