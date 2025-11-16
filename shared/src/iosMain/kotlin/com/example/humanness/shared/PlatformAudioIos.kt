package com.example.humanness.shared

import platform.AVFoundation.*
import platform.Foundation.*
import platform.AudioToolbox.kAudioFormatMPEG4AAC
import kotlinx.cinterop.alloc
import kotlinx.cinterop.useContents
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.memScoped
import platform.Foundation.NSError

actual object PlatformAudio {
    private var recorder: AVAudioRecorder? = null
    private var startTs: Long = 0

    private fun defaultDocumentsPath(): String {
        val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        return if (paths.count > 0) paths.objectAtIndex(0) as String else NSHomeDirectory() + "/Documents"
    }

    actual fun startRecording(): Boolean {
        try {
            val base = PlatformStorage.getBasePath() ?: defaultDocumentsPath()
            val filePath = "$base/audio_${System.currentTimeMillis()}.m4a"
            val url = NSURL.fileURLWithPath(filePath)

            val session = AVAudioSession.sharedInstance()
            memScoped {
                val errVar = alloc<ObjCObjectVar<NSError?>>()
                session.setCategory(AVAudioSessionCategoryRecord, error = errVar.ptr.reinterpret())
                session.setActive(true, error = errVar.ptr.reinterpret())
            }

            val settings = mapOf(
                AVFormatIDKey to NSNumber.numberWithInt(kAudioFormatMPEG4AAC.toInt()),
                AVSampleRateKey to NSNumber.numberWithDouble(44100.0),
                AVNumberOfChannelsKey to NSNumber.numberWithInt(1)
            ) as NSDictionary

            val err = alloc<ObjCObjectVar<NSError?>>()
            val rec = AVAudioRecorder(url, settings, err.ptr)
            if (rec == null) return false
            if (!rec.prepareToRecord()) return false
            if (!rec.record()) return false

            recorder = rec
            startTs = System.currentTimeMillis()
            return true
        } catch (t: Throwable) {
            recorder = null
            startTs = 0
            return false
        }
    }

    actual fun stopRecording(): RecordingResult? {
        try {
            val rec = recorder ?: return null
            rec.stop()
            val url = rec.url
            val path = url?.path ?: return null
            val duration = ((System.currentTimeMillis() - startTs) / 1000).toInt()
            recorder = null
            startTs = 0
            return RecordingResult(path = path, durationSec = duration)
        } catch (t: Throwable) {
            recorder = null
            startTs = 0
            return null
        }
    }

    actual fun isRecording(): Boolean = recorder != null
}
