package com.txwstudio.app.timetable.ui.courseeditor

import android.util.Log
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.txwstudio.app.timetable.MyApplication
import com.txwstudio.app.timetable.data.Course3
import com.txwstudio.app.timetable.data.CourseRepository
import com.txwstudio.app.timetable.utilities.INTENT_EXTRA_COURSE_ID_DEFAULT_VALUE
import com.txwstudio.app.timetable.utilities.logI
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * As you can see, this is a ViewModel.
 *
 * @param repository CourseRepository in this project.
 * @param courseId Default value is -1, passing course id to get into edit mode.
 * @param currentViewPagerItem Default value is 0, which is Monday.
 *                             Passing current viewpager item to automatic set weekday.
 */
class CourseEditorViewModel(
    private val repository: CourseRepository,
    private val courseId: Int,
    private val currentViewPagerItem: Int
) : ViewModel() {

    private val _isEditMode = MutableLiveData<Boolean>()
    val isEditMode: LiveData<Boolean>
        get() = _isEditMode

    private var courseIdFromDatabase = MutableLiveData<Int>(null)
    var courseName = MutableLiveData<String>()
    var coursePlace = MutableLiveData<String>()
    var courseProf = MutableLiveData<String?>()
    var courseBeginTime = MutableLiveData<String>()
    var courseEndTime = MutableLiveData<String>()
    var courseWeekday = MutableLiveData<Int>(0)

    // [START] Error
    private val _isCourseNameError = MutableLiveData<Boolean>()
    val isCourseNameError: LiveData<Boolean>
        get() = _isCourseNameError

    private val _isCoursePlaceError = MutableLiveData<Boolean>()
    val isCoursePlaceError: LiveData<Boolean>
        get() = _isCoursePlaceError

    private val _isCourseBeginTimeError = MutableLiveData<Boolean>()
    val isCourseBeginTimeError: LiveData<Boolean>
        get() = _isCourseBeginTimeError

    private val _isCourseEndTimeError = MutableLiveData<Boolean>()
    val isCourseEndTimeError: LiveData<Boolean>
        get() = _isCourseEndTimeError
    // [END] Error

    var openTimePicker = MutableLiveData<Boolean>(false)
    var pickBeginOrEnd = MutableLiveData<Int>()
    var courseBeginTimeForEdit = MutableLiveData<Int>()
    var courseEndTimeForEdit = MutableLiveData<Int>()

    var isSavedSuccessfully = MutableLiveData<Boolean>(false)

    init {
        if (courseId != INTENT_EXTRA_COURSE_ID_DEFAULT_VALUE) {
            // Edit mode, if courseId is provided.
            _isEditMode.value = true
            setupValueForEditing()
        } else {
            // Add mode, no course id is provided.
            courseWeekday.value = currentViewPagerItem
        }
    }

    /**
     * If start in edit mode, get course information from database, then set to screen.
     * */
    private fun setupValueForEditing() {
        viewModelScope.launch {
            val courseInfo = repository.getCourseById(courseId)
            courseIdFromDatabase.postValue(courseInfo.id!!)
            courseName.postValue(courseInfo.courseName!!)
            coursePlace.postValue(courseInfo.coursePlace!!)
            courseProf.postValue(courseInfo.profName)
            courseWeekday.postValue(courseInfo.courseWeekday!!)
            courseBeginTime.postValue(courseInfo.courseStartTime!!)
            courseEndTime.postValue(courseInfo.courseEndTime!!)
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
            TAG, "${courseName.value} | ${coursePlace.value} | " +
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
        val course = Course3(
            id = courseIdFromDatabase.value,
            courseName = courseName.value,
            coursePlace = coursePlace.value,
            courseWeekday = courseWeekday.value,
            courseStartTime = courseBeginTime.value,
            courseEndTime = courseEndTime.value,
            profName = courseProf.value
        )

        // Done
        if (_isEditMode.value != true) {
            // Add Mode
            viewModelScope.launch {
                try {
                    repository.insertCourse(course)
                    isSavedSuccessfully.value = true
                } catch (e: Exception) {
                    Log.i(TAG, "insert failed")
                }
            }
        } else {
            // Edit Mode
            viewModelScope.launch {
                try {
                    repository.updateCourse(course)
                    isSavedSuccessfully.value = true
                } catch (e: Exception) {
                    Log.i(TAG, "update failed")
                }
            }
        }
    }

    companion object {
        private const val TAG = "CourseEditorViewModel"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()

                val courseRepository = (this[APPLICATION_KEY] as MyApplication).courseRepository
                val courseId = this[DEFAULT_ARGS_KEY]?.getInt("courseId")
                val currentViewPagerItem = this[DEFAULT_ARGS_KEY]?.getInt("currentViewPagerItem")
                logI(TAG, "courseId: $courseId, currentViewPagerItem: $currentViewPagerItem")

                CourseEditorViewModel(
                    repository = courseRepository,
                    courseId = courseId!!,
                    currentViewPagerItem = currentViewPagerItem!!
                )
            }
        }
    }
}