package com.txwstudio.app.timetable.model

data class Course2(
        var courseName: String?,
        var coursePlace: String?,
        var courseStartTime: String?,
        var courseEndTime: String?,
        var courseTeacher: String?,
        var Id: Int?,
        var courseWeekday: Int? = 0
)