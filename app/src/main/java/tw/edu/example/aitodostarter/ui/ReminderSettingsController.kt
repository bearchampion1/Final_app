package tw.edu.example.aitodostarter.ui

import android.Manifest
import androidx.annotation.RequiresPermission
import tw.edu.example.aitodostarter.data.ReminderSettings
import tw.edu.example.aitodostarter.data.ReminderSettingsRepository
import tw.edu.example.aitodostarter.reminder.ReminderScheduler

data class ReminderSettingsUiState(
    val settings: ReminderSettings = ReminderSettings(),
)

class ReminderSettingsController(
    private val repository: ReminderSettingsRepository,
    private val scheduler: ReminderScheduler,
) {
    var state: ReminderSettingsUiState = ReminderSettingsUiState(repository.getSettings())
        private set

    fun updateReminderTime(hour: Int, minute: Int) {
        val settings = ReminderSettings(hour = hour, minute = minute)
        repository.saveSettings(settings)
        scheduler.scheduleDailyReminder(settings)
        state = state.copy(settings = settings)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun testReminder() {
        scheduler.showTestNotification()
    }
}