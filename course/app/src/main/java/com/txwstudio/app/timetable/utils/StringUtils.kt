package com.txwstudio.app.timetable.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object StringUtils {

    /**
     * @param time
     * @return e.g. AM 04:20
     */
    fun getHumanReadableTimeFormat(time: String): String {
        val formatter = SimpleDateFormat("a hh:mm", Locale.getDefault())
        val cal = Calendar.getInstance()

        val temp = StringBuilder().append(time)
        val h = temp.substring(0, 2)
        val m = temp.substring(2, 4)

        cal[Calendar.HOUR_OF_DAY] = h.toInt()
        cal[Calendar.MINUTE] = m.toInt()
        cal.isLenient = false

        return formatter.format(cal.time)
    }
}