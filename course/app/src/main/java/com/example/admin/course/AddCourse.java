package com.example.admin.course;

import android.app.TimePickerDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddCourse extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    DBHandler DBHandler;
    TextInputLayout addCourseNameWrapper, addCoursePlaceWrapper;
    EditText addCourseName, addCoursePlace;
    Button startTimeButton, endTimeButton;
    Spinner addCourseWeekdaysSpinner;

    private int weekdaySelected = 0;
    private String courseStartTimeNewEntry = "9999", courseEndTimeNewEntry = "9999";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        addCourseNameWrapper = (TextInputLayout) findViewById(R.id.addCourseNameWrapper);
        addCoursePlaceWrapper = (TextInputLayout) findViewById(R.id.addCoursePlaceWrapper);
        addCourseName = (EditText) findViewById(R.id.addCourseName);
        addCoursePlace = (EditText) findViewById(R.id.addCoursePlace);
        startTimeButton = (Button) findViewById(R.id.startTimeButton);
        endTimeButton = (Button) findViewById(R.id.endTimeButton);

        // Spinner: Select Weekdays
        addCourseWeekdaysSpinner = (Spinner) findViewById(R.id.addCourseWeekdaysSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.add_course_item, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addCourseWeekdaysSpinner.setAdapter(adapter);
        addCourseWeekdaysSpinner.setOnItemSelectedListener(this);

        // Database
        DBHandler = new DBHandler(this);

        // Save Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseNameNewEntry = addCourseName.getText().toString();
                String coursePlaceNewEntry = addCoursePlace.getText().toString();
                if (addCourseName.length() == 0 || addCoursePlace.length() == 0
                        || courseStartTimeNewEntry == "9999" || courseEndTimeNewEntry == "9999") {
                    if (addCourseName.length() == 0) {
                        addCourseNameWrapper.setError("Oops!");
                    }
                    if (addCoursePlace.length() == 0) {
                        addCoursePlaceWrapper.setError("Oops!");
                    }
                    if (courseStartTimeNewEntry == "9999" || courseEndTimeNewEntry == "9999") {
                        Toast.makeText(AddCourse.this,
                                        "輸入時間", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    addData(courseNameNewEntry, coursePlaceNewEntry,
                            weekdaySelected,
                            courseStartTimeNewEntry, courseEndTimeNewEntry);
                    finish();
                }
//                if (addCoursePlace.length() == 0) {
//
//                }


            }
        });

        // startTimeButton
//        Button startTimeButton = (Button) findViewById(R.id.startTimeButton);
//        startTimeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DialogFragment startTimePicker = new TimePickerFragment();
//                startTimePicker.show(getSupportFragmentManager(), "startTimePicker");
//            }
//        });

//        View.OnClickListener showTimePicker = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final View vv = v;
//
//                TimePickerDialog timePickDialog = new TimePickerDialog();
//            }
//        };
    }

    private boolean addDataStatus = false;
    public void addData(String courseNameNewEntry, String coursePlaceNewEntry,
                        int weekdaySelected,
                        String courseStartTimeNewEntry, String courseEndTimeNewEntry) {
        boolean status = DBHandler.addCourse(courseNameNewEntry, coursePlaceNewEntry,
                                            weekdaySelected,
                                            courseStartTimeNewEntry, courseEndTimeNewEntry);
        Log.i("Test",
                "入庫 - 名稱：" + courseNameNewEntry
                        + " 地點：" + coursePlaceNewEntry
                        + " 日期：" + weekdaySelected
                        + "開始：" + courseStartTimeNewEntry
                        + "結束：" + courseEndTimeNewEntry);
        if (status) {
            Log.i("Test", "加入成功");
        } else {
            Log.i("Test", "加入失敗");
        }
    }


    /**
     * In this two func, they will handle time set.
     *
     * func onTimeSet: Assign value to startTime and endTime.
     * func setTimeButtonOnClick: Open time picker dialog
     *
     * arg whichOne: Use Determine is start or end.
     * */
    private String whichOne = "startTime";

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int min) {
        String timeToShow = String.format("%02d:%02d", hourOfDay, min);
        String timeToSQL = String.format("%02d%02d", hourOfDay, min);
        if (whichOne == "startTime") {
            startTimeButton.setText(timeToShow);
            courseStartTimeNewEntry = timeToSQL;
            Log.i("Test", "courseStartTimeNewEntry: " + courseStartTimeNewEntry);
        } else if (whichOne == "endTime") {
            endTimeButton.setText(timeToShow);
            courseEndTimeNewEntry = timeToSQL;
            Log.i("Test", "courseEndTimeNewEntry: " + courseEndTimeNewEntry);
        }
    }

    public void setTimeButtonOnClick(View v) {
        DialogFragment startTimePicker = new TimePickerFragment();
        startTimePicker.show(getSupportFragmentManager(), "startTimePicker");

        if (v.getId() == R.id.startTimeButton) {
            whichOne = "startTime";
            Log.i("Test", "開始按鈕, WhichOne 為 " + whichOne);
        } else if (v.getId() == R.id.endTimeButton) {
            whichOne = "endTime";
            Log.i("Test Message", "結束按鈕, WhichOne 為 " + whichOne);
        }
        Log.i("Test", "This is setTimeButtonOnClick.");
    }


    /**
     * Determine which weekday it is.
     * Start from 0, which is Monday.
     * */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        String text= adapterView.getItemAtPosition(i).toString();
//        Toast.makeText(adapterView.getContext(), text, Toast.LENGTH_SHORT).show();
        weekdaySelected = addCourseWeekdaysSpinner.getSelectedItemPosition();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
