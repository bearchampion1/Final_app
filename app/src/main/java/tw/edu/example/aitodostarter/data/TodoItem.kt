package tw.edu.example.aitodostarter.data

data class TodoItem(
    val id: Int,
    val title: String,
    val isDone: Boolean = false,
)
