package tw.edu.example.aitodostarter.data

import android.content.Context

interface ReminderSettingsRepository {
    fun getSettings(): ReminderSettings
    fun saveSettings(settings: ReminderSettings)
}

class SharedPreferencesReminderSettingsRepository(
    context: Context,
) : ReminderSettingsRepository {
    private val preferences = context.getSharedPreferences(
        "reminder_settings",
        Context.MODE_PRIVATE,
    )

    override fun getSettings(): ReminderSettings = ReminderSettings(
        hour = preferences.getInt(KEY_HOUR, 9),
        minute = preferences.getInt(KEY_MINUTE, 0),
    )

    override fun saveSettings(settings: ReminderSettings) {
        preferences.edit()
            .putInt(KEY_HOUR, settings.hour)
            .putInt(KEY_MINUTE, settings.minute)
            .apply()
    }

    private companion object {
        const val KEY_HOUR = "hour"
        const val KEY_MINUTE = "minute"
    }
}