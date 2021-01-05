package com.txwstudio.app.timetable.ui.courseeditor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CourseEditorViewModel : ViewModel() {

    var courseName = MutableLiveData<String>()
    var coursePlace = MutableLiveData<String>()
    var courseStartTime = MutableLiveData<String>()
    var courseEndTime = MutableLiveData<String>()
    var courseWeekday = MutableLiveData<String>()

    var courseNameError = MutableLiveData<Boolean>()
    var coursePlaceError = MutableLiveData<Boolean>()
    var courseStartTimeError = MutableLiveData<Boolean>()
    var courseEndTimeError = MutableLiveData<Boolean>()
    var courseWeekdayError = MutableLiveData<Boolean>()

    /**
     * Check required entries are not empty.
     *
     * @return true, if EMPTY
     * @return false, if NOT EMPTY
     * */
    private fun isRequiredEntriesEmpty(): Boolean {
        return courseName.value.isNullOrEmpty() ||
                coursePlace.value.isNullOrBlank()
    }

    private fun markEmptyEntries() {
        courseNameError.value = courseName.value.isNullOrEmpty()
        coursePlaceError.value = coursePlace.value.isNullOrEmpty()
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

    }
}