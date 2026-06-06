package tw.edu.example.aitodostarter.data

/**
 * 代辦事項倉庫介面，定義業務邏輯所需的資料操作
 */
interface TodoRepository {
    /** 獲取所有代辦事項 */
    fun getTodos(): List<TodoItem>
    
    /** 獲取所有緊急代辦事項 (Requirement 4 規劃) */
    fun getUrgentTodos(): List<TodoItem>
    
    /** 新增代辦事項，支援設定是否緊急 */
    fun addTodo(title: String, isUrgent: Boolean = false): TodoItem
    
    /** 切換代辦事項的完成狀態 (Requirement 3) */
    fun toggleTodo(id: Int)
    
    /** 刪除代辦事項 (Requirement 2) */
    fun deleteTodo(id: Int)
}
