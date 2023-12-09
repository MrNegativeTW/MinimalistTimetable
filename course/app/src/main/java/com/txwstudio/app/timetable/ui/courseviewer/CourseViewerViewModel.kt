package com.txwstudio.app.timetable.ui.courseviewer

import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.txwstudio.app.timetable.MyApplication
import com.txwstudio.app.timetable.data.Course3
import com.txwstudio.app.timetable.data.CourseRepository
import com.txwstudio.app.timetable.utils.WHICH_WEEKDAY
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CourseViewerViewModel(
    private val repository: CourseRepository, private val weekday: Int
) : ViewModel() {

    private val _courseViewerEventChannel = Channel<CourseViewerEvent>()
    val courseViewerEvent = _courseViewerEventChannel.receiveAsFlow()

    val courseByWeekday: LiveData<List<Course3>> =
        repository.getCourseByWeekday(weekday).asLiveData()

    fun insert(course: Course3) = viewModelScope.launch {
        repository.insertCourse(course)
    }

    fun deleteCourse(course: Course3) = viewModelScope.launch {
        repository.deleteCourse(course)
        _courseViewerEventChannel.send(CourseViewerEvent.ShowUndoDeleteMessage(course))
    }

    sealed class CourseViewerEvent {
        data class ShowUndoDeleteMessage(val course: Course3) : CourseViewerEvent()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Get the Application object from extras
                val application = checkNotNull(this[APPLICATION_KEY])

                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = createSavedStateHandle()
                val weekday: Int = checkNotNull(savedStateHandle[WHICH_WEEKDAY])

                CourseViewerViewModel(
                    (application as MyApplication).courseRepository, weekday
                )
            }
        }
    }
}