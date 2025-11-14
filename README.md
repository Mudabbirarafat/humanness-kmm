# Humanness - Kotlin Multiplatform + Compose Multiplatform Prototype

A comprehensive Android application built with Kotlin Multiplatform (KMM) and Compose Multiplatform (CMP) that enables users to perform sample recording tasks including text reading, image description, and photo capture with task history tracking.

## Features

### 1. Start Screen
- Entry point with welcoming message in English and Hindi
- Button to navigate to noise test

### 2. Noise Test Screen
- Ambient noise level detection (0-60 dB scale)
- Real-time decibel level display
- Automatic pass/fail decision based on ambient noise
- Instructions for proceeding or finding a quieter environment

### 3. Task Selection Screen
Three distinct task types:
- Text Reading Task
- Image Description Task
- Photo Capture Task

### 4. Text Reading Task
- Fetches product descriptions from DummyJSON API
- Press-and-hold microphone button for recording
- Automatic validation of recording duration (10-20 seconds)
- Quality checkboxes validation
- Inline error messages for invalid recordings
- Task saved to local database

### 5. Image Description Task
- Displays product images from API
- Voice recording with duration validation
- Description through audio narration
- Task persisted locally

### 6. Photo Capture Task
- Camera capture using device camera
- Text field for manual description
- Optional audio description with mic button
- Photo and metadata saved locally

### 7. Task History Screen
- Displays all completed tasks in reverse chronological order
- Shows total task count and total recording duration
- Task previews (text snippets, image thumbnails)
- Delete functionality for individual tasks

## Architecture

```
├── shared/
│   └── src/commonMain/kotlin/
│       ├── TaskModels.kt           # Shared data classes
│       ├── AudioRecorder.kt        # Expect declaration
│       ├── NoiseDetector.kt        # Expect declaration
│       ├── RecordingValidator.kt   # Validation logic
│       └── ProductApiClient.kt     # API integration
│
└── androidApp/
    ├── src/main/java/com/example/humanness/
    │   ├── MainActivity.kt          # Navigation setup
    │   ├── AudioRecorderImpl.kt      # Android implementation
    │   ├── AudioPlayerImpl.kt        # Audio playback
    │   ├── NoiseDetectorImpl.kt      # Noise detection
    │   ├── db/
    │   │   ├── TaskDatabase.kt      # Room database setup
    │   │   ├── TaskEntity.kt        # Database entity
    │   │   ├── TaskDao.kt           # Database access
    │   │   └── TaskRepository.kt    # Data layer
    │   ├── ui/
    │   │   ├── navigation/
    │   │   │   └── Screen.kt        # Navigation routes
    │   │   ├── theme/
    │   │   │   └── Theme.kt         # Material Design theme
    │   │   └── screens/
    │   │       ├── StartScreen.kt
    │   │       ├── NoiseTestScreen.kt
    │   │       ├── TaskSelectionScreen.kt
    │   │       ├── TextReadingTaskScreen.kt
    │   │       ├── ImageDescriptionTaskScreen.kt
    │   │       ├── PhotoCaptureTaskScreen.kt
    │   │       └── TaskHistoryScreen.kt
    └── build.gradle.kts
```

## Tech Stack

- **Kotlin Multiplatform**: Shared business logic across platforms
- **Compose Multiplatform**: Modern declarative UI framework
- **Room Database**: Local data persistence
- **Ktor Client**: HTTP requests to DummyJSON API
- **MediaRecorder**: Audio recording from microphone
- **Accompanist Permissions**: Runtime permission handling
- **Coil**: Image loading and caching

## Dependencies

### Gradle
- Kotlin: 1.9.10
- Compose: 1.6.0
- Material3: 1.1.1
- Android API: 34 (compileSdk), 26 (minSdk)

### Libraries
- Ktor Client: 2.3.0
- Room: 2.5.2
- Accompanist Permissions: 0.32.0
- Coil: 2.4.0

## Building & Running

### Prerequisites
- Android SDK API 34
- Android Build Tools
- Gradle 8.0+
- JDK 11+

### Build APK

```bash
# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease
```

### Run on Device/Emulator

```bash
./gradlew installDebug
```

## Project Structure

The project follows Kotlin Multiplatform best practices:
- **Common code** in `shared/` module for business logic
- **Platform-specific** implementations in `androidApp/` module
- **Expect/Actual pattern** for platform-specific classes
- **Repository pattern** for data access abstraction
- **Composable functions** for UI following MVVM guidelines

## Data Models

### TaskRecord
```kotlin
data class TaskRecord(
    val id: String,
    val task_type: String,        // "text_reading", "image_description", "photo_capture"
    val text: String? = null,     // Product description for text reading
    val image_url: String? = null,
    val image_path: String? = null,
    val audio_path: String? = null,
    val duration_sec: Int,
    val timestamp: String,
    val checkpoints: List<String>? = null
)
```

## Recording Validation

- **Minimum duration**: 10 seconds
- **Maximum duration**: 20 seconds
- **Validation**: Automatic rejection with inline error messages
- **Retry**: Option to re-record if validation fails

## Noise Detection

- **Range**: 0-60 dB (decibel scale)
- **Good threshold**: < 40 dB
- **Needs quiet**: >= 40 dB
- **Duration**: 3-second real-time monitoring

## File Storage

- **Audio files**: `/data/data/com.example.humanness/files/recordings/`
- **Photos**: `/data/data/com.example.humanness/files/photos/`
- **Database**: Room SQLite at app cache directory

## Permissions Required

```xml
<uses-permission android:name="android.permission.RECORD_AUDIO"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.INTERNET"/>
```

## API Integration

- **Endpoint**: https://dummyjson.com/products
- **Usage**: Fetches product data for text reading and image description tasks
- **Fallback**: Default sample data if API unavailable

## Navigation Flow

```
Start Screen
    ↓
Noise Test Screen
    ↓
Task Selection Screen
    ├→ Text Reading Task → Task Submission
    ├→ Image Description Task → Task Submission
    └→ Photo Capture Task → Task Submission
    ↓
Back to Task Selection (Loop)
```

## Building for Release

```bash
# Build release APK
./gradlew assembleRelease

# Build release Bundle (for Play Store)
./gradlew bundleRelease
```

The APK will be generated at: `androidApp/build/outputs/apk/release/`

## Future Enhancements

- [ ] Desktop/iOS support using Compose Multiplatform
- [ ] Cloud sync for task history
- [ ] Voice transcription integration
- [ ] Advanced audio analytics
- [ ] Task statistics and analytics dashboard
- [ ] Offline mode with sync
- [ ] Multi-language support

## Contributing

1. Create a new branch for features
2. Follow Kotlin style guidelines
3. Ensure all screens are tested
4. Submit pull requests with detailed descriptions

## License

This project is provided as-is for educational and demonstration purposes.

## Support

For issues or questions, please refer to the GitHub repository issues section.

---

**Build Date**: November 2025  
**Kotlin Version**: 1.9.10  
**Compose Version**: 1.6.0  
**Target API**: 34  
**Minimum API**: 26
