package com.txwstudio.app.timetable.adapter

import android.view.View
import android.widget.TextView

object CourseCardBindingAdapter {
    @JvmStatic
    //@BindingAdapter("isTeacherColEnable")
    fun setErrorMsg(view: TextView, isTeacherColEnable: Boolean) {
        if (isTeacherColEnable) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }
}