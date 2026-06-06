package tw.edu.example.aitodostarter.ui

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tw.edu.example.aitodostarter.data.ReminderSettings
import tw.edu.example.aitodostarter.data.ReminderSettingsRepository
import tw.edu.example.aitodostarter.data.TodoItem
import tw.edu.example.aitodostarter.data.TodoRepository
import tw.edu.example.aitodostarter.reminder.ReminderScheduler

/**
 * 定義 App 中的頁面狀態 (Requirement 4)
 */
enum class AppPage {
    Todos, // 代辦事項主清單
    Settings, // 設定頁面
    Instructions, // 使用說明頁面
}

/**
 * App 的主入口 Composable
 */
@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(
    todoController: TodoController,
    reminderSettingsController: ReminderSettingsController,
) {
    // 使用 remember 監聽控制器中的狀態
    var todoState by remember { mutableStateOf(todoController.state) }
    var reminderSettingsState by remember {
        mutableStateOf(reminderSettingsController.state)
    }

    // 動態判斷配色方案 (主題色與深色模式切換)
    val primaryColor = Color(reminderSettingsState.settings.primaryColorArgb)
    val colorScheme = if (reminderSettingsState.settings.isDarkMode) {
        darkColorScheme(primary = primaryColor)
    } else {
        lightColorScheme(primary = primaryColor)
    }

    MaterialTheme(colorScheme = colorScheme) {
        // 使用 rememberSaveable 確保旋轉手機時停留在當前頁面
        var page by rememberSaveable { mutableStateOf(AppPage.Todos) }

        Scaffold(
            topBar = {
                // 根據當前頁面顯示不同的頂部欄
                when (page) {
                    AppPage.Todos -> TodoTopAppBar(
                        onSettingsClick = { page = AppPage.Settings },
                    )
                    AppPage.Settings -> SettingsTopAppBar(
                        onBackClick = { page = AppPage.Todos },
                    )
                    AppPage.Instructions -> InstructionsTopAppBar(
                        onBackClick = { page = AppPage.Settings },
                    )
                }
            },
            floatingActionButton = {
                // 僅在代辦事項頁面顯示新增按鈕 (Requirement 4)
                if (page == AppPage.Todos) {
                    FloatingActionButton(onClick = {
                        todoController.addTodo()
                        todoState = todoController.state
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add todo")
                    }
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background,
            ) {
                when (page) {
                    AppPage.Todos -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            TodoScreen(
                                state = todoState,
                                isDarkMode = reminderSettingsState.settings.isDarkMode,
                                activeCount = todoController.activeCount(),
                                onInputChange = {
                                    todoController.updateInput(it)
                                    todoState = todoController.state
                                },
                                onUrgentToggle = {
                                    todoController.toggleUrgentInput()
                                    todoState = todoController.state
                                },
                                onClearUrgentFilter = {
                                    todoController.setShowUrgentOnly(false)
                                    todoState = todoController.state
                                },
                                onSearchClick = {
                                    todoController.search()
                                    todoState = todoController.state
                                },
                                onToggleClick = {
                                    todoController.toggleTodo(it)
                                    todoState = todoController.state
                                },
                                onDeleteClick = {
                                    todoController.deleteTodo(it)
                                    todoState = todoController.state
                                },
                            )
                        }
                    }

                    AppPage.Settings -> ReminderSettingsScreen(
                        state = reminderSettingsState,
                        onTimeSelected = { hour, minute ->
                            reminderSettingsController.updateReminderTime(hour, minute)
                            reminderSettingsState = reminderSettingsController.state
                        },
                        onDarkModeToggle = {
                            reminderSettingsController.toggleDarkMode()
                            reminderSettingsState = reminderSettingsController.state
                        },
                        onColorSelected = { colorArgb ->
                            reminderSettingsController.updatePrimaryColor(colorArgb)
                            reminderSettingsState = reminderSettingsController.state
                        },
                        onInstructionsClick = {
                            page = AppPage.Instructions
                        },
                        onUrgentFilterClick = {
                            todoController.setShowUrgentOnly(true)
                            todoState = todoController.state
                            page = AppPage.Todos
                        },
                        onTestClick = {
                            reminderSettingsController.testReminder()
                        },
                    )

                    AppPage.Instructions -> InstructionsScreen()
                }
            }
        }
    }
}

/**
 * 主畫面的頂部欄
 */
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

/**
 * 設定頁面的頂部欄
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("設定", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to todo list",
                )
            }
        },
    )
}

/**
 * 使用說明頁面的頂部欄
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructionsTopAppBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text("使用說明", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to settings",
                )
            }
        },
    )
}

/**
 * 代辦事項清單畫面
 */
