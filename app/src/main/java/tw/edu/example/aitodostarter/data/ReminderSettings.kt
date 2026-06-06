package tw.edu.example.aitodostarter.data

import java.util.Locale

data class ReminderSettings(
    val hour: Int = 9,
    val minute: Int = 0,
    val isDarkMode: Boolean = false,
    val primaryColorArgb: Int = 0xFF6750A4.toInt(), // Default Material3 baseline primary
) {
    fun formattedTime(): String = String.format(Locale.US, "%02d:%02d", hour, minute)
}
