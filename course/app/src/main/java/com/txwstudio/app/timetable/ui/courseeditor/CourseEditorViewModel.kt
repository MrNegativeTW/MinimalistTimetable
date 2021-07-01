package com.txwstudio.app.timetable.ui.courseeditor

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.txwstudio.app.timetable.DBHandler
import com.txwstudio.app.timetable.model.Course2
import com.txwstudio.app.timetable.utilities.INTENT_EXTRA_COURSE_ID_DEFAULT_VALUE

class CourseEditorViewModel(
    private val repository: CourseRepository,
    private val courseId: Int,
    private val currentViewPagerItem: Int
) : ViewModel() {

    var isEditMode = MutableLiveData<Boolean>(false)

    var courseName = MutableLiveData<String>()
    var coursePlace = MutableLiveData<String>()
    var courseBeginTime = MutableLiveData<String>()
    var courseEndTime = MutableLiveData<String>()
    var courseWeekday = MutableLiveData<Int>(0)
    var courseProf = MutableLiveData<String>()

    var courseNameError = MutableLiveData<Boolean>()
    var coursePlaceError = MutableLiveData<Boolean>()
    var courseBeginTimeError = MutableLiveData<Boolean>()
    var courseEndTimeError = MutableLiveData<Boolean>()
    var courseWeekdayError = MutableLiveData<Boolean>()
    var courseProfError = MutableLiveData<Boolean>()

    var openTimePicker = MutableLiveData<Boolean>(false)
    var pickBeginOrEnd = MutableLiveData<Int>()
    var courseBeginTimeForEdit = MutableLiveData<Int>()
    var courseEndTimeForEdit = MutableLiveData<Int>()

    var isSaveToFinish = MutableLiveData<Boolean>(false)

    init {
        Log.i("TESTTT", "courseId is: ${courseId}")
        if (courseId != INTENT_EXTRA_COURSE_ID_DEFAULT_VALUE) {
            // If course id is provided, means we are in edit mode.
            isEditMode.value = true
        }
    }
    /**
     * If start in edit mode, get course information from database, then set to screen.
     * */
    fun setupValueForEditing() {
        courseId.value?.let {
            val course = DBHandler(getApplication()).getCourseById2(it)
            courseName.value = course.courseName ?: "-"
            coursePlace.value = course.coursePlace ?: "-"
            courseBeginTime.value = course.courseBeginTime ?: "0000"
            courseEndTime.value = course.courseEndTime ?: "0000"
            courseWeekday.value = course.courseWeekday ?: 1
        }
    }

    /**
     * Open timepicker when clicked, 0 for begin time, 1 for end time.
     * */
    fun selectBeginOrEndTime(beginOrAnd: Int) {
        openTimePicker.value = !openTimePicker.value!!
        this.pickBeginOrEnd.value = beginOrAnd
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


    /**
     * Change error value to true, use to highlight the empty entry.
     * */
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
        Log.i(
            "TESTTT", "${courseName.value} | ${coursePlace.value} | " +
                    " | ${courseProf.value} | ${courseWeekday.value}" +
                    " | ${courseBeginTime.value} | ${courseEndTime.value}"
        )

        // Check entries
        if (isRequiredEntriesEmpty()) {
            markEmptyEntries()
            return
        } else {
            markEmptyEntries()
        }

        // Making cake
        val course2 = Course2(
                courseName = courseName.value,
                coursePlace = coursePlace.value,
                courseBeginTime = courseBeginTime.value,
                courseEndTime = courseEndTime.value,
                courseWeekday = courseWeekday.value)

        // Done
        if (isEditMode.value != true) {
            // Add Mode
            isSaveToFinish.value = DBHandler(getApplication()).addCourse(course2)
        } else {
            // Edit Mode
            isSaveToFinish.value = DBHandler(getApplication()).updateCourse(course2, courseId.value!!)
class CourseEditorViewModelFactory(
    private val repository: CourseRepository,
    private val courseId: Int,
    private val currentViewPagerItem: Int
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseEditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseEditorViewModel(repository, courseId, currentViewPagerItem) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}