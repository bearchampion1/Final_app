package tw.edu.example.aitodostarter.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 代表代辦事項的資料模型 (Room Entity)
 */
@Entity(tableName = "todos")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // 自動生成的唯一識別碼
    val title: String, // 代辦事項標題
    val isDone: Boolean = false, // 是否已完成
    val isUrgent: Boolean = false, // 是否為緊急項目 (Requirement 4 新增)
)
