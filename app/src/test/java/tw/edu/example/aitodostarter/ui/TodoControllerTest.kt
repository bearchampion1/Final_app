package tw.edu.example.aitodostarter.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import tw.edu.example.aitodostarter.data.InMemoryTodoRepository

class TodoControllerTest {
    @Test
    fun addTodo_withNonBlankInput_addsTodoAndClearsInput() {
        val controller = TodoController(InMemoryTodoRepository())

        controller.updateInput("Prepare final demo")
        controller.addTodo()

        assertEquals("", controller.state.inputText)
        assertTrue(controller.state.todos.any { it.title == "Prepare final demo" })
    }

    @Test
    fun addTodo_withBlankInput_doesNotAddTodo() {
        val controller = TodoController(InMemoryTodoRepository())
        val originalSize = controller.state.todos.size

        controller.updateInput("   ")
        controller.addTodo()

        assertEquals(originalSize, controller.state.todos.size)
    }

    @Test
    fun toggleTodo_changesDoneState() {
        val controller = TodoController(InMemoryTodoRepository())
        val firstTodo = controller.state.todos.first()

        controller.toggleTodo(firstTodo.id)

        val updatedTodo = controller.state.todos.first { it.id == firstTodo.id }
        assertFalse(firstTodo.isDone)
        assertTrue(updatedTodo.isDone)
    }
}
