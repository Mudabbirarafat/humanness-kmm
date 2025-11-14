package com.example.humanness.db

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val task_type: String,
    val text: String? = null,
    val image_url: String? = null,
    val image_path: String? = null,
    val audio_path: String? = null,
    val duration_sec: Int,
    val timestamp: String,
    val checkpoints: String? = null
)

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks ORDER BY timestamp DESC")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int

    @Query("SELECT SUM(duration_sec) FROM tasks")
    suspend fun getTotalDuration(): Long?

    @Query("SELECT * FROM tasks WHERE task_type = :taskType")
    suspend fun getTasksByType(taskType: String): List<TaskEntity>
}

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var instance: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}
