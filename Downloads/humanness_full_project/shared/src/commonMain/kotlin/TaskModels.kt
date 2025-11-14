package shared

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class TaskRecord(
    val id: String = Uuid.random().toString(),
    val task_type: String,
    val text: String? = null,
    val image_url: String? = null,
    val image_path: String? = null,
    val audio_path: String? = null,
    val duration_sec: Int,
    val timestamp: String,
    val checkpoints: List<String>? = null
)

@Serializable
data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val images: List<String>
)

@Serializable
data class ProductsResponse(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

enum class TaskType {
    TEXT_READING,
    IMAGE_DESCRIPTION,
    PHOTO_CAPTURE
}

enum class NoiseLevel {
    GOOD,
    NEEDS_QUIET
}