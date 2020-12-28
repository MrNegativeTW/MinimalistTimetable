package com.txwstudio.app.timetable.ui.activity;

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
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.txwstudio.app.timetable.DBHandler;
import com.txwstudio.app.timetable.model.Course;
import com.txwstudio.app.timetable.R;
import com.txwstudio.app.timetable.ui.Fragments.TimePickerFragment;
import com.txwstudio.app.timetable.Util;

public class CourseAddActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    // Database and model.
    com.txwstudio.app.timetable.DBHandler DBHandler;
    final Course course = new Course();

    // Define View.
    TextInputLayout addCourseNameWrapper, addCoursePlaceWrapper;
    TextView addStartTimeView, addEndTimeView;
    public TextView addWeekday;
    EditText addCourseName, addCoursePlace;
    private AdView mAdView;

    // Default value for time, use to verify.
    private String courseStartTimeNewEntry = "9999", courseEndTimeNewEntry = "9999";
    String[] addCourseItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.setupTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupAds();
        findViewById();

        // Database
        DBHandler = new DBHandler(this);
    }


    private void setupAds() {
        mAdView = (AdView) findViewById(R.id.addCourseAdView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdView.loadAd(adRequest);
            }
        }, 500);
    }


    private void findViewById() {
        addCourseNameWrapper = (TextInputLayout) findViewById(R.id.addCourseNameWrapper);
        addCoursePlaceWrapper = (TextInputLayout) findViewById(R.id.addCoursePlaceWrapper);
        addCourseName = (EditText) findViewById(R.id.addCourseName);
        addCoursePlace = (EditText) findViewById(R.id.addCoursePlace);
        addStartTimeView = (TextView) findViewById(R.id.addStartTimeView);
        addEndTimeView = (TextView) findViewById(R.id.addEndTimeView);
        addWeekday = (TextView) findViewById(R.id.addWeekday);

        addCourseItem = getResources().getStringArray(R.array.add_course_item);

        /* Get the current day and set it as default when adding the course. */
        int autoWeekday = getIntent().getIntExtra("autoWeekday", 0);
        addWeekday.setText(String.valueOf(addCourseItem[autoWeekday]));
        course.setCourseWeekday(autoWeekday);
    }


    public void selectDay(View v) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setItems(R.array.add_course_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addWeekday.setText(String.valueOf(addCourseItem[i]));
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
                addStartTimeView.setText(timeToShow);
                courseStartTimeNewEntry = timeToSQL;
                course.setCourseStartTime(timeToSQL);
                break;
            case "endTime":
                addEndTimeView.setText(timeToShow);
                courseEndTimeNewEntry = timeToSQL;
                course.setCourseEndTime(timeToSQL);
                break;
        }
    }

    public void setTimeButtonOnClick(View v) {
        switch (v.getId()) {
            case R.id.startTimeCardView:
                whichOne = "startTime";
                break;
            case R.id.endTimeCardView:
                whichOne = "endTime";
                break;
        }
        DialogFragment startTimePicker = new TimePickerFragment();
        startTimePicker.show(getSupportFragmentManager(), "startTimePicker");
    }


    /**
     * Below code will handle every actionBar button's action.
     *
     * menuSave: Check value then call database to save it.
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
                if (addCourseName.length() == 0 || addCoursePlace.length() == 0
                        || courseStartTimeNewEntry.equals("9999") || courseEndTimeNewEntry.equals("9999")) {
                    if (addCourseName.length() == 0) {
                        addCourseNameWrapper.setError(getString(R.string.errorNoEntry));
                    }
                    if (addCoursePlace.length() == 0) {
                        addCoursePlaceWrapper.setError(getString(R.string.errorNoEntry));
                    }
                    if (courseStartTimeNewEntry.equals("9999") || courseEndTimeNewEntry.equals("9999")) {
                        Toast.makeText(CourseAddActivity.this,
                                R.string.errorNoTimeEntry, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    course.setCourseName(addCourseName.getText().toString());
                    course.setCoursePlace(addCoursePlace.getText().toString());
                    DBHandler.addCourse(course);

                    // Exit activity
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
