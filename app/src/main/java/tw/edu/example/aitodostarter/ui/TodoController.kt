package tw.edu.example.aitodostarter.ui

import tw.edu.example.aitodostarter.data.TodoItem
import tw.edu.example.aitodostarter.data.TodoRepository

data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val inputText: String = "",
)

class TodoController(
    private val repository: TodoRepository,
) {
    var state: TodoUiState = TodoUiState(todos = repository.getTodos())
        private set

    fun updateInput(text: String) {
        state = state.copy(inputText = text)
    }

    fun addTodo() {
        if (state.inputText.isBlank()) {
            return
        }

        repository.addTodo(state.inputText)
        state = state.copy(
            todos = repository.getTodos(),
            inputText = "",
        )
    }

    fun toggleTodo(id: Int) {
        repository.toggleTodo(id)
        state = state.copy(todos = repository.getTodos())
    }

    fun deleteTodo(id: Int) {
        repository.deleteTodo(id)
        state = state.copy(todos = repository.getTodos())
    }

    fun activeCount(): Int = state.todos.count { !it.isDone }
}
