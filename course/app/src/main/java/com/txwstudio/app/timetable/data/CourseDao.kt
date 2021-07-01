package com.txwstudio.app.timetable.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM timeTable WHERE courseWeekday = :weekday ORDER BY courseStartTime ASC")
    fun getCourseByWeekday(weekday: Int): Flow<List<Course3>>

    @Query("SELECT * FROM timeTable WHERE id = :id")
    suspend fun getCourseById(id: Int): Course3

    @Insert
    suspend fun insertCourse(course: Course3): Long

    @Delete
    suspend fun deleteCourse(course: Course3)
}