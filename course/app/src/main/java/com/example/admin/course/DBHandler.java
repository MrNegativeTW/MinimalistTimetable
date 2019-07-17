package com.example.admin.course;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
     * */
    public boolean addCourse(Course course) {
        Log.i("Test", "正在執行：使用 Model 加入資料");
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
            Log.i("Test", "使用 Model 加入資料失敗");
            return false;
        } else {
            return true;
        }
    }


    public Cursor getCourse(int weekdayWanted) {
        // Random youtube video
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TIMETABLE
                    + " WHERE courseWeekday = " + weekdayWanted
                    + " ORDER BY " + COURSE_START_TIME + " ASC";
        Cursor data = db.rawQuery(query, null);
        Log.i("Test", "正在執行：取得課程");
        return data;
    }


    public boolean deleteCourse(int weekday, String courseName, String coursePlace, String courseTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TIMETABLE
                    + " WHERE " + COURSE_WEEKDAY + " = " + weekday
                    + " AND " + COURSE_NAME + "=" + courseName;
        db.rawQuery(query, null);
        return true;
    }

}
