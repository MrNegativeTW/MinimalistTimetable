package com.txwstudio.app.timetable.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/**
 * See [sunflower](https://github.com/android/sunflower) project, it uses dagger here.
 * */
class CourseRepository(private val courseDao: CourseDao) {

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCourse(course: Course3) = courseDao.insertCourse(course)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteCourse(course: Course3) {
        courseDao.deleteCourse(course)
    }

    fun getCourseByWeekday(weekday: Int): Flow<List<Course3>> {
        return courseDao.getCourseByWeekday(weekday)
    }

    suspend fun getCourseById(courseId: Int) = courseDao.getCourseById(courseId)
}