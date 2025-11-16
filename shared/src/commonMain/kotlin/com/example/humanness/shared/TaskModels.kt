package com.example.humanness.shared

import kotlinx.serialization.Serializable

@Serializable
data class TaskRecord(
    val id: String,
    val task_type: String,
    val text: String? = null,
    val image_url: String? = null,
    val image_path: String? = null,
    val audio_path: String? = null,
    val duration_sec: Int = 0,
    val timestamp: String = ""
)
