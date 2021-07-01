package com.txwstudio.app.timetable.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room
 * */
@Entity(tableName = "timeTable")
data class Course3(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int?,
    @ColumnInfo(name = "courseName") var courseName: String?,
    @ColumnInfo(name = "coursePlace") var coursePlace: String?,
    @ColumnInfo(name = "courseWeekday") var courseWeekday: Int?,
    @ColumnInfo(name = "courseStartTime") var courseStartTime: String?,
    @ColumnInfo(name = "courseEndTime") var courseEndTime: String?,
    @ColumnInfo(name = "profName") var profName: String?,
)