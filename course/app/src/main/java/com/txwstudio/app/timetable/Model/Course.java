package com.txwstudio.app.timetable.Model;

public class Course {

    private String courseName, coursePlace, courseStartTime, courseEndTime, courseTeacher;
    private int ID, courseWeekday;

    public Course(){}

    public Course(int ID, String courseName, String coursePlace, String courseStartTime,
                  String courseEndTime, String courseTeacher, int courseWeekday) {
        this.ID = ID;
        this.courseName = courseName;
        this.coursePlace = coursePlace;
        this.courseStartTime = courseStartTime;
        this.courseEndTime = courseEndTime;
        this.courseTeacher = courseTeacher;
        this.courseWeekday = courseWeekday;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
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

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseWeekday(int courseWeekday) {this.courseWeekday = courseWeekday;}

    public int getCourseWeekday() {
        return courseWeekday;
    }



}
