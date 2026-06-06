package tw.edu.example.aitodostarter.data

/**
 * 實作 TodoRepository 的 Room 版本 (Requirement 1)
 */
class RoomTodoRepository(private val todoDao: TodoDao) : TodoRepository {
    
    /** 從 Room 資料庫獲取所有項目 */
    override fun getTodos(): List<TodoItem> {
        return todoDao.getAll()
    }

    /** 從 Room 資料庫獲取緊急項目 */
    override fun getUrgentTodos(): List<TodoItem> {
        return todoDao.getUrgentAll()
    }

    /** 插入新項目到 Room 並回傳包含 ID 的物件 */
    override fun addTodo(title: String, isUrgent: Boolean): TodoItem {
        val todo = TodoItem(title = title, isUrgent = isUrgent)
        val id = todoDao.insert(todo)
        return todo.copy(id = id.toInt())
    }

    /** 根據 ID 查詢並反轉完成狀態 */
    override fun toggleTodo(id: Int) {
        val todo = todoDao.getById(id)
        if (todo != null) {
            todoDao.update(todo.copy(isDone = !todo.isDone))
        }
    }

    /** 根據 ID 從資料庫刪除項目 (Requirement 2) */
    override fun deleteTodo(id: Int) {
        todoDao.deleteById(id)
    }
}
