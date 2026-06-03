package tw.edu.example.aitodostarter

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import tw.edu.example.aitodostarter.data.InMemoryTodoRepository
import tw.edu.example.aitodostarter.data.SharedPreferencesReminderSettingsRepository
import tw.edu.example.aitodostarter.reminder.ReminderScheduler
import tw.edu.example.aitodostarter.ui.ReminderSettingsController
import tw.edu.example.aitodostarter.ui.TodoApp
import tw.edu.example.aitodostarter.ui.TodoController

class MainActivity : ComponentActivity() {
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val todoController = TodoController(InMemoryTodoRepository())
        val reminderScheduler = ReminderScheduler(applicationContext)
        val reminderSettingsController = ReminderSettingsController(
            repository = SharedPreferencesReminderSettingsRepository(applicationContext),
            scheduler = reminderScheduler,
        )

        reminderScheduler.createNotificationChannel()
        requestNotificationPermissionIfNeeded()
        reminderScheduler.scheduleDailyReminder(reminderSettingsController.state.settings)

        setContent {
            TodoApp(
                todoController = todoController,
                reminderSettingsController = reminderSettingsController,
            )
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}