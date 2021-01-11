package com.txwstudio.app.timetable.ui.timepicker

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.text.format.DateFormat
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment() {
    //  implements TimePickerDialog.OnTimeSetListener
    // https://www.youtube.com/watch?v=QMwaNN_aM3U

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c[Calendar.HOUR_OF_DAY]
        val min = c[Calendar.MINUTE]
        return TimePickerDialog(activity,
                activity as OnTimeSetListener?,
                hour, min, DateFormat.is24HourFormat(activity))
    }
}