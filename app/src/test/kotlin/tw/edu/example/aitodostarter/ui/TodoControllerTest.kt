package tw.edu.example.aitodostarter.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import tw.edu.example.aitodostarter.data.TodoItem
import tw.edu.example.aitodostarter.data.TodoRepository

class FakeTodoRepository : TodoRepository {
    private val todos = mutableListOf(
        TodoItem(id = 1, title = "Task 1"),
        TodoItem(id = 2, title = "Task 2")
    )
    private var nextId = 3

    override fun getTodos(): List<TodoItem> = todos.toList()

    override fun addTodo(title: String): TodoItem {
        val todo = TodoItem(id = nextId++, title = title)
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

class TodoControllerTest {

    @Test
    fun addTodo_withNonBlankInput_addsTodoAndClearsInput() {
        val controller = TodoController(FakeTodoRepository())
        controller.updateInput("Prepare final demo")
        controller.addTodo()

        assertEquals("", controller.state.inputText)
        assertTrue(controller.state.todos.any { it.title == "Prepare final demo" })
    }

    @Test
    fun toggleTodo_toDoneAndThenToUndone_worksCorrectly() {
        val controller = TodoController(FakeTodoRepository())
        controller.updateInput("New Task")
        controller.addTodo()
        val newTodo = controller.state.todos.last()

        // Mark as done
        controller.toggleTodo(newTodo.id)
        assertTrue(controller.state.todos.first { it.id == newTodo.id }.isDone)

        // Mark as undone
        controller.toggleTodo(newTodo.id)
        assertFalse(controller.state.todos.first { it.id == newTodo.id }.isDone)
    }

    @Test
    fun deleteTodo_afterMarkingDone_removesItem() {
        val controller = TodoController(FakeTodoRepository())
        controller.updateInput("Task to delete")
        controller.addTodo()
        val todoToDelete = controller.state.todos.last()

        controller.toggleTodo(todoToDelete.id)
        assertTrue(controller.state.todos.first { it.id == todoToDelete.id }.isDone)

        controller.deleteTodo(todoToDelete.id)
        assertFalse(controller.state.todos.any { it.id == todoToDelete.id })
    }

    @Test
    fun deleteTodo_withMultipleItems_onlyRemovesTargetItem() {
        val controller = TodoController(FakeTodoRepository())
        val initialSize = controller.state.todos.size
        val firstTodoId = controller.state.todos[0].id
        val secondTodoId = controller.state.todos[1].id

        controller.deleteTodo(firstTodoId)

        assertEquals(initialSize - 1, controller.state.todos.size)
        assertFalse(controller.state.todos.any { it.id == firstTodoId })
        assertTrue(controller.state.todos.any { it.id == secondTodoId })
    }
}
