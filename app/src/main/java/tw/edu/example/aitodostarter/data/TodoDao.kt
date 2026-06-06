package tw.edu.example.aitodostarter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * 代辦事項的資料存取介面 (DAO)
 */
@Dao
interface TodoDao {
    /**
     * 獲取所有代辦事項
     */
    @Query("SELECT * FROM todos")
    fun getAll(): List<TodoItem>

    /**
     * 僅獲取緊急代辦事項
     */
    @Query("SELECT * FROM todos WHERE isUrgent = 1")
    fun getUrgentAll(): List<TodoItem>

    /**
     * 插入新的代辦事項
     */
    @Insert
    fun insert(todo: TodoItem): Long

    /**
     * 更新現有的代辦事項 (例如: 標記完成/未完成)
     */
    @Update
    fun update(todo: TodoItem)

    /**
     * 根據 ID 獲取特定代辦事項
     */
    @Query("SELECT * FROM todos WHERE id = :id")
    fun getById(id: Int): TodoItem?

    /**
     * 根據 ID 刪除特定代辦事項 (Requirement 2)
     */
    @Query("DELETE FROM todos WHERE id = :id")
    fun deleteById(id: Int)
}
