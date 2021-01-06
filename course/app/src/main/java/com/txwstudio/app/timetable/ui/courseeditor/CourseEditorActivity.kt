package com.txwstudio.app.timetable.ui.courseeditor

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.databinding.ActivityCourseEditorBinding
import java.text.SimpleDateFormat
import java.util.*

const val INTENT_EXTRA_IS_EDIT_MODE = "is_edit_mode"
const val INTENT_EXTRA_COURSE_ID = "course_id"

class CourseEditorActivity : AppCompatActivity() {

    /**
     * This activity is an alternative to CourseEditorFragment
     * //TODO(Remove this activity and merge into CourseEditorFragment. single activity)
     * */

    private lateinit var binding: ActivityCourseEditorBinding
    private val courseEditorViewModel: CourseEditorViewModel by viewModels()

    private var isEditMode = false
    private var courseIdInDatabase = 0

    private val currentViewPagerItem by lazy { intent.getIntExtra("currentViewPagerItem", 0) }
    private val weekdayArray by lazy { resources.getStringArray(R.array.weekdayList) }

    @TimeFormat
    private var clockFormat = 0
    private val formatter = SimpleDateFormat("a hh:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_course_editor)
        binding.viewModel = courseEditorViewModel

        setupToolBar()

        isEditMode = intent.getBooleanExtra(INTENT_EXTRA_IS_EDIT_MODE, false)
        courseIdInDatabase = intent.getIntExtra(INTENT_EXTRA_COURSE_ID, 0)

        checkIsEditMode()
        setupWeekday()
        subscribeUi()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuSave -> {
                binding.viewModel?.saveFired()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(R.string.courseEditor_exitConfirmDialogTitle)
            setMessage(R.string.courseEditor_exitConfirmDialogMsg)
            setPositiveButton(R.string.all_confirm) { _, _ ->
                super.onBackPressed()
            }
            setNegativeButton(R.string.all_cancel) { _, _ ->

            }
            show()
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.toolbarCourseEditorAct)
        supportActionBar?.title = if (!isEditMode) {
            getString(R.string.courseEditor_titleAdd)
        } else getString(R.string.courseEditor_titleAdd)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
    }

    /**
     * Check is opened as edit mode or not, if yes, let viewModel know.
     * */
    private fun checkIsEditMode() {
        if (isEditMode) {
            courseEditorViewModel.isEditMode.value = isEditMode
            courseEditorViewModel.courseId.value = courseIdInDatabase
            courseEditorViewModel.setupValueForEditing()
        }
    }

    /**
     * Set text by is edit mode or not.
     * Little hack here, the text and real value is not associate
     * */
    private fun setupWeekday() {
        if (isEditMode) {
            binding.dropDownCourseEditorAct.setText(weekdayArray[courseEditorViewModel.courseWeekday.value!!])
        } else {
            binding.dropDownCourseEditorAct.setText(weekdayArray[currentViewPagerItem])
            courseEditorViewModel.courseWeekday.value = currentViewPagerItem
        }
        val adapter = ArrayAdapter(this, R.layout.list_item, weekdayArray)
        (binding.dropDownCourseEditorAct as? AutoCompleteTextView)?.setAdapter(adapter)
    }


    private fun subscribeUi() {
        // Select weekday dialog
        binding.dropDownCourseEditorAct.setOnItemClickListener { adapterView, view, position, rowId ->
            courseEditorViewModel.courseWeekday.value = position
        }

        // Select course begin time
        binding.textViewCourseEditorActCourseBeginTime.setOnClickListener {
            showMaterialTimePicker(0)
        }

        // Select course end time
        binding.textViewCourseEditorActCourseEndTime.setOnClickListener {
            showMaterialTimePicker(1)
        }


        // Show Error, TODO(Bind these shit into xml, but it won't work)
        courseEditorViewModel.courseNameError.observe(this) {
            if (it) {
                binding.tilCourseEditorActCourseNameEntry.isErrorEnabled = true
                binding.tilCourseEditorActCourseNameEntry.error = getString(R.string.courseEditor_noEntry)
            } else {
                binding.tilCourseEditorActCourseNameEntry.isErrorEnabled = false
            }
        }

        courseEditorViewModel.coursePlaceError.observe(this) {
            if (it) {
                binding.tilCourseEditorActCoursePlaceEntry.isErrorEnabled = true
                binding.tilCourseEditorActCoursePlaceEntry.error = getString(R.string.courseEditor_noEntry)
            } else {
                binding.tilCourseEditorActCoursePlaceEntry.isErrorEnabled = false
            }
        }

        courseEditorViewModel.courseBeginTimeError.observe(this) {
            if (it) {
                binding.tilCourseEditorActCourseBeginTimeEntry.isErrorEnabled = true
                binding.tilCourseEditorActCourseBeginTimeEntry.error = getString(R.string.courseEditor_noEntry)
            } else {
                binding.tilCourseEditorActCourseBeginTimeEntry.isErrorEnabled = false
            }
        }

        courseEditorViewModel.courseEndTimeError.observe(this) {
            if (it) {
                binding.tilCourseEditorActCourseEndTimeEntry.isErrorEnabled = true
                binding.tilCourseEditorActCourseEndTimeEntry.error = getString(R.string.courseEditor_noEntry)
            } else {
                binding.tilCourseEditorActCourseEndTimeEntry.isErrorEnabled = false
            }
        }

        // Close current activity
        courseEditorViewModel.isSaveToFinish.observe(this) {
            if (it) finish()
        }
    }

    // TODO(Architecture)
    private fun showMaterialTimePicker(beginOrEnd: Int) {
        val isSystem24Hour = DateFormat.is24HourFormat(this)
        clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val materialTimePicker = MaterialTimePicker.Builder()
                .setTimeFormat(clockFormat)
                .build()
        materialTimePicker.show(supportFragmentManager, "selectCourseBeginTime")

        materialTimePicker.addOnPositiveButtonClickListener {
            val newHour = materialTimePicker.hour
            val newMinute = materialTimePicker.minute
            this.onTimeSet(newHour, newMinute, beginOrEnd)
        }
    }

    // TODO(Architecture)
    private fun onTimeSet(newHour: Int, newMinute: Int, beginOrEnd: Int) {
        // Frontend
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = newHour
        cal[Calendar.MINUTE] = newMinute
        cal.isLenient = false
        val format: String = formatter.format(cal.time)

        // Backend
        val timeToDatabase = String.format("%02d%02d", newHour, newMinute)

        if (beginOrEnd == 0) {
            binding.textViewCourseEditorActCourseBeginTime.setText(format)
            courseEditorViewModel.courseBeginTime.value = timeToDatabase
        } else {
            binding.textViewCourseEditorActCourseEndTime.setText(format)
            courseEditorViewModel.courseEndTime.value = timeToDatabase
        }
    }
}