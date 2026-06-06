package tw.edu.example.aitodostarter.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import tw.edu.example.aitodostarter.data.TodoItem
import tw.edu.example.aitodostarter.data.TodoRepository

/**
 * 用於測試的偽造代辦事項倉庫 (Fake)
 */
class FakeTodoRepository : TodoRepository {
    private val todos = mutableListOf(
        TodoItem(id = 1, title = "Task 1"),
        TodoItem(id = 2, title = "Task 2")
    )
    private var nextId = 3

    override fun getTodos(): List<TodoItem> = todos.toList()

    override fun getUrgentTodos(): List<TodoItem> = todos.filter { it.isUrgent }

    override fun addTodo(title: String, isUrgent: Boolean): TodoItem {
        val todo = TodoItem(id = nextId++, title = title, isUrgent = isUrgent)
        todos.add(todo)
        return todo
    }

    override fun toggleTodo(id: Int) {
        val index = todos.indexOfFirst { it.id == id }
        if (index >= 0) {
            todos[index] = todos[index].copy(isDone = !todos[index].isDone)
        }
    }

    override fun deleteTodo(id: Int) {
        todos.removeAll { it.id == id }
    }
}

/**
 * 針對 TodoController 進行功能驗證的自動化測試 (Requirement 3 & 4)
 */
class TodoControllerTest {

    /** 測試：新增非空白事項後，輸入框應清空且清單應包含該事項 */
    @Test
    fun addTodo_withNonBlankInput_addsTodoAndClearsInput() {
        val controller = TodoController(FakeTodoRepository())
        controller.updateInput("Prepare final demo")
        controller.addTodo()

        assertEquals("", controller.state.inputText)
        assertTrue(controller.state.todos.any { it.title == "Prepare final demo" })
        assertTrue(controller.state.filteredTodos.any { it.title == "Prepare final demo" })
    }

    /** 測試：切換完成狀態功能 (Done -> Undone) (Requirement 3) */
    @Test
    fun toggleTodo_toDoneAndThenToUndone_worksCorrectly() {
        val controller = TodoController(FakeTodoRepository())
        controller.updateInput("New Task")
        controller.addTodo()
        val newTodo = controller.state.todos.last()

        // 1. 標記為 Done
        controller.toggleTodo(newTodo.id)
        assertTrue(controller.state.todos.first { it.id == newTodo.id }.isDone)

        // 2. 標記為 Undone
        controller.toggleTodo(newTodo.id)
        assertFalse(controller.state.todos.first { it.id == newTodo.id }.isDone)
    }

    /** 測試：新增後標記完成並刪除 (Requirement 3) */
    @Test
    fun deleteTodo_afterMarkingDone_removesItem() {
        val controller = TodoController(FakeTodoRepository())
        controller.updateInput("Task to delete")
        controller.addTodo()
        val todoToDelete = controller.state.todos.last()

        controller.toggleTodo(todoToDelete.id)
        assertTrue(controller.state.todos.first { it.id == todoToDelete.id }.isDone)

        // 執行刪除
        controller.deleteTodo(todoToDelete.id)
        assertFalse(controller.state.todos.any { it.id == todoToDelete.id })
    }

    /** 測試：當有多筆資料時，刪除應只影響選定的那一筆 (Requirement 3) */
    @Test
    fun deleteTodo_withMultipleItems_onlyRemovesTargetItem() {
        val controller = TodoController(FakeTodoRepository())
        val initialSize = controller.state.todos.size
        val firstTodoId = controller.state.todos[0].id
        val secondTodoId = controller.state.todos[1].id

        controller.deleteTodo(firstTodoId)

        // 驗證大小減少且正確項目被刪除
        assertEquals(initialSize - 1, controller.state.todos.size)
        assertFalse(controller.state.todos.any { it.id == firstTodoId })
        assertTrue(controller.state.todos.any { it.id == secondTodoId })
    }

    /** 測試：關鍵字搜尋篩選清單 (Requirement 4) */
    @Test
    fun search_withQuery_filtersList() {
        val controller = TodoController(FakeTodoRepository())
        controller.updateInput("Task 1")
        controller.search()

        assertEquals(1, controller.state.filteredTodos.size)
        assertEquals("Task 1", controller.state.filteredTodos[0].title)
    }

    /** 測試：空關鍵字搜尋應顯示所有資料 (Requirement 4) */
    @Test
    fun search_withBlankQuery_showsAll() {
        val controller = TodoController(FakeTodoRepository())
        controller.updateInput("")
        controller.search()

        assertEquals(2, controller.state.filteredTodos.size)
    }

    /** 測試：搜尋時不應區分字母大小寫 (Requirement 4) */
    @Test
    fun search_isCaseInsensitive() {
        val controller = TodoController(FakeTodoRepository())
        controller.updateInput("task 2")
        controller.search()

        assertEquals(1, controller.state.filteredTodos.size)
        assertEquals("Task 2", controller.state.filteredTodos[0].title)
    }

    /** 測試：新增緊急事項應正確標記 (Requirement 4 規劃) */
    @Test
    fun addTodo_asUrgent_appearsInFilteredList() {
        val controller = TodoController(FakeTodoRepository())
        controller.updateInput("Urgent Task")
        controller.toggleUrgentInput()
        controller.addTodo()

        assertTrue(controller.state.todos.first { it.title == "Urgent Task" }.isUrgent)
    }

    /** 測試：切換為緊急事項過濾模式時的篩選準確性 */
    @Test
    fun setShowUrgentOnly_filtersListCorrectly() {
        val controller = TodoController(FakeTodoRepository())
        controller.updateInput("Urgent One")
        controller.toggleUrgentInput()
        controller.addTodo()

        controller.updateInput("Normal One")
        controller.addTodo()

        controller.setShowUrgentOnly(true)
        assertEquals(1, controller.state.filteredTodos.size)
        assertEquals("Urgent One", controller.state.filteredTodos[0].title)
    }
}
