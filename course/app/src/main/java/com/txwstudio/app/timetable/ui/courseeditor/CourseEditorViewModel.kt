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
import com.txwstudio.app.timetable.utils.INTENT_EXTRA_COURSE_ID_DEFAULT_VALUE
import com.txwstudio.app.timetable.utils.logI
import kotlinx.coroutines.Dispatchers
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

    // [START] Course information
    private var courseIdFromDatabase = MutableLiveData<Int>(null)
    private val _courseName = MutableLiveData<String>()
    val courseName: LiveData<String>
        get() = _courseName

    private val _coursePlace = MutableLiveData<String>()
    val coursePlace: LiveData<String>
        get() = _coursePlace

    private val _courseProf = MutableLiveData<String>()
    val courseProf: LiveData<String>
        get() = _courseProf

    private val _courseWeekday = MutableLiveData<Int>(0)
    val courseWeekday: LiveData<Int>
        get() = _courseWeekday

    private val _courseBeginTime = MutableLiveData<String>()
    val courseBeginTime: LiveData<String>
        get() = _courseBeginTime

    private val _courseEndTime = MutableLiveData<String>()
    val courseEndTime: LiveData<String>
        get() = _courseEndTime
    // [END] Course information

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

    init {
        if (courseId != INTENT_EXTRA_COURSE_ID_DEFAULT_VALUE) {
            // Edit mode, if courseId is provided.
            _isEditMode.value = true
            setupValueForEditing()
        } else {
            // Add mode, no course id is provided.
            _isEditMode.value = false
            _courseWeekday.value = currentViewPagerItem
            _courseBeginTime.value = "0900"
            _courseEndTime.value = "1200"
        }
    }

    /**
     * If starts in edit mode, get course information from database.
     * */
    private fun setupValueForEditing() {
        viewModelScope.launch(Dispatchers.IO) {
            val courseInfo = repository.getCourseById(courseId)
            courseIdFromDatabase.postValue(courseInfo.id!!)
            _courseName.postValue(courseInfo.courseName!!)
            _coursePlace.postValue(courseInfo.coursePlace!!)
            courseInfo.profName?.let {
                _courseProf.postValue(it)
            }
            _courseWeekday.postValue(courseInfo.courseWeekday!!)
            _courseBeginTime.postValue(courseInfo.courseStartTime!!)
            _courseEndTime.postValue(courseInfo.courseEndTime!!)
        }
    }

    fun submitCourseText(name: String, place: String, prof: String?) {
        _courseName.value = name
        _coursePlace.value = place
        prof?.let {
            _courseProf.value = it
        }
    }

    fun submitCourseWeekday(value: Int) {
        _courseWeekday.value = value
    }

    fun submitCourseBeginTime(value: String) {
        _courseBeginTime.value = value
    }

    fun submitCourseEndTime(value: String) {
        _courseEndTime.value = value
    }

    private val _isSavedSuccessfully = MutableLiveData<Boolean>(false)
    val isSavedSuccessfully: LiveData<Boolean>
        get() = _isSavedSuccessfully

    fun saveToDatabase() {
        Log.i(
            TAG, "${courseName.value} | ${coursePlace.value} | " +
                    " | ${courseProf.value} | ${courseWeekday.value}" +
                    " | ${courseBeginTime.value} | ${courseEndTime.value}"
        )

        if (isRequiredEntriesHasError()) return

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
                    _isSavedSuccessfully.value = true
                } catch (e: Exception) {
                    Log.i(TAG, "insert failed")
                }
            }
        } else {
            // Edit Mode
            viewModelScope.launch {
                try {
                    repository.updateCourse(course)
                    _isSavedSuccessfully.value = true
                } catch (e: Exception) {
                    Log.i(TAG, "update failed")
                }
            }
        }
    }

    private fun isRequiredEntriesHasError(): Boolean {
        _isCourseNameError.value = courseName.value.isNullOrEmpty()
        _isCoursePlaceError.value = coursePlace.value.isNullOrEmpty()
        _isCourseBeginTimeError.value = courseBeginTime.value.isNullOrEmpty()
        _isCourseEndTimeError.value = courseEndTime.value.isNullOrEmpty()

        return courseName.value.isNullOrEmpty() ||
                coursePlace.value.isNullOrBlank() ||
                courseBeginTime.value.isNullOrEmpty() ||
                courseEndTime.value.isNullOrEmpty()
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