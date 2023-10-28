package com.txwstudio.app.timetable.adapter

import com.google.android.material.textfield.TextInputLayout
import com.txwstudio.app.timetable.R

object CourseEditorBindingAdapter {
    @JvmStatic
    //@BindingAdapter("isError")
    fun setErrorMsg(view: TextInputLayout, isError: Boolean) {
        if (isError) {
            view.isErrorEnabled = true
            view.error = view.context.getString(R.string.courseEditor_noEntry)
        } else {
            view.isErrorEnabled = false
        }
    }
}