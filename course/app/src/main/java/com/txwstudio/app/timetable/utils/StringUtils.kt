package com.txwstudio.app.timetable.utils

import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object StringUtils {

    /**
     * Format time string from database to human friendly format.
     *
     * @param context Context
     * @param time 4 digits time without semicolon, e.g. "1300"
     * @return time string in 12H or 24H format based on user's device setting,
     * e.g. "13:00", "01:00 PM"
     */
    fun getHumanReadableTimeFormat(context: Context, time: String): String {
        val formatter = if (DateFormat.is24HourFormat(context)) {
            // Use 24-hour format
            SimpleDateFormat("HH:mm", Locale.getDefault())
        } else {
            // Use 12-hour format with AM/PM
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, time.substring(0, 2).toInt())
            set(Calendar.MINUTE, time.substring(2, 4).toInt())
            isLenient = false // Avoid parsing errors with invalid dates
        }

        return formatter.format(calendar.time)
    }
}