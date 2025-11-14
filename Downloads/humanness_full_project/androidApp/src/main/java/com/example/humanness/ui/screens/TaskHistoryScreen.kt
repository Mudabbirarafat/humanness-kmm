package com.example.humanness.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.humanness.db.TaskRepository
import shared.TaskRecord
import java.io.File

@Composable
fun TaskHistoryScreen(
    context: Context,
    onBack: () -> Unit
) {
    val repository = remember { TaskRepository(context) }
    val tasks = remember { mutableStateOf<List<TaskRecord>>(emptyList()) }
    val totalCount = remember { mutableStateOf(0) }
    val totalDuration = remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        tasks.value = repository.getAllTasks()
        totalCount.value = repository.getTaskCount()
        totalDuration.value = repository.getTotalDuration()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Total Tasks",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = totalCount.value.toString(),
                    fontSize = 24.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Total Duration",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "${totalDuration.value}s",
                    fontSize = 24.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }

        // Task list
        if (tasks.value.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No tasks completed yet",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp)
            ) {
                items(tasks.value) { task ->
                    TaskHistoryItem(
                        task = task,
                        onDelete = {
                            repository.deleteTask(task.id)
                            tasks.value = tasks.value.filter { it.id != task.id }
                            totalCount.value = tasks.value.size
                            totalDuration.value = tasks.value.sumOf { it.duration_sec.toLong() }
                        }
                    )
                }
            }
        }

        // Back button
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Back to Start")
        }
    }
}

@Composable
fun TaskHistoryItem(
    task: TaskRecord,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Task ID: ${task.id.take(8)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Type: ${task.task_type}",
                        fontSize = 14.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        text = "Duration: ${task.duration_sec}s | ${task.timestamp}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }

            // Preview section
            when (task.task_type) {
                "text_reading" -> {
                    if (!task.text.isNullOrEmpty()) {
                        Text(
                            text = task.text,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                "image_description" -> {
                    if (!task.image_url.isNullOrEmpty()) {
                        AsyncImage(
                            model = task.image_url,
                            contentDescription = "Task Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(top = 8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                "photo_capture" -> {
                    if (!task.image_path.isNullOrEmpty() && File(task.image_path).exists()) {
                        AsyncImage(
                            model = File(task.image_path),
                            contentDescription = "Captured Photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(top = 8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}
