package com.txwstudio.app.timetable.ui.courseeditor

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.txwstudio.app.timetable.MyApplication
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.databinding.FragmentCourseEditorBinding
import com.txwstudio.app.timetable.utilities.INTENT_TIMETABLE_CHANGED
import com.txwstudio.app.timetable.widget.TimetableWidgetProvider
import java.text.SimpleDateFormat
import java.util.*

private const val TAG_TIME_PICKER_BEGIN_TIME = 0
private const val TAG_TIME_PICKER_END_TIME = 1

/**
 * An editor to add or edit the class info.
 * */
class CourseEditorFragment : Fragment() {

    private lateinit var binding: FragmentCourseEditorBinding
    private val courseEditorViewModel: CourseEditorViewModel by viewModels {
        CourseEditorViewModelFactory(
            (requireActivity().application as MyApplication).courseRepository,
            args.courseId,
            args.currentViewPagerItem
        )
    }

    private val args: CourseEditorFragmentArgs by navArgs()

    private val weekdayArray by lazy { resources.getStringArray(R.array.weekdayList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            exitConfirmDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseEditorBinding.inflate(inflater, container, false)
        binding.viewModel = courseEditorViewModel
        binding.lifecycleOwner = this

        setupToolBar()
        setupWeekdayDropdown()

        subscribeUi()
        subscribeUiForError()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuSave -> {
                courseEditorViewModel.saveFired()
                return true
            }
            android.R.id.home -> {
                exitConfirmDialog()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarCourseEditorFrag)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)

        setHasOptionsMenu(true)

        courseEditorViewModel.isEditMode.observe(viewLifecycleOwner) {
            // Set actionBar text base on mode.
            (activity as AppCompatActivity).supportActionBar!!.title = if (it) {
                getString(R.string.courseEditor_titleEdit)
            } else {
                getString(R.string.courseEditor_titleAdd)
            }
        }
    }

    /**
     * Set weekday selector text by is edit mode or not.
     * Little hack here, the text and real value is not associate.
     * */
    private fun setupWeekdayDropdown() {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, weekdayArray)
        (binding.dropDownCourseEditorFrag as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun subscribeUi() {
        // Handle selection of weekday dropdown.
        binding.dropDownCourseEditorFrag.setOnItemClickListener { adapterView, view, position, rowId ->
            courseEditorViewModel.courseWeekday.value = position
        }

        // Change weekday dropdown text. In order to set init text when is edit mode, observe it.
        courseEditorViewModel.courseWeekday.observe(viewLifecycleOwner) {
            binding.dropDownCourseEditorFrag.setText(weekdayArray[it], false)
        }

        // Select course begin time
        binding.textViewCourseEditorFragCourseBeginTime.setOnClickListener {
            showMaterialTimePicker(TAG_TIME_PICKER_BEGIN_TIME)
        }

        // Select course end time
        binding.textViewCourseEditorFragCourseEndTime.setOnClickListener {
            showMaterialTimePicker(TAG_TIME_PICKER_END_TIME)
        }

        courseEditorViewModel.courseBeginTime.observe(viewLifecycleOwner) {
            binding.textViewCourseEditorFragCourseBeginTime.setText(displayFormattedTime(it))
        }

        courseEditorViewModel.courseEndTime.observe(viewLifecycleOwner) {
            binding.textViewCourseEditorFragCourseEndTime.setText(displayFormattedTime(it))
        }

        // Close current activity
        courseEditorViewModel.isSavedSuccessfully.observe(viewLifecycleOwner) {
            // Send broadcast intent to update the widget.
            val intent = Intent(requireContext(), TimetableWidgetProvider::class.java).apply {
                action = INTENT_TIMETABLE_CHANGED
            }
            requireContext().sendBroadcast(intent)

            // Close fragment.
            if (it) findNavController().navigateUp()
        }
    }

    /**
     * Subscribe Ui for error event.
     * */
    private fun subscribeUiForError() {
        courseEditorViewModel.courseNameError.observe(viewLifecycleOwner) {
            if (it) {
                binding.tilCourseEditorFragCourseNameEntry.isErrorEnabled = true
                binding.tilCourseEditorFragCourseNameEntry.error =
                    getString(R.string.courseEditor_noEntry)
            } else {
                binding.tilCourseEditorFragCourseNameEntry.isErrorEnabled = false
            }
        }

        courseEditorViewModel.coursePlaceError.observe(viewLifecycleOwner) {
            if (it) {
                binding.tilCourseEditorFragCoursePlaceEntry.isErrorEnabled = true
                binding.tilCourseEditorFragCoursePlaceEntry.error =
                    getString(R.string.courseEditor_noEntry)
            } else {
                binding.tilCourseEditorFragCoursePlaceEntry.isErrorEnabled = false
            }
        }

        courseEditorViewModel.courseBeginTimeError.observe(viewLifecycleOwner) {
            if (it) {
                binding.tilCourseEditorFragCourseBeginTimeEntry.isErrorEnabled = true
                binding.tilCourseEditorFragCourseBeginTimeEntry.error =
                    getString(R.string.courseEditor_noEntry)
            } else {
                binding.tilCourseEditorFragCourseBeginTimeEntry.isErrorEnabled = false
            }
        }

        courseEditorViewModel.courseEndTimeError.observe(viewLifecycleOwner) {
            if (it) {
                binding.tilCourseEditorFragCourseEndTimeEntry.isErrorEnabled = true
                binding.tilCourseEditorFragCourseEndTimeEntry.error =
                    getString(R.string.courseEditor_noEntry)
            } else {
                binding.tilCourseEditorFragCourseEndTimeEntry.isErrorEnabled = false
            }
        }
    }

    /**
     * Show dialog when user tries to exit course editor.
     * */
    private fun exitConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(R.string.courseEditor_exitConfirmDialogTitle)
            setMessage(R.string.courseEditor_exitConfirmDialogMsg)
            setPositiveButton(R.string.all_confirm) { _, _ ->
                findNavController().navigateUp()
            }
            setNegativeButton(R.string.all_cancel) { _, _ ->

            }
            show()
        }
    }

    private fun showMaterialTimePicker(isBeginOrEnd: Int) {
        // Determine system time format.
        val isSystem24Hour = DateFormat.is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H

        val materialTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .build()
        materialTimePicker.show(requireActivity().supportFragmentManager, "tag")

        materialTimePicker.addOnPositiveButtonClickListener {
            val newHour = materialTimePicker.hour
            val newMinute = materialTimePicker.minute
            this.onTimeSet(newHour, newMinute, isBeginOrEnd)
        }
    }

    private fun onTimeSet(newHour: Int, newMinute: Int, isBeginOrEnd: Int) {
        val timeToDatabase = String.format("%02d%02d", newHour, newMinute)

        if (isBeginOrEnd == TAG_TIME_PICKER_BEGIN_TIME) {
            courseEditorViewModel.courseBeginTime.value = timeToDatabase
        } else {
            courseEditorViewModel.courseEndTime.value = timeToDatabase
        }
    }

    private fun displayFormattedTime(time: String): String {
        val formatter = SimpleDateFormat("a hh:mm", Locale.getDefault())
        val cal = Calendar.getInstance()

        val temp = StringBuilder().append(time)
        val h = temp.substring(0, 2)
        val m = temp.substring(2, 4)

        cal[Calendar.HOUR_OF_DAY] = h.toInt()
        cal[Calendar.MINUTE] = m.toInt()
        cal.isLenient = false

        return formatter.format(cal.time)
    }
}