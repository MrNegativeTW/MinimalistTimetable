package com.txwstudio.app.timetable.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.txwstudio.app.timetable.R

object CourseCardBindingAdapter {
    @JvmStatic
    @BindingAdapter("isTeacherColEnable")
    fun setErrorMsg(view: TextView, isTeacherColEnable: Boolean) {
        if (isTeacherColEnable) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}