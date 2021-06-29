package com.txwstudio.app.timetable.viewmodels

import com.txwstudio.app.timetable.data.Course3

class CourseCardViewModel(course3: Course3) {

    val course = course3

    val id = course.id

    val courseName = course.courseName

    val coursePlace = course.coursePlace

    val courseWeekday = course.courseWeekday

    val courseStartTime
        get() = course.courseStartTime?.replace("..(?!$)".toRegex(), "$0:")

    val courseEndTime
        get() = course.courseEndTime?.replace("..(?!$)".toRegex(), "$0:")

    val profName
        get() = if (!course.profName.isNullOrBlank()) ", ${course.profName}" else ""
}