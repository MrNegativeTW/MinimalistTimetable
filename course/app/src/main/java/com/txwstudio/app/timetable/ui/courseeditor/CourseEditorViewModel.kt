package com.txwstudio.app.timetable.ui.courseeditor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.txwstudio.app.timetable.DBHandler
import com.txwstudio.app.timetable.model.Course
import com.txwstudio.app.timetable.model.Course2

class CourseEditorViewModel(application: Application) : AndroidViewModel(application) {

    var courseName = MutableLiveData<String>()
    var coursePlace = MutableLiveData<String>()
    var courseBeginTime = MutableLiveData<String>()
    var courseEndTime = MutableLiveData<String>()
    var courseWeekday = MutableLiveData<Int>(0)
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
                "${courseBeginTime.value} | ${courseEndTime.value} | ${courseWeekday.value}")

        val course = Course()
        course.courseName = courseName.value
        course.coursePlace = coursePlace.value
        course.courseStartTime = courseBeginTime.value
        course.courseEndTime = courseEndTime.value
        course.courseWeekday = courseWeekday.value!!

        val course2 = Course2(
                courseName = courseName.value,
                coursePlace = coursePlace.value,
                courseBeginTime = courseBeginTime.value,
                courseEndTime = courseEndTime.value,
                courseWeekday = courseWeekday.value)
        DBHandler(getApplication()).addCourse(course)
    }
}