@Composable
fun TodoScreen(
    state: TodoUiState,
    isDarkMode: Boolean,
    activeCount: Int,
    onInputChange: (String) -> Unit,
    onUrgentToggle: () -> Unit,
    onClearUrgentFilter: () -> Unit,
    onSearchClick: () -> Unit,
    onToggleClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 顯示事項計數或模式提示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (state.showUrgentOnly) "Urgent Items" else "$activeCount active item(s)",
                style = MaterialTheme.typography.bodyMedium,
                color = if (state.showUrgentOnly) Color.Red else MaterialTheme.colorScheme.onSurface
            )

            // 如果處於緊急項目過濾模式，顯示恢復按鈕
            if (state.showUrgentOnly) {
                Button(
                    onClick = onClearUrgentFilter,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Show All", color = Color.White)
                }
            }
        }

        // 輸入與搜尋列 (Requirement 4)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = state.inputText,
                onValueChange = onInputChange,
                label = { Text("New todo or search") },
                singleLine = true,
                leadingIcon = {
                    // 切換緊急狀態的小圖示
                    IconButton(onClick = onUrgentToggle) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Mark as Urgent",
                            tint = if (state.isUrgentInput) Color.Red else Color.Gray
                        )
                    }
                }
            )
            // 搜尋按鈕，深色模式文字設為白色
            Button(
                onClick = onSearchClick,
                colors = ButtonDefaults.buttonColors(
                    contentColor = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Search")
            }
        }

        HorizontalDivider()

        // 顯示清單內容 (Requirement 4: 顯示篩選後的 filteredTodos)
        if (state.filteredTodos.isEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(if (state.searchQuery.isBlank()) "No todos yet" else "No matching todos")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.filteredTodos, key = { it.id }) { todo ->
                    TodoRow(
                        todo = todo,
                        onToggleClick = onToggleClick,
                        onDeleteClick = onDeleteClick,
                    )
                }
            }
        }
    }
}

/**
 * 優化後的清單式設定畫面
 */
@Composable
fun ReminderSettingsScreen(
    state: ReminderSettingsUiState,
    onTimeSelected: (Int, Int) -> Unit,
    onDarkModeToggle: () -> Unit,
    onColorSelected: (Int) -> Unit,
    onInstructionsClick: () -> Unit,
    onUrgentFilterClick: () -> Unit,
    onTestClick: () -> Unit,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        // --- 一、帳號與個人資料 ---
        item { SettingHeader("帳號與個人資料") }
        item { SettingsListItem(icon = Icons.Default.Person, text = "顯示名稱", onClick = {}) }
        item { SettingsListItem(icon = Icons.Default.Email, text = "電子郵件", onClick = {}) }
        item { SettingsListItem(icon = Icons.Default.Lock, text = "密碼與安全", onClick = {}) }
        item {
            SettingsListItem(
                icon = Icons.Default.AccountCircle,
                text = "使用 Google 建立帳號",
                onClick = { /* 實作 Google 登入邏輯 */ },
                textColor = Color(0xFF4285F4)
            )
        }
        item {
            SettingsListItem(
                icon = Icons.Default.Email,
                text = "使用 Email 建立帳號",
                onClick = { /* 實作 Email 註冊邏輯 */ }
            )
        }

        // --- 二、通知設定 ---
        item { SettingHeader("通知設定") }
        item {
            SettingsListItem(
                icon = Icons.Default.Notifications,
                text = "每日提醒時間 (${state.settings.formattedTime()})",
                onClick = {
                    TimePickerDialog(
                        context,
                        { _, hour, minute -> onTimeSelected(hour, minute) },
                        state.settings.hour,
                        state.settings.minute,
                        true,
                    ).show()
                }
            )
        }
        item { SettingsListItem(icon = Icons.Default.PlayArrow, text = "立即測試通知", onClick = onTestClick) }

        // --- 三、Todo / 排程事件設定 ---
        item { SettingHeader("Todo / 排程事件設定") }
        // 將「已說讚」改為「緊急」(Requirement 4 調整)
        item { SettingsListItem(icon = Icons.Default.Warning, text = "緊急", onClick = onUrgentFilterClick, textColor = Color.Red) }
        item { SettingsListItem(icon = Icons.Default.Star, text = "我的珍藏", onClick = {}) }

        // --- 四、顯示與外觀 ---
        item { SettingHeader("顯示與外觀") }
        item {
            SettingsListItem(
                icon = Icons.Default.Settings,
                text = "主題色 (目前顏色)",
                onClick = {
                    val colors = listOf(
                        0xFF6750A4.toInt(), 0xFFB00020.toInt(), 0xFF1B5E20.toInt(),
                        0xFF01579B.toInt(), 0xFFFF6F00.toInt()
                    )
                    val currentIndex = colors.indexOf(state.settings.primaryColorArgb)
                    val nextIndex = (currentIndex + 1) % colors.size
                    onColorSelected(colors[nextIndex])
                }
            )
        }
        item {
            SettingsListItem(
                icon = Icons.Default.Build,
                text = "深色模式 (${if (state.settings.isDarkMode) "開啟" else "關閉"})",
                onClick = onDarkModeToggle
            )
        }

        // --- 七、說明與支援 ---
        item { SettingHeader("說明與支援") }
        item { SettingsListItem(icon = Icons.Default.Info, text = "使用說明", onClick = onInstructionsClick) }
        item { SettingsListItem(icon = Icons.Default.Info, text = "關於", onClick = {}) }

        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray
            )
        }

        item {
            Text(
                text = "切換個人檔案",
                color = Color(0xFF0095F6),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
        item {
            Text(
                text = "登出",
                color = Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

/**
 * 設定區塊標題
 */
@Composable
fun SettingHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Color.Gray,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
    )
}

/**
 * 使用說明畫面 (Requirement 4 指導頁面)
 */
@Composable
fun InstructionsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "歡迎使用 AiTodoStarter！",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item {
            InstructionSection(
                title = "1. 新增代辦事項",
                description = "點擊畫面右下角的「+」懸浮按鈕。系統會自動讀取上方輸入框的文字內容，並將其新增至清單中。"
            )
        }
        item {
            InstructionSection(
                title = "2. 搜尋與篩選",
                description = "在輸入框輸入關鍵字後，點擊旁邊的「Search」按鈕。清單將即時篩選出包含該文字的項目。"
            )
        }
        item {
            InstructionSection(
                title = "3. 標記完成與刪除",
                description = "點擊項目右側的選單圖示（三個點），您可以選擇「Done」標記完成或「Delete」刪除項目。"
            )
        }
        item {
            InstructionSection(
                title = "4. 個人化設定",
                description = "進入「設定」頁面可調整提醒時間、切換深色模式以及更改主題配色。"
            )
        }
    }
}

