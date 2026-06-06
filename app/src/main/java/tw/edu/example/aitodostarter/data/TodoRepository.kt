package tw.edu.example.aitodostarter.data

interface TodoRepository {
    fun getTodos(): List<TodoItem>
    fun addTodo(title: String): TodoItem
    fun toggleTodo(id: Int)
    fun deleteTodo(id: Int)
}
