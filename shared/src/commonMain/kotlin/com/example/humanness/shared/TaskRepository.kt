package com.example.humanness.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object TaskRepository {
    private val tasks = mutableStateListOf<TaskRecord>()

    init {
        // try load from platform storage
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val json = PlatformStorage.loadTasksJson()
                if (json != null) {
                    val list = Json.decodeFromString<List<TaskRecord>>(json)
                    tasks.addAll(list)
                }
            } catch (e: Exception) {
                // ignore for prototype
            }
        }
    }

    fun addTask(t: TaskRecord) {
        tasks.add(0, t)
        saveAll()
    }

    fun getAll(): List<TaskRecord> = tasks.toList()

    private fun saveAll() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val json = Json.encodeToString(tasks.toList())
                PlatformStorage.saveTasksJson(json)
            } catch (e: Exception) {
                // ignore in prototype
            }
        }
    }
}
