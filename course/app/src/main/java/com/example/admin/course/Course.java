package com.example.admin.course;

public class Course {

    private String courseName, coursePlace, courseStartTime, courseEndTime;
    private int courseWeekday;

    public Course(){}

    public Course(String courseName, String coursePlace, String courseStartTime,
                  String courseEndTime, int courseWeekday) {
        this.courseName = courseName;
        this.coursePlace = coursePlace;
        this.courseStartTime = courseStartTime;
        this.courseEndTime = courseEndTime;
        this.courseWeekday = courseWeekday;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCoursePlace(String coursePlace) {
        this.coursePlace = coursePlace;
    }

    public String getCoursePlace() {
        return coursePlace;
    }

    public void setCourseStartTime(String courseStartTime) {
        this.courseStartTime = courseStartTime;
    }

    public String getCourseStartTime() {
        return courseStartTime;
    }

    public void setCourseEndTime(String courseEndTime) {
        this.courseEndTime = courseEndTime;
    }

    public String getCourseEndTime() {
        return courseEndTime;
    }

    public void setCourseWeekday(int courseWeekday) {this.courseWeekday = courseWeekday;}

    public int getCourseWeekday() {
        return courseWeekday;
    }
}
