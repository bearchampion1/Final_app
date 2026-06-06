package tw.edu.example.aitodostarter.ui

import tw.edu.example.aitodostarter.data.TodoItem
import tw.edu.example.aitodostarter.data.TodoRepository

data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val filteredTodos: List<TodoItem> = emptyList(),
    val inputText: String = "",
    val searchQuery: String = "",
)

class TodoController(
    private val repository: TodoRepository,
) {
    var state: TodoUiState = TodoUiState(
        todos = repository.getTodos(),
        filteredTodos = repository.getTodos()
    )
        private set

    fun updateInput(text: String) {
        state = state.copy(inputText = text)
    }

    fun search() {
        val query = state.inputText
        val allTodos = repository.getTodos()
        val filtered = if (query.isBlank()) {
            allTodos
        } else {
            allTodos.filter { it.title.contains(query, ignoreCase = true) }
        }
        state = state.copy(
            todos = allTodos,
            filteredTodos = filtered,
            searchQuery = query
        )
    }

    fun addTodo() {
        if (state.inputText.isBlank()) {
            return
        }

        repository.addTodo(state.inputText)
        val allTodos = repository.getTodos()
        state = state.copy(
            todos = allTodos,
            filteredTodos = filterList(allTodos, state.searchQuery),
            inputText = "",
        )
    }

    fun toggleTodo(id: Int) {
        repository.toggleTodo(id)
        val allTodos = repository.getTodos()
        state = state.copy(
            todos = allTodos,
            filteredTodos = filterList(allTodos, state.searchQuery)
        )
    }

    fun deleteTodo(id: Int) {
        repository.deleteTodo(id)
        val allTodos = repository.getTodos()
        state = state.copy(
            todos = allTodos,
            filteredTodos = filterList(allTodos, state.searchQuery)
        )
    }

    private fun filterList(list: List<TodoItem>, query: String): List<TodoItem> {
        return if (query.isBlank()) {
            list
        } else {
            list.filter { it.title.contains(query, ignoreCase = true) }
        }
    }

    fun activeCount(): Int = state.todos.count { !it.isDone }
}
