package com.example.humanness.shared

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSString
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask

actual object PlatformStorage {
    private var basePath: String? = null

    actual fun setBasePath(path: String) {
        basePath = path
    }

    actual fun getBasePath(): String? = basePath

    private fun defaultDocumentsPath(): String {
        val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        return if (paths.count > 0) paths.objectAtIndex(0) as String else NSHomeDirectory() + "/Documents"
    }

    actual suspend fun saveTasksJson(json: String) {
        withContext(Dispatchers.Default) {
            val base = basePath ?: defaultDocumentsPath()
            val filePath = "$base/tasks.json"
            val ns = NSString.create(string = json)
            try {
                ns.writeToFile(filePath, atomically = true, encoding = 4u, error = null)
            } catch (t: Throwable) {
                // ignore
            }
        }
    }

    actual suspend fun loadTasksJson(): String? {
        return withContext(Dispatchers.Default) {
            val base = basePath ?: defaultDocumentsPath()
            val filePath = "$base/tasks.json"
            try {
                val ns = NSString.stringWithContentsOfFile(filePath, 4u, null)
                ns as? String
            } catch (t: Throwable) {
                null
            }
        }
    }
}
