package com.txwstudio.app.timetable.ui.courseeditor

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.databinding.ActivityCourseEditorBinding

class CourseEditorActivity : AppCompatActivity() {

    /**
     * This activity is an alternative to CourseEditorFragment
     * //TODO(Remove this activity and merge into CourseEditorFragment. single activity)
     * */

    private lateinit var binding: ActivityCourseEditorBinding
    private val courseEditorViewModel: CourseEditorViewModel by viewModels()

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_course_editor)

//        binding.viewModel = courseEditorViewModel

        setupToolBar()
        subscribeUi()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuSave -> {
                Log.i("TESTTT", "45646415")
                return true
            }
            android.R.id.home -> {
                MaterialAlertDialogBuilder(this).apply {
                    setTitle(R.string.courseEditor_exitConfirmDialogTitle)
                    setMessage(R.string.courseEditor_exitConfirmDialogMsg)
                    setPositiveButton(R.string.all_confirm) { _, _ ->
                        finish()
                    }
                    setNegativeButton(R.string.all_cancel) { _, _ ->

                    }
                    show()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.toolbarCourseEditorAct)
        supportActionBar?.title = if (!isEditMode) "Add Course" else "Edit Course"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
    }

    private fun subscribeUi() {

        /* Get the current day and set it as default when adding the course. */
        val autoWeekday = intent.getIntExtra("autoWeekday", 0)

        val items = listOf("Material", "Design", "Components", "Android")
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        (binding.dropDownCourseEditorAct as? AutoCompleteTextView)?.setAdapter(adapter)
    }
}