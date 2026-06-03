package tw.edu.example.aitodostarter.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tw.edu.example.aitodostarter.data.InMemoryTodoRepository
import tw.edu.example.aitodostarter.data.ReminderSettings
import tw.edu.example.aitodostarter.data.ReminderSettingsRepository
import tw.edu.example.aitodostarter.data.TodoItem
import tw.edu.example.aitodostarter.reminder.ReminderScheduler

enum class AppPage {
    Todos,
    Settings,
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(
    todoController: TodoController,
    reminderSettingsController: ReminderSettingsController,
) {
    MaterialTheme {
        var page by remember { mutableStateOf(AppPage.Todos) }
        var todoState by remember { mutableStateOf(todoController.state) }
        var reminderSettingsState by remember {
            mutableStateOf(reminderSettingsController.state)
        }

        Scaffold(
            topBar = {
                when (page) {
                    AppPage.Todos -> TodoTopAppBar(
                        onSettingsClick = { page = AppPage.Settings },
                    )

                    AppPage.Settings -> SettingsTopAppBar(
                        onBackClick = { page = AppPage.Todos },
                    )
                }
            },
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    when (page) {
                        AppPage.Todos -> TodoScreen(
                            state = todoState,
                            activeCount = todoController.activeCount(),
                            onInputChange = {
                                todoController.updateInput(it)
                                todoState = todoController.state
                            },
                            onAddClick = {
                                todoController.addTodo()
                                todoState = todoController.state
                            },
                            onToggleClick = {
                                todoController.toggleTodo(it)
                                todoState = todoController.state
                            },
                        )

                        AppPage.Settings -> ReminderSettingsScreen(
                            state = reminderSettingsState,
                            onTimeSelected = { hour, minute ->
                                reminderSettingsController.updateReminderTime(hour, minute)
                                reminderSettingsState = reminderSettingsController.state
                            },
                            onTestClick = {
                                reminderSettingsController.testReminder()
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTopAppBar(onSettingsClick: () -> Unit) {
    TopAppBar(
        title = { Text("Todo List") },
        actions = {
            TodoAppMenu(onSettingsClick = onSettingsClick)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("Reminder Settings") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back to todo list",
                )
            }
        },
    )
}

@Composable
fun TodoScreen(
    state: TodoUiState,
    activeCount: Int,
    onInputChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onToggleClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "$activeCount active item(s)",
            style = MaterialTheme.typography.bodyMedium,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = state.inputText,
                onValueChange = onInputChange,
                label = { Text("New todo") },
                singleLine = true,
            )
            Button(onClick = onAddClick) {
                Text("Add")
            }
        }

        Divider()

        if (state.todos.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("No todos yet")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.todos, key = { it.id }) { todo ->
                    TodoRow(
                        todo = todo,
                        onToggleClick = onToggleClick,
                    )
                }
            }
        }
    }
}

@Composable
fun TodoAppMenu(onSettingsClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "Open menu",
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        DropdownMenuItem(
            text = { Text("Settings") },
            onClick = {
                expanded = false
                onSettingsClick()
            },
        )
    }
}

@Composable
fun ReminderSettingsScreen(
    state: ReminderSettingsUiState,
    onTimeSelected: (Int, Int) -> Unit,
    onTestClick: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Daily reminder time: ${state.settings.formattedTime()}",
            style = MaterialTheme.typography.bodyLarge,
        )

        Button(
            onClick = {
                TimePickerDialog(
                    context,
                    { _, hour, minute -> onTimeSelected(hour, minute) },
                    state.settings.hour,
                    state.settings.minute,
                    true,
                ).show()
            },
        ) {
            Text("Choose Time")
        }

        Button(onClick = onTestClick) {
            Text("Test Notification Now")
        }
    }
}

@Composable
fun TodoRow(
    todo: TodoItem,
    onToggleClick: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = todo.title,
            textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
        )
        TodoItemMenu(
            isDone = todo.isDone,
            onDoneClick = { onToggleClick(todo.id) },
            onUndoneClick = { onToggleClick(todo.id) },
        )
    }
}

@Composable
fun TodoItemMenu(
    isDone: Boolean,
    onDoneClick: () -> Unit,
    onUndoneClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "Open todo menu",
        )
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        if (isDone) {
            DropdownMenuItem(
                text = { Text("Undone") },
                onClick = {
                    expanded = false
                    onUndoneClick()
                },
            )
        } else {
            DropdownMenuItem(
                text = { Text("Done") },
                onClick = {
                    expanded = false
                    onDoneClick()
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoScreenPreview() {
    TodoApp(
        todoController = TodoController(InMemoryTodoRepository()),
        reminderSettingsController = ReminderSettingsController(
            repository = PreviewReminderSettingsRepository(),
            scheduler = ReminderScheduler(LocalContext.current),
        ),
    )
}

private class PreviewReminderSettingsRepository : ReminderSettingsRepository {
    private var settings = ReminderSettings()

    override fun getSettings(): ReminderSettings = settings

    override fun saveSettings(settings: ReminderSettings) {
        this.settings = settings
    }
}