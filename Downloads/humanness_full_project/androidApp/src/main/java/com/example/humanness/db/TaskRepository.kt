package com.example.humanness.db

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import shared.TaskRecord

class TaskRepository(context: Context) {
    private val taskDao = TaskDatabase.getDatabase(context).taskDao()

    suspend fun insertTask(task: TaskRecord) {
        withContext(Dispatchers.IO) {
            taskDao.insertTask(
                TaskEntity(
                    id = task.id,
                    task_type = task.task_type,
                    text = task.text,
                    image_url = task.image_url,
                    image_path = task.image_path,
                    audio_path = task.audio_path,
                    duration_sec = task.duration_sec,
                    timestamp = task.timestamp,
                    checkpoints = task.checkpoints?.joinToString(",")
                )
            )
        }
    }

    suspend fun getAllTasks(): List<TaskRecord> {
        return withContext(Dispatchers.IO) {
            taskDao.getAllTasks().map { entity ->
                TaskRecord(
                    id = entity.id,
                    task_type = entity.task_type,
                    text = entity.text,
                    image_url = entity.image_url,
                    image_path = entity.image_path,
                    audio_path = entity.audio_path,
                    duration_sec = entity.duration_sec,
                    timestamp = entity.timestamp,
                    checkpoints = entity.checkpoints?.split(",")
                )
            }
        }
    }

    suspend fun getTaskCount(): Int {
        return withContext(Dispatchers.IO) {
            taskDao.getTaskCount()
        }
    }

    suspend fun getTotalDuration(): Long {
        return withContext(Dispatchers.IO) {
            taskDao.getTotalDuration() ?: 0L
        }
    }

    suspend fun deleteTask(id: String) {
        withContext(Dispatchers.IO) {
            taskDao.getTaskById(id)?.let { taskDao.deleteTask(it) }
        }
    }
}
