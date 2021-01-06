package com.txwstudio.app.timetable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.txwstudio.app.timetable.model.Course;
import com.txwstudio.app.timetable.model.Course2;

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
                COURSE_WEEKDAY + " INTERGER," +
                COURSE_START_TIME + " TEXT," +
                COURSE_END_TIME + " TEXT" +
                ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TIMETABLE);
        onCreate(db);
    }


    /**
     * Add course.
     *
     * @return True for success, False for failed. (-1 == failed)
     */
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

    public boolean addCourse(Course2 course2) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COURSE_NAME, course2.getCourseName());
        contentValues.put(COURSE_PLACE, course2.getCoursePlace());
        contentValues.put(COURSE_WEEKDAY, course2.getCourseWeekday());
        contentValues.put(COURSE_START_TIME, course2.getCourseBeginTime());
        contentValues.put(COURSE_END_TIME, course2.getCourseEndTime());
        long testResult = db.insert(TIMETABLE, null, contentValues);
        db.close();
        return testResult != -1;
    }


    /**
     * Get course information by list.
     */
    public ArrayList<Course> getCourse(int dayWanted) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Course> courseArrayList = new ArrayList<>();
        Course course;

        String selection = COURSE_WEEKDAY + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(dayWanted)};
        String orderBy = COURSE_START_TIME + " ASC";
        Cursor data = db.query(TIMETABLE, null, selection, selectionArgs,
                null, null, orderBy);

        while (data.moveToNext()) {
            course = new Course();
            course.setID(data.getInt(data.getColumnIndex(COURSE_ID)));
            course.setCourseName(data.getString(data.getColumnIndex(COURSE_NAME)));
            course.setCoursePlace(data.getString(data.getColumnIndex(COURSE_PLACE)));
            course.setCourseStartTime(data.getString(data.getColumnIndex(COURSE_START_TIME)));
            course.setCourseEndTime(data.getString(data.getColumnIndex(COURSE_END_TIME)));
            courseArrayList.add(course);
        }
        data.close();
        db.close();
        return courseArrayList;
    }

    /**
     * Get course information once.
     */
    public ArrayList<Course> getCourseById(int ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Course> courseArrayList = new ArrayList<>();
        Course course;

        String selection = COURSE_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(ID)};
        Cursor data = db.query(TIMETABLE, null, selection, selectionArgs,
                null, null, null);

        while (data.moveToNext()) {
            course = new Course();
            course.setID(data.getInt(data.getColumnIndex(COURSE_ID)));
            course.setCourseName(data.getString(data.getColumnIndex(COURSE_NAME)));
            course.setCoursePlace(data.getString(data.getColumnIndex(COURSE_PLACE)));
            course.setCourseWeekday(data.getInt(data.getColumnIndex(COURSE_WEEKDAY)));
            course.setCourseStartTime(data.getString(data.getColumnIndex(COURSE_START_TIME)));
            course.setCourseEndTime(data.getString(data.getColumnIndex(COURSE_END_TIME)));
            courseArrayList.add(course);
        }
        data.close();
        db.close();
        return courseArrayList;
    }


    /**
     * Delete course.
     */
    public void deleteCourse(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String IDD = String.valueOf(ID);
        db.delete(TIMETABLE, COURSE_ID + "=?", new String[]{IDD});
        db.close();
    }


    /**
     * Update course.
     */
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
