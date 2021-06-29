package com.txwstudio.app.timetable.ui.courseviewer

import androidx.lifecycle.*
import com.txwstudio.app.timetable.data.Course3
import com.txwstudio.app.timetable.data.CourseRepository
import kotlinx.coroutines.launch

class CourseViewerViewModel(private val repository: CourseRepository, private val weekday: Int) : ViewModel() {

    val courseByWeekday: LiveData<List<Course3>> = repository.getCourseByWeekday(weekday).asLiveData()

    fun insert(course: Course3) = viewModelScope.launch {
        repository.insertCourse(course)
    }
}

class CourseViewerViewModelFactory(private val repository: CourseRepository, private val weekday: Int) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseViewerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseViewerViewModel(repository, weekday) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}