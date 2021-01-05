package com.txwstudio.app.timetable.ui.courseeditor

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CourseEditorViewModel : ViewModel() {

    var courseName = MutableLiveData<String>()
    var coursePlace = MutableLiveData<String>()
    var courseBeginTime = MutableLiveData<String>()
    var courseEndTime = MutableLiveData<String>()
    var courseWeekday = MutableLiveData<String>()
    var courseTeacher = MutableLiveData<String>()

    var courseNameError = MutableLiveData<Boolean>()
    var coursePlaceError = MutableLiveData<Boolean>()
    var courseBeginTimeError = MutableLiveData<Boolean>()
    var courseEndTimeError = MutableLiveData<Boolean>()
    var courseWeekdayError = MutableLiveData<Boolean>()
    var courseTeacherError = MutableLiveData<Boolean>()

    var openTimePicker = MutableLiveData<Boolean>(false)
    var beginOrAnd = MutableLiveData<Int>()

    /**
     * Open timepicker when clicked, 0 for begin time, 1 for end time.
     * */
    fun selectBeginOrEndTime(beginOrAnd: Int) {
        openTimePicker.value = !openTimePicker.value!!
        this.beginOrAnd.value = beginOrAnd
    }

    /**
     * Check required entries are not empty.
     *
     * @return true, if EMPTY
     * @return false, if NOT EMPTY
     * */
    private fun isRequiredEntriesEmpty(): Boolean {
        return courseName.value.isNullOrEmpty() ||
                coursePlace.value.isNullOrBlank() ||
                courseBeginTime.value.isNullOrEmpty() ||
                courseEndTime.value.isNullOrEmpty()
    }

    private fun markEmptyEntries() {
        courseNameError.value = courseName.value.isNullOrEmpty()
        coursePlaceError.value = coursePlace.value.isNullOrEmpty()
        courseBeginTimeError.value = courseBeginTime.value.isNullOrEmpty()
        courseEndTimeError.value = courseEndTime.value.isNullOrEmpty()
    }

    /**
     * User clicked save, start process it.
     * */
    fun saveFired() {
        if (isRequiredEntriesEmpty()) {
            markEmptyEntries()
            return
        } else {
            markEmptyEntries()
        }

        Log.i("TESTTT", "${courseName.value} | ${coursePlace.value} | " +
                "${courseWeekday.value} | ${courseBeginTime.value} | ${courseEndTime.value}")
    }
}