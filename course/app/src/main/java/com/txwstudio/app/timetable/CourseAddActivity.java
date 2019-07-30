package com.txwstudio.app.timetable;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class CourseAddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    // Database and model.
    DBHandler DBHandler;
    final Course course = new Course();

    // Define View.
    TextInputLayout addCourseNameWrapper, addCoursePlaceWrapper;
    EditText addCourseName, addCoursePlace;
    Button startTimeButton, endTimeButton;
    Spinner addCourseWeekdaysSpinner;
    private AdView mAdView;

    // Default value for time, use to verify input or not.
    private String courseStartTimeNewEntry = "9999", courseEndTimeNewEntry = "9999";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setupTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_add);
        setupAds();

        addCourseNameWrapper = (TextInputLayout) findViewById(R.id.addCourseNameWrapper);
        addCoursePlaceWrapper = (TextInputLayout) findViewById(R.id.addCoursePlaceWrapper);
        addCourseName = (EditText) findViewById(R.id.addCourseName);
        addCoursePlace = (EditText) findViewById(R.id.addCoursePlace);
        startTimeButton = (Button) findViewById(R.id.startTimeButton);
        endTimeButton = (Button) findViewById(R.id.endTimeButton);

        // Spinner: Use to Select Weekdays.
        addCourseWeekdaysSpinner = (Spinner) findViewById(R.id.addCourseWeekdaysSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.add_course_item, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addCourseWeekdaysSpinner.setAdapter(adapter);
        addCourseWeekdaysSpinner.setOnItemSelectedListener(this);

        // Database
        DBHandler = new DBHandler(this);

        submitVerify();
    }


    private void setupAds() {
        mAdView = (AdView) findViewById(R.id.addCourseAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    /**
     * Verify submitted data is empty or not, NO content verify.
     * */
    private void submitVerify() {
        // Save Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addCourseName.length() == 0 || addCoursePlace.length() == 0
                        || courseStartTimeNewEntry == "9999" || courseEndTimeNewEntry == "9999") {
                    if (addCourseName.length() == 0) {
                        addCourseNameWrapper.setError("Oops!");
                    }
                    if (addCoursePlace.length() == 0) {
                        addCoursePlaceWrapper.setError("Oops!");
                    }
                    if (courseStartTimeNewEntry == "9999" || courseEndTimeNewEntry == "9999") {
                        Toast.makeText(CourseAddActivity.this,
                                "輸入時間", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    course.setCourseName(addCourseName.getText().toString());
                    course.setCoursePlace(addCoursePlace.getText().toString());
                    DBHandler.addCourse(course);

                    // Exit activity
                    finish();
                }
            }
        });
    }


    /**
     * In this two func, they will handle time set.
     *
     * func onTimeSet: Assign value to startTime or endTime.
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
            course.setCourseStartTime(timeToSQL);
        } else if (whichOne == "endTime") {
            endTimeButton.setText(timeToShow);
            courseEndTimeNewEntry = timeToSQL;
            course.setCourseEndTime(timeToSQL);
        }
    }

    public void setTimeButtonOnClick(View v) {
        DialogFragment startTimePicker = new TimePickerFragment();
        startTimePicker.show(getSupportFragmentManager(), "startTimePicker");

        if (v.getId() == R.id.startTimeButton) {
            whichOne = "startTime";
        } else if (v.getId() == R.id.endTimeButton) {
            whichOne = "endTime";
        }
    }


    /**
     * Determine which day it is.
     * Start from 0, Monday.
     * */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        course.setCourseWeekday(addCourseWeekdaysSpinner.getSelectedItemPosition());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Default value for weekday.
        course.setCourseWeekday(0);
    }
}
