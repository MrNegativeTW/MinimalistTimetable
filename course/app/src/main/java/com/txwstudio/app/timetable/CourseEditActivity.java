package com.txwstudio.app.timetable;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;

public class CourseEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TimePickerDialog.OnTimeSetListener {

    // Database and model.
    DBHandler db;
    final Course course = new Course();
    private ArrayList<Course> courseArrayList;
    int ID;

    // Define View.
    TextInputLayout editCourseNameWrapper, editCoursePlaceWrapper;
    EditText editCourseName, editCoursePlace;
    Button editStartTimeButton, editEndTimeButton;
    Spinner editCourseWeekdaysSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setupTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);
        setTitle(R.string.editCourseActivityTitle);

        editCourseNameWrapper = (TextInputLayout) findViewById(R.id.editCourseNameWrapper);
        editCoursePlaceWrapper = (TextInputLayout) findViewById(R.id.editCoursePlaceWrapper);
        editCourseName = (EditText) findViewById(R.id.editCourseName);
        editCoursePlace = (EditText) findViewById(R.id.editCoursePlace);
        editStartTimeButton = (Button) findViewById(R.id.editStartTimeButton);
        editEndTimeButton = (Button) findViewById(R.id.editEndTimeButton);

        // Spinner: Use to Select Weekdays.
        editCourseWeekdaysSpinner = (Spinner) findViewById(R.id.editCourseWeekdaysSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.add_course_item, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editCourseWeekdaysSpinner.setAdapter(adapter);
        editCourseWeekdaysSpinner.setOnItemSelectedListener(this);

        // Database
        db = new DBHandler(this);
        courseArrayList = new ArrayList<>();
        ID = getIntent().getExtras().getInt("ID");

        settingValue(ID);

        submitVerify();
    }


    /**
     * Get course detail from database.
     * Set default course detail on screen.
     * Set default course detail to methods.
     * */
    private void settingValue(int ID) {
        courseArrayList = db.getCourseById(ID);

        String Name = courseArrayList.get(0).getCourseName();
        String Place = courseArrayList.get(0).getCoursePlace();
        String StartTime = courseArrayList.get(0).getCourseStartTime();
        int Weekday = courseArrayList.get(0).getCourseWeekday();
        String EndTime = courseArrayList.get(0).getCourseEndTime();

        editCourseName.setText(Name, TextView.BufferType.EDITABLE);
        editCoursePlace.setText(Place);
        editCourseWeekdaysSpinner.setSelection(Weekday);
        editStartTimeButton.setText(StartTime.replaceAll("..(?!$)", "$0:"));
        editEndTimeButton.setText(EndTime.replaceAll("..(?!$)", "$0:"));

        course.setCourseName(Name);
        course.setCoursePlace(Place);
        course.setCourseWeekday(Weekday);
        course.setCourseStartTime(StartTime);
        course.setCourseEndTime(EndTime);
    }




    private void submitVerify() {
        // Save Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_update);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editCourseName.length() == 0 || editCoursePlace.length() == 0) {
                    if (editCourseName.length() == 0) {
                        editCourseNameWrapper.setError(getString(R.string.errorNoEntry));
                    }
                    if (editCoursePlace.length() == 0) {
                        editCoursePlaceWrapper.setError(getString(R.string.errorNoEntry));
                    }
                } else {
                    course.setCourseName(editCourseName.getText().toString());
                    course.setCoursePlace(editCoursePlace.getText().toString());
                    db.updateCourse(course, ID);
                    finish();
                }
            }
        }); // .setOnClickListener
    }


    private String whichOne = "startTime";

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int min) {
        String timeToShow = String.format("%02d:%02d", hourOfDay, min);
        String timeToSQL = String.format("%02d%02d", hourOfDay, min);
        if (whichOne == "startTime") {
            editStartTimeButton.setText(timeToShow);
            course.setCourseStartTime(timeToSQL);
        } else if (whichOne == "endTime") {
            editEndTimeButton.setText(timeToShow);
            course.setCourseEndTime(timeToSQL);
        }
    }

    public void setTimeButtonOnClick(View v) {
        DialogFragment startTimePicker = new TimePickerFragment();
        startTimePicker.show(getSupportFragmentManager(), "startTimePicker");

        if (v.getId() == R.id.editStartTimeButton) {
            whichOne = "startTime";
        } else if (v.getId() == R.id.editEndTimeButton) {
            whichOne = "endTime";
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        course.setCourseWeekday(editCourseWeekdaysSpinner.getSelectedItemPosition());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
