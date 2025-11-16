import Foundation
import UIKit

// This file assumes the Kotlin 'shared' framework has been added to the Xcode project.
// It provides small helpers that call into the Kotlin singletons for storage and audio.

@objc public class SharedBridge: NSObject {
    @objc public static func setBasePathToDocuments() {
        let docs = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true).first ?? NSHomeDirectory()
        // PlatformStorage is provided by the Kotlin shared framework once linked
        PlatformStorage.shared.setBasePath(path: docs)
    }

    @objc public static func startRecording() -> Bool {
        return PlatformAudio.shared.startRecording()
    }

    @objc public static func stopRecording() -> RecordingResult? {
        return PlatformAudio.shared.stopRecording()
    }
}