/**
 * 使用說明的小區塊
 */
@Composable
fun InstructionSection(title: String, description: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 通用的設定清單項目
 */
@Composable
fun SettingsListItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )
    }
}

/**
 * 主畫面的功能選單 (右上角)
 */
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

/**
 * 單一代辦事項列
 */
@Composable
fun TodoRow(
    todo: TodoItem,
    onToggleClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 如果是緊急項目，顯示紅色標誌
        if (todo.isUrgent) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Urgent",
                tint = Color.Red,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = todo.title,
            textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
        )
        TodoItemMenu(
            isDone = todo.isDone,
            onDoneClick = { onToggleClick(todo.id) },
            onUndoneClick = { onToggleClick(todo.id) },
            onDeleteClick = { onDeleteClick(todo.id) },
        )
    }
}

/**
 * 代辦事項的互動選單 (Requirement 2 & 3)
 */
@Composable
fun TodoItemMenu(
    isDone: Boolean,
    onDoneClick: () -> Unit,
    onUndoneClick: () -> Unit,
    onDeleteClick: () -> Unit,
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
        DropdownMenuItem(
            text = { Text("Delete") },
            onClick = {
                expanded = false
                onDeleteClick()
            },
        )
    }
}

/**
 * 預覽功能倉庫
 */
private class PreviewTodoRepository : TodoRepository {
    override fun getTodos(): List<TodoItem> = listOf(
        TodoItem(id = 1, title = "Read the starter code"),
        TodoItem(id = 2, title = "Ask AI to generate SPEC.md"),
        TodoItem(id = 3, title = "Demo one maintenance request", isUrgent = true),
    )
    override fun getUrgentTodos(): List<TodoItem> = getTodos().filter { it.isUrgent }
    override fun addTodo(title: String, isUrgent: Boolean): TodoItem = TodoItem(id = 4, title = title, isUrgent = isUrgent)
    override fun toggleTodo(id: Int) {}
    override fun deleteTodo(id: Int) {}
}

/**
 * 預覽設定倉庫
 */
private class PreviewReminderSettingsRepository : ReminderSettingsRepository {
    private var settings = ReminderSettings()

    override fun getSettings(): ReminderSettings = settings

    override fun saveSettings(settings: ReminderSettings) {
        this.settings = settings
    }
}
