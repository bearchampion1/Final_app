package tw.edu.example.aitodostarter.data

class InMemoryTodoRepository : TodoRepository {
    private val todos = mutableListOf(
        TodoItem(id = 1, title = "Read the starter code"),
        TodoItem(id = 2, title = "Ask AI to generate SPEC.md"),
        TodoItem(id = 3, title = "Demo one maintenance request"),
    )
    private var nextId = 4

    override fun getTodos(): List<TodoItem> = todos.toList()

    override fun addTodo(title: String): TodoItem {
        val todo = TodoItem(id = nextId, title = title)
        nextId += 1
        todos.add(todo)
        return todo
    }

    override fun toggleTodo(id: Int) {
        val index = todos.indexOfFirst { it.id == id }
        if (index >= 0) {
            val current = todos[index]
            todos[index] = current.copy(isDone = !current.isDone)
        }
    }
}
