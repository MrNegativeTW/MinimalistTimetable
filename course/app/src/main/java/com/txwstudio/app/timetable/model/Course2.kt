package com.txwstudio.app.timetable.model

data class Course2(
        var courseName: String?,
        var coursePlace: String?,
        var courseBeginTime: String?,
        var courseEndTime: String?,
        var courseWeekday: Int? = 0,
        var courseTeacher: String? = "",
        var Id: Int? = -1
)