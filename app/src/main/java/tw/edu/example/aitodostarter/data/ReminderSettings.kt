package tw.edu.example.aitodostarter.data

import java.util.Locale

data class ReminderSettings(
    val hour: Int = 9,
    val minute: Int = 0,
) {
    fun formattedTime(): String = String.format(Locale.US, "%02d:%02d", hour, minute)
}