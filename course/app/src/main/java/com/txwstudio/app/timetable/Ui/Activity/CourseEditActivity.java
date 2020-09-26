package com.txwstudio.app.timetable.Ui.Activity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.txwstudio.app.timetable.DBHandler;
import com.txwstudio.app.timetable.Model.Course;
import com.txwstudio.app.timetable.R;
import com.txwstudio.app.timetable.Ui.Fragments.TimePickerFragment;
import com.txwstudio.app.timetable.Util;

import java.util.ArrayList;

public class CourseEditActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    // Database and model.
    DBHandler db;
    final Course course = new Course();
    private ArrayList<Course> courseArrayList;
    int ID;

    // Define View.
    TextInputLayout editCourseNameWrapper, editCoursePlaceWrapper;
    TextView editStartTimeView, editEndTimeView;
    public TextView editWeekday;
    EditText editCourseName, editCoursePlace;
    private AdView mAdView;

    String[] addCourseItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setupTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupAds();

        findViewById();

        // Database
        db = new DBHandler(this);
        courseArrayList = new ArrayList<>();
        ID = getIntent().getExtras().getInt("ID");

        settingValue(ID);
    }


    private void setupAds() {
        mAdView = (AdView) findViewById(R.id.editCourseAdView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdView.loadAd(adRequest);
            }
        }, 500);
    }


    private void findViewById() {
        editCourseNameWrapper = (TextInputLayout) findViewById(R.id.editCourseNameWrapper);
        editCoursePlaceWrapper = (TextInputLayout) findViewById(R.id.editCoursePlaceWrapper);
        editCourseName = (EditText) findViewById(R.id.editCourseName);
        editCoursePlace = (EditText) findViewById(R.id.editCoursePlace);
        editStartTimeView = (TextView) findViewById(R.id.editStartTimeView);
        editEndTimeView = (TextView) findViewById(R.id.editEndTimeView);
        editWeekday = (TextView) findViewById(R.id.editWeekday);
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
        String EndTime = courseArrayList.get(0).getCourseEndTime();
        int Weekday = courseArrayList.get(0).getCourseWeekday();

        editCourseName.setText(Name, TextView.BufferType.EDITABLE);
        editCoursePlace.setText(Place);
        editStartTimeView.setText(StartTime.replaceAll("..(?!$)", "$0:"));
        editEndTimeView.setText(EndTime.replaceAll("..(?!$)", "$0:"));
        addCourseItem = getResources().getStringArray(R.array.add_course_item);
        editWeekday.setText(String.valueOf(addCourseItem[Weekday]));

        course.setCourseName(Name);
        course.setCoursePlace(Place);
        course.setCourseStartTime(StartTime);
        course.setCourseEndTime(EndTime);
        course.setCourseWeekday(Weekday);
    }


    public void selectDay(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setItems(R.array.add_course_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editWeekday.setText(String.valueOf(addCourseItem[i]));
                course.setCourseWeekday(i);
            }
        });
        dialog.show();
    }


    /**
     * These two func will handle time set.
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
        switch (whichOne) {
            case "startTime":
                editStartTimeView.setText(timeToShow);
                course.setCourseStartTime(timeToSQL);
                break;
            case "endTime":
                editEndTimeView.setText(timeToShow);
                course.setCourseEndTime(timeToSQL);
                break;
        }
    }

    public void setTimeButtonOnClick(View v) {
        switch (v.getId()) {
            case R.id.editStartTimeCardView:
                whichOne = "startTime";
                break;
            case R.id.editEndTimeCardView:
                whichOne = "endTime";
                break;
        }
        DialogFragment startTimePicker = new TimePickerFragment();
        startTimePicker.show(getSupportFragmentManager(), "startTimePicker");
    }


    /**
     * Below code will handle every actionBar button's action.
     *
     * menuSave: Check value then call database to update it.
     * android.R.id.home: If there is already has value, check user want to leave or not.
     * */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuSave:
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
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Util.onBackPressedDialog(this);
    }
}
