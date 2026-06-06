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
        isDarkMode = preferences.getBoolean(KEY_DARK_MODE, false),
        primaryColorArgb = preferences.getInt(KEY_PRIMARY_COLOR, 0xFF6750A4.toInt()),
    )

    override fun saveSettings(settings: ReminderSettings) {
        preferences.edit()
            .putInt(KEY_HOUR, settings.hour)
            .putInt(KEY_MINUTE, settings.minute)
            .putBoolean(KEY_DARK_MODE, settings.isDarkMode)
            .putInt(KEY_PRIMARY_COLOR, settings.primaryColorArgb)
            .apply()
    }

    private companion object {
        const val KEY_HOUR = "hour"
        const val KEY_MINUTE = "minute"
        const val KEY_DARK_MODE = "is_dark_mode"
        const val KEY_PRIMARY_COLOR = "primary_color"
    }
}
