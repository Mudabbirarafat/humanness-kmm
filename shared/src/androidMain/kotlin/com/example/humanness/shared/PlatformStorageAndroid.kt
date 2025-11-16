package com.example.humanness.shared

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

actual object PlatformStorage {
    private var basePath: String? = null

    actual fun setBasePath(path: String) {
        basePath = path
    }

    actual fun getBasePath(): String? = basePath

    private fun tasksFile(): File? {
        val base = basePath ?: return null
        return File(base, "tasks.json")
    }

    actual suspend fun saveTasksJson(json: String) {
        withContext(Dispatchers.IO) {
            val f = tasksFile() ?: return@withContext
            f.writeText(json)
        }
    }

    actual suspend fun loadTasksJson(): String? {
        return withContext(Dispatchers.IO) {
            val f = tasksFile() ?: return@withContext null
            if (!f.exists()) return@withContext null
            f.readText()
        }
    }
}
