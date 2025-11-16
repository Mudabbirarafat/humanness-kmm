package com.example.humanness.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

sealed class Screen {
    object Start : Screen()
    object NoiseTest : Screen()
    object TaskSelection : Screen()
    object TextReading : Screen()
    object ImageDescription : Screen()
    object PhotoCapture : Screen()
    object TaskHistory : Screen()
}

@Composable
fun SharedApp() {
    var screen by remember { mutableStateOf<Screen>(Screen.Start) }
    val repo = remember { TaskRepository }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val s = screen) {
            is Screen.Start -> StartScreen { screen = Screen.NoiseTest }
            is Screen.NoiseTest -> NoiseTestScreen(onPass = { screen = Screen.TaskSelection }, onBack = { screen = Screen.Start })
            is Screen.TaskSelection -> TaskSelectionScreen(onChoose = {
                screen = when (it) {
                    "text" -> Screen.TextReading
                    "image" -> Screen.ImageDescription
                    "photo" -> Screen.PhotoCapture
                    else -> Screen.TaskSelection
                }
            }, onHistory = { screen = Screen.TaskHistory }, onBack = { screen = Screen.Start })
            is Screen.TextReading -> TextReadingScreen(onSubmit = { task ->
                repo.addTask(task)
                screen = Screen.TaskSelection
            }, onCancel = { screen = Screen.TaskSelection })
            is Screen.ImageDescription -> ImageDescriptionScreen(onSubmit = { task ->
                repo.addTask(task)
                screen = Screen.TaskSelection
            }, onCancel = { screen = Screen.TaskSelection })
            is Screen.PhotoCapture -> PhotoCaptureScreen(onSubmit = { task ->
                repo.addTask(task)
                screen = Screen.TaskSelection
            }, onCancel = { screen = Screen.TaskSelection })
            is Screen.TaskHistory -> TaskHistoryScreen(onBack = { screen = Screen.TaskSelection })
        }
    }
}

@Composable
fun StartScreen(onStart: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Let’s start with a Sample Task for practice.", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Pehele hum ek sample task karte hain.")
        Spacer(Modifier.height(24.dp))
        Button(onClick = onStart) { Text("Start Sample Task") }
    }
}

@Composable
fun NoiseTestScreen(onPass: () -> Unit, onBack: () -> Unit) {
    val coroutine = rememberCoroutineScope()
    var db by remember { mutableStateOf(0) }
    var avg by remember { mutableStateOf(0) }
    var running by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Noise Test", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(8.dp))
        Box(modifier = Modifier.size(200.dp).background(Color.LightGray, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("dB: $db", fontSize = 30.sp)
                Spacer(Modifier.height(8.dp))
                Text("Avg: $avg dB")
            }
        }
        Spacer(Modifier.height(12.dp))
        Row { Button(onClick = {
            if (!running) {
                running = true
                coroutine.launch {
                    var sum = 0
                    var count = 0
                    repeat(10) {
                        val simulated = (30..55).random()
                        db = simulated
                        sum += simulated
                        count++
                        avg = sum / count
                        delay(300)
                    }
                    running = false
                }
            }
        }) { Text("Start Test") }
        Spacer(Modifier.width(8.dp))
        Button(onClick = onBack) { Text("Back") }}

        Spacer(Modifier.height(12.dp))
        if (!running && avg > 0) {
            if (avg < 40) {
                Text("Good to proceed", color = Color.Green)
                Spacer(Modifier.height(8.dp))
                Button(onClick = onPass) { Text("Continue") }
            } else {
                Text("Please move to a quieter place", color = Color.Red)
            }
        }
    }
}

@Composable
fun TaskSelectionScreen(onChoose: (String) -> Unit, onHistory: () -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Choose a task", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onChoose("text") }, modifier = Modifier.fillMaxWidth()) { Text("Text Reading Task") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onChoose("image") }, modifier = Modifier.fillMaxWidth()) { Text("Image Description Task") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onChoose("photo") }, modifier = Modifier.fillMaxWidth()) { Text("Photo Capture Task") }
        Spacer(Modifier.height(24.dp))
        Row { Button(onClick = onHistory) { Text("Task History") } Spacer(Modifier.width(8.dp)); Button(onClick = onBack) { Text("Back") } }
    }
}

