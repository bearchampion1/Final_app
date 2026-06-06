package tw.edu.example.aitodostarter.ui

import tw.edu.example.aitodostarter.data.TodoItem
import tw.edu.example.aitodostarter.data.TodoRepository

/**
 * 代辦事項清單的 UI 狀態模型
 */
data class TodoUiState(
    val todos: List<TodoItem> = emptyList(), // 原始完整清單
    val filteredTodos: List<TodoItem> = emptyList(), // 搜尋或篩選後的清單 (Requirement 4)
    val inputText: String = "", // 輸入框文字
    val searchQuery: String = "", // 目前的搜尋關鍵字
    val isUrgentInput: Boolean = false, // 新增時是否勾選緊急
    val showUrgentOnly: Boolean = false, // 是否僅顯示緊急項目
)

/**
 * 代辦事項邏輯控制類別 (ViewModel 角色)
 */
class TodoController(
    private val repository: TodoRepository,
) {
    // 初始狀態：從倉庫獲取資料並同步給 filtered 清單
    var state: TodoUiState = TodoUiState(
        todos = repository.getTodos(),
        filteredTodos = repository.getTodos()
    )
        private set

    /** 更新使用者輸入的內容 */
    fun updateInput(text: String) {
        state = state.copy(inputText = text)
    }

    /** 切換新增時的緊急狀態標記 */
    fun toggleUrgentInput() {
        state = state.copy(isUrgentInput = !state.isUrgentInput)
    }

    /** 設定是否僅顯示緊急事項並刷新清單 */
    fun setShowUrgentOnly(show: Boolean) {
        state = state.copy(showUrgentOnly = show)
        refreshList()
    }

    /** 執行關鍵字搜尋 (Requirement 4) */
    fun search() {
        val query = state.inputText
        state = state.copy(searchQuery = query, showUrgentOnly = false)
        refreshList()
    }

    /** 讀取輸入欄位並新增事項 (由 FAB 觸發 - Requirement 4) */
    fun addTodo() {
        if (state.inputText.isBlank()) {
            return
        }

        // 將輸入框文字與緊急標記存入倉庫
        repository.addTodo(state.inputText, state.isUrgentInput)
        // 清空輸入狀態並刷新清單
        state = state.copy(inputText = "", isUrgentInput = false)
        refreshList()
    }

    /** 切換事項完成狀態並刷新 UI (Requirement 3) */
    fun toggleTodo(id: Int) {
        repository.toggleTodo(id)
        refreshList()
    }

    /** 刪除事項並刷新 UI (Requirement 2) */
    fun deleteTodo(id: Int) {
        repository.deleteTodo(id)
        refreshList()
    }

    /**
     * 核心清單刷新邏輯：
     * 1. 根據是否為「緊急模式」決定資料來源
     * 2. 根據「搜尋關鍵字」進行文字篩選
     */
    private fun refreshList() {
        // 1. 決定資料來源
        val sourceList = if (state.showUrgentOnly) {
            repository.getUrgentTodos()
        } else {
            repository.getTodos()
        }

        // 2. 進行搜尋篩選
        val filtered = if (state.searchQuery.isBlank()) {
            sourceList
        } else {
            sourceList.filter { it.title.contains(state.searchQuery, ignoreCase = true) }
        }

        // 3. 更新狀態
        state = state.copy(
            todos = repository.getTodos(),
            filteredTodos = filtered
        )
    }

    /** 計算尚未完成的項目總數 */
    fun activeCount(): Int = state.todos.count { !it.isDone }
}
