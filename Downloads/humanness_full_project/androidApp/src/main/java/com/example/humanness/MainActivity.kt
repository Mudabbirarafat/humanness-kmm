package com.example.humanness

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.humanness.db.TaskRepository
import com.example.humanness.ui.navigation.Screen
import com.example.humanness.ui.screens.ImageDescriptionTaskScreen
import com.example.humanness.ui.screens.NoiseTestScreen
import com.example.humanness.ui.screens.PhotoCaptureTaskScreen
import com.example.humanness.ui.screens.StartScreen
import com.example.humanness.ui.screens.TaskHistoryScreen
import com.example.humanness.ui.screens.TaskSelectionScreen
import com.example.humanness.ui.screens.TextReadingTaskScreen
import com.example.humanness.ui.theme.HumannessTheme
import kotlinx.coroutines.launch
import shared.TaskRecord
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.uuid.Uuid

class MainActivity : ComponentActivity() {
    private lateinit var taskRepository: TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskRepository = TaskRepository(this)

        setContent {
            HumannessTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation(taskRepository, this@MainActivity)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(repository: TaskRepository, context: android.content.Context) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Start.route) {
        composable(Screen.Start.route) {
            StartScreen(onNavigateToNoiseTest = { navController.navigate(Screen.NoiseTest.route) })
        }

        composable(Screen.NoiseTest.route) {
            NoiseTestScreen(
                onPassTest = { navController.navigate(Screen.TaskSelection.route) },
                onFailTest = { navController.popBackStack() }
            )
        }

        composable(Screen.TaskSelection.route) {
            TaskSelectionScreen(
                onSelectTextReading = { navController.navigate(Screen.TextReading.route) },
                onSelectImageDescription = { navController.navigate(Screen.ImageDescription.route) },
                onSelectPhotoCapture = { navController.navigate(Screen.PhotoCapture.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TextReading.route) {
            TextReadingTaskScreen(
                context = context,
                onTaskSubmitted = { audioPath, duration ->
                    saveTask(
                        repository,
                        context,
                        "text_reading",
                        audioPath = audioPath,
                        duration = duration
                    )
                    navController.navigate(Screen.TaskSelection.route) {
                        popUpTo(Screen.TaskSelection.route) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ImageDescription.route) {
            ImageDescriptionTaskScreen(
                context = context,
                onTaskSubmitted = { imageUrl, audioPath, duration ->
                    saveTask(
                        repository,
                        context,
                        "image_description",
                        imageUrl = imageUrl,
                        audioPath = audioPath,
                        duration = duration
                    )
                    navController.navigate(Screen.TaskSelection.route) {
                        popUpTo(Screen.TaskSelection.route) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.PhotoCapture.route) {
            PhotoCaptureTaskScreen(
                context = context,
                onTaskSubmitted = { imagePath, description, audioPath, duration ->
                    saveTask(
                        repository,
                        context,
                        "photo_capture",
                        imagePath = imagePath,
                        audioPath = audioPath,
                        duration = duration
                    )
                    navController.navigate(Screen.TaskSelection.route) {
                        popUpTo(Screen.TaskSelection.route) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TaskHistory.route) {
            TaskHistoryScreen(
                context = context,
                onBack = { navController.popBackStack(Screen.Start.route, false) }
            )
        }
    }
}

private fun saveTask(
    repository: TaskRepository,
    context: android.content.Context,
    taskType: String,
    audioPath: String = "",
    imageUrl: String = "",
    imagePath: String = "",
    duration: Int = 0
) {
    val activity = context as? MainActivity ?: return
    val task = TaskRecord(
        id = Uuid.random().toString(),
        task_type = taskType,
        audio_path = audioPath.ifEmpty { null },
        image_url = imageUrl.ifEmpty { null },
        image_path = imagePath.ifEmpty { null },
        duration_sec = duration,
        timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    )

    activity.lifecycleScope.launch {
        repository.insertTask(task)
    }
}