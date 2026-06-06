package tw.edu.example.aitodostarter.data

class RoomTodoRepository(private val todoDao: TodoDao) : TodoRepository {
    override fun getTodos(): List<TodoItem> {
        return todoDao.getAll()
    }

    override fun addTodo(title: String): TodoItem {
        val todo = TodoItem(title = title)
        val id = todoDao.insert(todo)
        return todo.copy(id = id.toInt())
    }

    override fun toggleTodo(id: Int) {
        val todo = todoDao.getById(id)
        if (todo != null) {
            todoDao.update(todo.copy(isDone = !todo.isDone))
        }
    }

    override fun deleteTodo(id: Int) {
        todoDao.deleteById(id)
    }
}
