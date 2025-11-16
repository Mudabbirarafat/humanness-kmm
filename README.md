
Humanness — KMM + Compose Multiplatform prototype (Android + iOS)

This repository contains a prototype application that demonstrates a sample-task flow across Android and iOS using Kotlin Multiplatform (KMM) and Compose Multiplatform (shared UI for Android). The prototype implements recording tasks (text reading, image description, photo capture) and a local Task History.

Contents
- `shared/` — Kotlin Multiplatform module. Contains Compose UI screens, models (`TaskModels.kt`), `TaskRepository` (simple persistence), and platform expect/actual implementations for storage and audio.
- `androidApp/` — Android app module wired to call into shared Compose UI. Android `MainActivity` initializes platform storage and displays the shared UI.
- `iosApp/Humanness_iOS/` — Xcode SwiftUI app. Includes a native SwiftUI Task History view, Swift glue (`SharedBridge.swift`), recording & photo capture UI, and scripts to produce and install the shared Kotlin XCFramework.
- `scripts/` — Build scripts to create an XCFramework (`build_xcframework.sh`, `build_xcframework.ps1`).

Feature summary
- Start screen, Noise Test (simulated decibel meter), Task Selection.
- Text reading task: press-and-hold to record audio (10–20 s validation), checkboxes and submit.
- Image description task: press-and-hold to record audio and submit.
- Photo capture task: capture photo, optionally record a short description, and submit.
- Task History: shows saved tasks (task type, duration, timestamp, preview).

Platform status
- Android: Shared UI integrated; audio recording implemented using `MediaRecorder` (real audio files saved to app `filesDir`); persistence writes `tasks.json` to `filesDir`. Camera capture is currently a placeholder path (but Android `Manifest` includes camera permission). Runtime permission requests are not yet implemented on Android and should be added for API 23+.
- iOS: Shared Kotlin code includes `PlatformStorage` (file-backed implementation) and `PlatformAudio` implemented with AVAudioRecorder via Kotlin/Native interop. To run the iOS app with full shared functionality you must build and link the `shared` Kotlin framework into Xcode (instructions below). A native SwiftUI Task History and helper UI are included so you can run the iOS app using the local `tasks.json` without linking the Kotlin framework.

Build and run (Android)
1. Open the project in Android Studio.
2. Let Gradle sync and download dependencies.
3. To build the debug APK via command line from project root:
```powershell
.\gradlew.bat :androidApp:assembleDebug
```
4. To install on a connected device:
```powershell
.\gradlew.bat :androidApp:installDebug
```

Build and run (iOS) — produce XCFramework (macOS required)
1. On macOS, from the repository root run the helper script to build frameworks and create an XCFramework:
```bash
chmod +x ./scripts/build_xcframework.sh
./scripts/build_xcframework.sh
```
Or run the PowerShell script if preferred:
```powershell
.\scripts\build_xcframework.ps1
```
2. Open `iosApp/Humanness_iOS` in Xcode.
3. In the app target -> `Frameworks, Libraries, and Embedded Content` add the generated `shared.xcframework` (or `shared.framework`) and set it to `Embed & Sign`.
4. In Swift code (e.g., `App` or `ContentView.init`) call `SharedBridge.setBasePathToDocuments()` (or `PlatformStorage.shared.setBasePath(...)`) to initialize the shared storage base path to the Documents directory.
5. Ensure `Info.plist` contains microphone and camera usage descriptions (already present in this repo).
6. Run on a device (recommended for audio/camera). The example `ContentView` contains a native Task History viewer. After embedding the framework you can use `RecordControls` (calls into `PlatformAudio`) and `PhotoCaptureView`.

Notes on permissions and testing
- Android: You must request `RECORD_AUDIO` and `CAMERA` runtime permissions on Android 6.0+ before recording or taking photos. The `AndroidManifest.xml` already declares these permissions.
- iOS: `NSMicrophoneUsageDescription` and `NSCameraUsageDescription` are included in `iosApp/Humanness_iOS/Info.plist`.
- Test audio/camera flows on a real device for reliable results.

Project scripts
- `scripts/build_xcframework.sh` — builds shared frameworks and creates `shared.xcframework` then copies it to `iosApp/Humanness_iOS/Frameworks`.
- `scripts/build_xcframework.ps1` — PowerShell version of the same script.

Limitations and next steps
- Android runtime permission handling and complete camera implementation (preview, save) should be added.
- iOS Compose-for-iOS embedding (rendering `SharedApp()` Compose UI inside a UIViewController) is not included; the repo provides a native SwiftUI wrapper and call-bridges to use shared Kotlin APIs (`PlatformAudio`, `PlatformStorage`). If you want full Compose-for-iOS rendering, I can add the embedding steps and example host UIViewController.
- Tests and CI are not included; consider adding Gradle tasks and unit/integration tests.

How I tested this prototype
- I implemented and wired shared UI and platform code inside the repo. Android audio code and iOS AVAudioRecorder code require building on their respective platforms (Android Studio / macOS Xcode). The SwiftUI Task History and FileStorage can run on iOS without linking the shared framework (reads `tasks.json` directly from Documents).

If you want me to continue
- I can finish Android runtime permission flow and implement camera capture + preview, then build the Android APK and upload it.
- I can prepare an example UIViewController and steps for embedding `SharedApp()` Compose UI on iOS (Compose-for-iOS) and produce the exact Xcode changes needed.
- I can help create a GitHub repo and upload the generated APK and instructions.

Contact
- Tell me which of the next steps you'd like me to implement and I will continue. 

