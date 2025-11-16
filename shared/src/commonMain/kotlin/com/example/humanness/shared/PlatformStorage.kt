package com.example.humanness.shared

expect object PlatformStorage {
    fun setBasePath(path: String)
    fun getBasePath(): String?
    suspend fun saveTasksJson(json: String)
    suspend fun loadTasksJson(): String?
}
