package tw.edu.example.aitodostarter.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    fun getAll(): List<TodoItem>

    @Insert
    fun insert(todo: TodoItem): Long

    @Update
    fun update(todo: TodoItem)

    @Query("SELECT * FROM todos WHERE id = :id")
    fun getById(id: Int): TodoItem?

    @Query("DELETE FROM todos WHERE id = :id")
    fun deleteById(id: Int)
}
