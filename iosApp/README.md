# iOS integration and local app instructions

This folder contains a minimal SwiftUI iOS app and helper Swift files to consume the shared Kotlin module (when you build and embed it). The app also contains native SwiftUI helpers so you can run the iOS app without the Kotlin framework (it will read/write `tasks.json` in the app Documents folder).

What is included
- `ContentView.swift` — native SwiftUI Task History viewer that reads `tasks.json` from Documents.
- `SharedBridge.swift` — small Swift bridge that calls Kotlin singletons (`PlatformStorage`, `PlatformAudio`) once `shared.framework` is linked.
- `RecordControls.swift` — SwiftUI controls that call `SharedBridge` to start/stop recording (and save resulting task entries).
- `PhotoCaptureView.swift` — camera capture UI that saves photos to Documents and appends tasks.

Build the shared Kotlin framework (macOS required)
1. On macOS, from the repository root build the shared framework(s). The repository includes helper scripts to create an XCFramework:

```bash
chmod +x ./scripts/build_xcframework.sh
./scripts/build_xcframework.sh
```

or use the PowerShell script:

```powershell
.\scripts\build_xcframework.ps1
```

2. The scripts call Gradle to build the `shared` module frameworks for simulator and device and then run `xcodebuild -create-xcframework` to produce `shared.xcframework` under `shared/build/bin` and copy it into `iosApp/Humanness_iOS/Frameworks`.

Add the shared framework to Xcode
1. Open `iosApp/Humanness_iOS.xcodeproj` in Xcode.
2. Drag `shared.xcframework` (or the built `shared.framework`) into your project and add it to the app target.
3. In the app target -> `Frameworks, Libraries, and Embedded Content` set the framework to `Embed & Sign`.

Initialize the shared storage base path
1. In your app startup (for example in `ContentView.init()` or `SceneDelegate`), call:

```swift
import shared

// set Kotlin shared storage to the app Documents directory
SharedBridge.setBasePathToDocuments()
```

This ensures Kotlin `PlatformStorage` will write `tasks.json` into the Documents folder.

Permissions
- Make sure `iosApp/Humanness_iOS/Info.plist` contains microphone and camera usage descriptions (the repo already has `NSMicrophoneUsageDescription` and `NSCameraUsageDescription`).

Run and test
- Build and run the app on a device (recommended) or simulator. The native `ContentView` will show Task History by reading `tasks.json`.
- If `shared.framework` is linked, `RecordControls` will call into Kotlin `PlatformAudio` to perform AVAudioRecorder-based recording and `PhotoCaptureView` will save captured photos — the app will append task entries to `tasks.json` and the Task History will refresh.

Compose-for-iOS embedding (optional / advanced)
- If you want to embed the Compose Multiplatform UI (`SharedApp()`) directly on iOS, follow JetBrains Compose Multiplatform iOS embedding docs. The general approach:
   1. Build and embed the `shared` framework as above.
   2. Expose a Kotlin function that returns a `UIViewController` hosting Compose content (or use the Compose iOS interop entry points).
   3. Present that view controller from SwiftUI using `UIViewControllerRepresentable`.

See official docs for details:
https://github.com/JetBrains/compose-multiplatform/tree/master/tutorials/Compose%20for%20iOS

Troubleshooting
- Building the Kotlin/Native iOS framework requires macOS and Xcode. If the K/N build fails with interop errors, paste the Gradle/Xcode output and I can adjust the Kotlin code.
- Audio recording may behave differently on simulator vs device; prefer running on a physical iPhone for audio and camera tests.

If you want, I can add a small SwiftUI screen that includes `RecordControls` and `PhotoCaptureView` in `ContentView` so testers can easily record and capture photos without extra wiring.

