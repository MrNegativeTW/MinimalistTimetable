package com.txwstudio.app.timetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "MyCourse.db";
    private final static String TIMETABLE = "timeTable";
    private final static String COURSE_ID = "id";
    private final static String COURSE_NAME = "courseName";
    private final static String COURSE_PLACE = "coursePlace";
    private final static String COURSE_WEEKDAY = "courseWeekday";
    private final static String COURSE_START_TIME = "courseStartTime";
    private final static String COURSE_END_TIME = "courseEndTime";
    private final static String COURSE_TEACHER = "courseTeacher";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TIMETABLE + "(" +
                COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COURSE_NAME + " TEXT," +
                COURSE_PLACE + " TEXT," +
                COURSE_WEEKDAY+ " INTERGER," +
                COURSE_START_TIME +" TEXT," +
                COURSE_END_TIME +" TEXT" +
                ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TIMETABLE);
        onCreate(db);
    }


    /**
     * Methods for Course things.
     *
     * addCourse(): Adding data from CourseAddActivity.Activity
     * getCourse(): Called by 5 fragments, will return a ArrayList, and send to Adapter.
     * deleteCourse(): Delete course by ID, called by every new adapter.
     * updateCourse():
     * */
    public boolean addCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COURSE_NAME, course.getCourseName());
        contentValues.put(COURSE_PLACE, course.getCoursePlace());
        contentValues.put(COURSE_WEEKDAY, course.getCourseWeekday());
        contentValues.put(COURSE_START_TIME, course.getCourseStartTime());
        contentValues.put(COURSE_END_TIME, course.getCourseEndTime());
        long testResult = db.insert(TIMETABLE, null, contentValues);
        db.close();
        if (testResult == -1) {
            return false;
        } else {
            return true;
        }
    }


    public ArrayList<Course> getCourse(int dayWanted) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Course> courseArrayList = new ArrayList<>();
        Course course;

        String query = "SELECT * FROM " + TIMETABLE
                    + " WHERE " + COURSE_WEEKDAY + " = " + dayWanted
                    + " ORDER BY " + COURSE_START_TIME + " ASC";
        Cursor data = db.rawQuery(query, null);
        while(data.moveToNext()) {
            course = new Course();
            course.setID(data.getInt(data.getColumnIndex(COURSE_ID)));
            course.setCourseName(data.getString(data.getColumnIndex(COURSE_NAME)));
            course.setCoursePlace(data.getString(data.getColumnIndex(COURSE_PLACE)));
            course.setCourseStartTime(data.getString(data.getColumnIndex(COURSE_START_TIME)));
            course.setCourseEndTime(data.getString(data.getColumnIndex(COURSE_END_TIME)));
            courseArrayList.add(course);
        }
        db.close();
        return courseArrayList;
    }


    public ArrayList<Course> getCourseById(int ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Course> courseArrayList = new ArrayList<>();
        Course course;

        String query = "SELECT * FROM " + TIMETABLE + " WHERE " + COURSE_ID + " = " + ID;
        Cursor data = db.rawQuery(query, null);

        while(data.moveToNext()){
            course = new Course();
            course.setID(data.getInt(data.getColumnIndex(COURSE_ID)));
            course.setCourseName(data.getString(data.getColumnIndex(COURSE_NAME)));
            course.setCoursePlace(data.getString(data.getColumnIndex(COURSE_PLACE)));
            course.setCourseWeekday(data.getInt(data.getColumnIndex(COURSE_WEEKDAY)));
            course.setCourseStartTime(data.getString(data.getColumnIndex(COURSE_START_TIME)));
            course.setCourseEndTime(data.getString(data.getColumnIndex(COURSE_END_TIME)));
            courseArrayList.add(course);
        }
        db.close();
        return courseArrayList;
    }

    public void deleteCourse(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String IDD = String.valueOf(ID);
        db.delete(TIMETABLE, COURSE_ID + "=?", new String[]{IDD});
        db.close();

    }


    public void updateCourse(Course course, int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COURSE_NAME, course.getCourseName());
        contentValues.put(COURSE_PLACE, course.getCoursePlace());
        contentValues.put(COURSE_WEEKDAY, course.getCourseWeekday());
        contentValues.put(COURSE_START_TIME, course.getCourseStartTime());
        contentValues.put(COURSE_END_TIME, course.getCourseEndTime());
        db.update(TIMETABLE, contentValues, COURSE_ID + " = " + ID, null);
        db.close();
    }
}