@Composable
fun TextReadingScreen(onSubmit: (TaskRecord) -> Unit, onCancel: () -> Unit) {
    val coroutine = rememberCoroutineScope()
    var recording by remember { mutableStateOf(false) }
    var startTs by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    var audioPath by remember { mutableStateOf<String?>(null) }
    var cb1 by remember { mutableStateOf(false) }
    var cb2 by remember { mutableStateOf(false) }
    var cb3 by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Read the passage aloud in your native language.", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Mega long lasting fragrance... (sample text to read)")
        Spacer(Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxWidth().height(80.dp).background(Color(0xFFEFEFEF), RoundedCornerShape(8.dp)).pointerInput(Unit) {
            detectPressForRecording(onStart = {
                recording = true
                val ok = PlatformAudio.startRecording()
                if (!ok) {
                    recording = false
                    error = "Failed to start recording (permission missing?)"
                } else {
                    error = null
                }
            }, onStop = {
                recording = false
                val res = PlatformAudio.stopRecording()
                if (res == null) {
                    error = "Recording failed"
                    audioPath = null
                    duration = 0
                } else {
                    duration = res.durationSec
                    audioPath = res.path
                    if (duration < 10) {
                        error = "Recording too short (min 10 s)."
                        audioPath = null
                    } else if (duration > 20) {
                        error = "Recording too long (max 20 s)."
                        audioPath = null
                    } else {
                        error = null
                    }
                }
            })
        }, contentAlignment = Alignment.Center) {
            Text(if (recording) "Recording..." else "Press and hold mic to record")
        }
        Spacer(Modifier.height(8.dp))
        error?.let { Text(it, color = Color.Red) }
        Spacer(Modifier.height(8.dp))
        audioPath?.let { Text("Saved: $it (duration ${duration}s)") }

        Spacer(Modifier.height(8.dp))
        Row { Checkbox(checked = cb1, onCheckedChange = { cb1 = it }); Spacer(Modifier.width(6.dp)); Text("No background noise") }
        Row { Checkbox(checked = cb2, onCheckedChange = { cb2 = it }); Spacer(Modifier.width(6.dp)); Text("No mistakes while reading") }
        Row { Checkbox(checked = cb3, onCheckedChange = { cb3 = it }); Spacer(Modifier.width(6.dp)); Text("Beech me koi galti nahi hai") }

        Spacer(Modifier.height(12.dp))
        Row { Button(onClick = {
            // reset to re-record
            audioPath = null; duration = 0; error = null
        }) { Text("Record again") }
        Spacer(Modifier.width(8.dp))
        Button(onClick = {
            if (audioPath != null && cb1 && cb2 && cb3) {
                val task = TaskRecord(id = java.util.UUID.randomUUID().toString(), task_type = "text_reading", text = "Mega long lasting fragrance...", audio_path = audioPath, duration_sec = duration, timestamp = Instant.now().toString())
                onSubmit(task)
            }
        }, enabled = (audioPath != null && cb1 && cb2 && cb3)) { Text("Submit") }
        Spacer(Modifier.width(8.dp))
        Button(onClick = onCancel) { Text("Cancel") }
    }
}

