package com.example.admin.course;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper {

    private final static int DB_VERSION = 1;
    private final static String DB_NAME = "MyCourse.db";

    private final static String MONDAY_TABLE = "MondayTable";
    private final static String ID = "id";
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
        String createTable = "CREATE TABLE IF NOT EXISTS " + MONDAY_TABLE +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
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
        db.execSQL("DROP TABLE IF EXISTS " + MONDAY_TABLE);
        onCreate(db);
    }

    public boolean addCourse(String courseName, String coursePlace,
                             int weekDay,
                             String courseStartTime, String coursEndTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues coutentValues = new ContentValues();
        coutentValues.put(COURSE_NAME, courseName);
        coutentValues.put(COURSE_PLACE, coursePlace);
        coutentValues.put(COURSE_WEEKDAY, weekDay);
        coutentValues.put(COURSE_START_TIME, courseStartTime);
        coutentValues.put(COURSE_END_TIME, coursEndTime);
        Log.i("Test", "正在加入以下資料：" + courseName);

        long result = db.insert(MONDAY_TABLE, null, coutentValues);
        Log.i("Test", String.valueOf(result));
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getCourse(int weekdayWanted) {
        // Random youtube video
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + MONDAY_TABLE
                    + " WHERE courseWeekday = " + weekdayWanted
                    + " ORDER BY " + COURSE_START_TIME + " ASC";
        Cursor data = db.rawQuery(query, null);
        Log.i("Test", String.valueOf(data) + "I am Here!");
        return data;

        // Google



    }

}
