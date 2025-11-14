package com.example.humanness.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import coil.compose.AsyncImage
import com.example.humanness.audio.AudioPlayerImpl
import com.example.humanness.audio.AudioRecorderImpl
import kotlinx.coroutines.delay
import shared.ProductApiClient
import shared.RecordingValidator

@Composable
fun ImageDescriptionTaskScreen(
    context: android.content.Context,
    onTaskSubmitted: (imageUrl: String, audioPath: String, duration: Int) -> Unit,
    onBack: () -> Unit
) {
    val audioRecorder = remember { AudioRecorderImpl(context) }
    val audioPlayer = remember { AudioPlayerImpl() }
    val apiClient = remember { ProductApiClient() }

    val imageUrl = remember { mutableStateOf("") }
    val isLoadingImage = remember { mutableStateOf(true) }
    val isRecording = remember { mutableStateOf(false) }
    val recordingTime = remember { mutableStateOf(0L) }
    val recordingError = remember { mutableStateOf("") }
    val hasRecorded = remember { mutableStateOf(false) }

    // Fetch product image
    LaunchedEffect(Unit) {
        try {
            val products = apiClient.getProducts(1)
            if (products.isNotEmpty() && products[0].images.isNotEmpty()) {
                imageUrl.value = products[0].images[0]
            }
            isLoadingImage.value = false
        } catch (e: Exception) {
            imageUrl.value = "https://cdn.dummyjson.com/product-images/1/1.jpg"
            isLoadingImage.value = false
        }
    }

    // Update recording time
    LaunchedEffect(isRecording.value) {
        while (isRecording.value) {
            recordingTime.value = audioRecorder.getRecordingDurationMs()
            delay(100)
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
            text = "Image Description Task",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoadingImage.value) {
            CircularProgressIndicator()
        } else {
            // Display image
            if (imageUrl.value.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl.value,
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = "Describe what you see in your native language.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(16.dp)
            )

            // Recording time display
            Text(
                text = "${recordingTime.value / 1000}s",
                fontSize = 32.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(16.dp),
                color = if (recordingTime.value > 20000) Color.Red else MaterialTheme.colorScheme.primary
            )

            // Mic button
            Box(
                modifier = Modifier
                    .size(120.dp)
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
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Mic",
                        modifier = Modifier.size(60.dp),
                        tint = if (isRecording.value) Color.Red else MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (recordingError.value.isNotEmpty()) {
                Text(
                    text = recordingError.value,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (hasRecorded.value) {
                // Action buttons
                Button(
                    onClick = {
                        audioRecorder.getRecordingFile()?.let {
                            onTaskSubmitted(imageUrl.value, it.absolutePath, (recordingTime.value / 1000).toInt())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Submit")
                }

                OutlinedButton(
                    onClick = {
                        hasRecorded.value = false
                        recordingTime.value = 0L
                        audioRecorder.getRecordingFile()?.delete()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Record Again")
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
