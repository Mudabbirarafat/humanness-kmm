package com.example.humanness.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.humanness.audio.AudioRecorderImpl
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import shared.RecordingValidator
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoCaptureTaskScreen(
    context: Context,
    onTaskSubmitted: (imagePath: String, description: String, audioPath: String?, duration: Int) -> Unit,
    onBack: () -> Unit
) {
    val audioRecorder = remember { AudioRecorderImpl(context) }
    val capturedPhotoUri = remember { mutableStateOf<Uri?>(null) }
    val capturedPhotoPath = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }

    val isRecording = remember { mutableStateOf(false) }
    val recordingTime = remember { mutableStateOf(0L) }
    val recordingError = remember { mutableStateOf("") }
    val hasRecorded = remember { mutableStateOf(false) }
    val enableAudio = remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    // Update recording time
    LaunchedEffect(isRecording.value) {
        while (isRecording.value) {
            recordingTime.value = audioRecorder.getRecordingDurationMs()
            delay(100)
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && capturedPhotoUri.value != null) {
            val inputStream = context.contentResolver.openInputStream(capturedPhotoUri.value!!)
            val outputFile = File(context.filesDir, "photos").apply { mkdirs() }
            val photoFile = File(outputFile, "photo_${System.currentTimeMillis()}.jpg")
            inputStream?.copyTo(photoFile.outputStream())
            capturedPhotoPath.value = photoFile.absolutePath
        }
    }

    fun launchCamera() {
        if (!cameraPermissionState.hasPermission) {
            cameraPermissionState.launchPermissionRequest()
        } else {
            val photoFile = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            capturedPhotoUri.value = photoUri
            cameraLauncher.launch(photoUri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Photo Capture Task",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (capturedPhotoPath.value.isEmpty()) {
            // Camera capture button
            Button(
                onClick = { launchCamera() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Capture Image")
            }
        } else {
            // Display captured photo
            AsyncImage(
                model = File(capturedPhotoPath.value),
                contentDescription = "Captured Photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )

            // Description input
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text("Describe the photo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                maxLines = 4
            )

            // Optional audio recording
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Audio Description:")
                OutlinedButton(
                    onClick = { enableAudio.value = !enableAudio.value }
                ) {
                    Text(if (enableAudio.value) "Enabled" else "Enable Audio")
                }
            }

            if (enableAudio.value) {
                // Recording time display
                Text(
                    text = "${recordingTime.value / 1000}s",
                    fontSize = 28.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    color = if (recordingTime.value > 20000) Color.Red else MaterialTheme.colorScheme.primary
                )

                // Mic button
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .padding(16.dp)
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    when (event.type) {
                                        PointerEventType.Press -> {
                                            recordingError.value = ""
                                            audioRecorder.startRecording()
                                            isRecording.value = true
                                        }
                                        PointerEventType.Release -> {
                                            if (isRecording.value) {
                                                isRecording.value = false
                                                audioRecorder.stopRecording()
                                                recordingTime.value = audioRecorder.getRecordingDurationMs()

                                                val (isValid, error) = RecordingValidator.isValidDuration(recordingTime.value)
                                                if (!isValid) {
                                                    recordingError.value = error ?: "Invalid recording"
                                                    audioRecorder.getRecordingFile()?.delete()
                                                } else {
                                                    hasRecorded.value = true
                                                    recordingError.value = ""
                                                }
                                            }
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mic",
                            modifier = Modifier.size(50.dp),
                            tint = if (isRecording.value) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (recordingError.value.isNotEmpty()) {
                    Text(
                        text = recordingError.value,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = {
                        capturedPhotoPath.value = ""
                        description.value = ""
                        enableAudio.value = false
                        hasRecorded.value = false
                        recordingTime.value = 0L
                        audioRecorder.getRecordingFile()?.delete()
                    }
                ) {
                    Text("Retake Photo")
                }

                Button(
                    onClick = {
                        val audioPath = if (enableAudio.value && hasRecorded.value) {
                            audioRecorder.getRecordingFile()?.absolutePath ?: ""
                        } else {
                            ""
                        }
                        val duration = if (enableAudio.value && hasRecorded.value) {
                            (recordingTime.value / 1000).toInt()
                        } else {
                            0
                        }
                        onTaskSubmitted(
                            capturedPhotoPath.value,
                            description.value,
                            audioPath.ifEmpty { null },
                            duration
                        )
                    },
                    enabled = description.value.isNotEmpty() && (!enableAudio.value || hasRecorded.value)
                ) {
                    Text("Submit")
                }
            }
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Back to Selection")
        }
    }
}