@Composable
fun ImageDescriptionScreen(onSubmit: (TaskRecord) -> Unit, onCancel: () -> Unit) {
    var recording by remember { mutableStateOf(false) }
    var startTs by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    var audioPath by remember { mutableStateOf<String?>(null) }
    val sampleImage = "https://cdn.dummyjson.com/product-images/14/2.jpg"

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Describe what you see in your native language.", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Image: $sampleImage")
        Spacer(Modifier.height(12.dp))

        Box(modifier = Modifier.fillMaxWidth().height(80.dp).background(Color(0xFFEFEFEF), RoundedCornerShape(8.dp)).pointerInput(Unit) {
            detectPressForRecording(onStart = {
                recording = true
                val ok = PlatformAudio.startRecording()
                if (!ok) { recording = false; error = "Failed to start recording" } else { error = null }
            }, onStop = {
                recording = false
                val res = PlatformAudio.stopRecording()
                if (res == null) {
                    error = "Recording failed"
                    audioPath = null
                    duration = 0
                } else {
                    duration = res.durationSec
                    audioPath = res.path
                    if (duration < 10) { error = "Recording too short (min 10 s)."; audioPath = null }
                    else if (duration > 20) { error = "Recording too long (max 20 s)."; audioPath = null }
                    else { error = null }
                }
            })
        }, contentAlignment = Alignment.Center) {
            Text(if (recording) "Recording..." else "Press and hold mic to record")
        }
        Spacer(Modifier.height(8.dp))
        error?.let { Text(it, color = Color.Red) }
        audioPath?.let { Text("Saved: $it (duration ${duration}s)") }

        Spacer(Modifier.height(12.dp))
        Row { Button(onClick = { /* re-record */ audioPath = null; duration = 0 }) { Text("Record again") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (audioPath != null) {
                    val task = TaskRecord(id = java.util.UUID.randomUUID().toString(), task_type = "image_description", image_url = sampleImage, audio_path = audioPath, duration_sec = duration, timestamp = Instant.now().toString())
                    onSubmit(task)
                }
            }, enabled = (audioPath != null)) { Text("Submit") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onCancel) { Text("Cancel") }
        }
    }
}

@Composable
fun PhotoCaptureScreen(onSubmit: (TaskRecord) -> Unit, onCancel: () -> Unit) {
    var imagePath by remember { mutableStateOf<String?>(null) }
    var desc by remember { mutableStateOf("") }
    var audioPath by remember { mutableStateOf<String?>(null) }
    var duration by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Photo Capture", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { imagePath = "/local/path/photo_${Instant.now().epochSecond}.jpg" }) { Text("Capture Image") }
        Spacer(Modifier.height(8.dp))
        imagePath?.let { Text("Preview: $it") }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Describe the photo in your language.") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth().height(60.dp).background(Color(0xFFEFEFEF), RoundedCornerShape(8.dp)).pointerInput(Unit) {
            detectPressForRecording(onStart = {
                val ok = PlatformAudio.startRecording()
                if (ok) { /* started */ } else { /* ignore */ }
            }, onStop = {
                val res = PlatformAudio.stopRecording()
                if (res != null) {
                    duration = res.durationSec
                    audioPath = res.path
                }
            })
        }, contentAlignment = Alignment.Center) {
            Text("Optional: Press and hold to record description")
        }

        Spacer(Modifier.height(12.dp))
        Row {
            Button(onClick = { imagePath = null; audioPath = null; desc = "" }) { Text("Retake Photo") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                if (imagePath != null) {
                    val task = TaskRecord(id = java.util.UUID.randomUUID().toString(), task_type = "photo_capture", image_path = imagePath, audio_path = audioPath, duration_sec = duration, timestamp = Instant.now().toString())
                    onSubmit(task)
                }
            }, enabled = (imagePath != null)) { Text("Submit") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onCancel) { Text("Cancel") }
        }
    }
}

@Composable
fun TaskHistoryScreen(onBack: () -> Unit) {
    val repo = remember { TaskRepository }
    val tasks by remember { derivedStateOf { repo.getAll() } }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Tasks: ${tasks.size}")
            val totalDur = tasks.sumOf { it.duration_sec }
            Text("Total Duration: ${totalDur}s")
        }
        Spacer(Modifier.height(8.dp))
        tasks.forEach { t ->
            Card(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("${t.task_type} - ${t.duration_sec}s")
                        Text(t.timestamp)
                        Text(t.text ?: t.image_url ?: t.image_path ?: "")
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onBack) { Text("Back") }
    }
}

// Utility: pointer input helper for press-and-hold
suspend fun androidx.compose.ui.input.pointer.PointerInputScope.detectPressForRecording(onStart: () -> Unit, onStop: () -> Unit) {
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown()
            onStart()
            val up = waitForUpOrCancellation()
            onStop()
        }
    }
}

