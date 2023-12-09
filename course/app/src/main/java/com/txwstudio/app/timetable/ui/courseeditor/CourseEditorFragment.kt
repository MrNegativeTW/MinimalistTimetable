package com.txwstudio.app.timetable.ui.courseeditor

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.databinding.FragmentCourseEditorBinding
import com.txwstudio.app.timetable.utils.StringUtils
import com.txwstudio.app.timetable.widget.TimetableWidgetProvider

/**
 * An editor to add or edit the class info.
 * */
class CourseEditorFragment : Fragment(), MenuProvider {

    @Deprecated("Get args from ViewModel factory")
    private val args: CourseEditorFragmentArgs by navArgs()

    private val viewModel: CourseEditorViewModel by viewModels {
        CourseEditorViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            exitConfirmDialog()
        }
    }

    private lateinit var binding: FragmentCourseEditorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseEditorBinding.inflate(layoutInflater)

        setupToolBar()
        setupWeekdayDropdown()

        subscribeUi()
        subscribeViewModel()
        subscribeViewModelForErrorEvents()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val adRequest = AdRequest.Builder().build()
        binding.adViewCourseEditorFrag.loadAd(adRequest)
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.save_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.menuSave -> {
                viewModel.submitCourseText(
                    binding.editTextCourseEditorFragCourseNameEntry.text.toString(),
                    binding.editTextCourseEditorFragCoursePlaceEntry.text.toString(),
                    binding.editTextCourseEditorFragCourseProfEntry.text.toString()
                )
                viewModel.saveToDatabase()
                true
            }

            android.R.id.home -> {
                exitConfirmDialog()
                true
            }

            else -> false
        }
    }

    private fun setupToolBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarCourseEditorFrag)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)

        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private val weekdayArray by lazy { resources.getStringArray(R.array.weekdayList) }

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
        binding.dropDownCourseEditorFrag.setOnItemClickListener { _, _, position, _ ->
            viewModel.submitCourseWeekday(position)
        }

        // Select course begin time
        binding.textViewCourseEditorFragCourseBeginTime.setOnClickListener {
            showMaterialTimePicker(TIME_PICKER_BEGIN)
        }

        // Select course end time
        binding.textViewCourseEditorFragCourseEndTime.setOnClickListener {
            showMaterialTimePicker(TIME_PICKER_END)
        }
    }

    private fun subscribeViewModel() {
        viewModel.isEditMode.observe(viewLifecycleOwner) {
            (activity as AppCompatActivity).supportActionBar!!.title = if (it) {
                getString(R.string.courseEditor_titleEdit)
            } else {
                getString(R.string.courseEditor_titleAdd)
            }
        }

        viewModel.courseName.observe(viewLifecycleOwner) {
            binding.editTextCourseEditorFragCourseNameEntry.setText(it)
        }

        viewModel.coursePlace.observe(viewLifecycleOwner) {
            binding.editTextCourseEditorFragCoursePlaceEntry.setText(it)
        }

        viewModel.courseProf.observe(viewLifecycleOwner) {
            binding.editTextCourseEditorFragCourseProfEntry.setText(it)
        }

        // Change weekday dropdown text. In order to set init text when is edit mode, observe it.
        viewModel.courseWeekday.observe(viewLifecycleOwner) {
            binding.dropDownCourseEditorFrag.setText(weekdayArray[it], false)
        }

        viewModel.courseBeginTime.observe(viewLifecycleOwner) {
            binding.textViewCourseEditorFragCourseBeginTime.setText(
                StringUtils.getHumanReadableTimeFormat(requireContext(), it)
            )
        }

        viewModel.courseEndTime.observe(viewLifecycleOwner) {
            binding.textViewCourseEditorFragCourseEndTime.setText(
                StringUtils.getHumanReadableTimeFormat(requireContext(),  it)
            )
        }

        viewModel.isSavedSuccessfully.observe(viewLifecycleOwner) {
            if (it) {
                // Update widget
                val component = ComponentName(requireContext(), TimetableWidgetProvider::class.java)
                with(AppWidgetManager.getInstance(requireContext())) {
                    val appWidgetIds = getAppWidgetIds(component)
                    notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview_appwidget)
                }

                if (it) findNavController().navigateUp()
            }
        }
    }

    private fun subscribeViewModelForErrorEvents() {
        viewModel.isCourseNameError.observe(viewLifecycleOwner) {
            if (it) {
                binding.tilCourseEditorFragCourseNameEntry.isErrorEnabled = true
                binding.tilCourseEditorFragCourseNameEntry.error =
                    getString(R.string.courseEditor_noEntry)
            } else {
                binding.tilCourseEditorFragCourseNameEntry.isErrorEnabled = false
            }
        }

        viewModel.isCoursePlaceError.observe(viewLifecycleOwner) {
            if (it) {
                binding.tilCourseEditorFragCoursePlaceEntry.isErrorEnabled = true
                binding.tilCourseEditorFragCoursePlaceEntry.error =
                    getString(R.string.courseEditor_noEntry)
            } else {
                binding.tilCourseEditorFragCoursePlaceEntry.isErrorEnabled = false
            }
        }

        viewModel.isCourseBeginTimeError.observe(viewLifecycleOwner) {
            if (it) {
                binding.tilCourseEditorFragCourseBeginTimeEntry.isErrorEnabled = true
                binding.tilCourseEditorFragCourseBeginTimeEntry.error =
                    getString(R.string.courseEditor_noEntry)
            } else {
                binding.tilCourseEditorFragCourseBeginTimeEntry.isErrorEnabled = false
            }
        }

        viewModel.isCourseEndTimeError.observe(viewLifecycleOwner) {
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
            setPositiveButton(R.string.all_discard) { _, _ ->
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

        val timePicker = MaterialTimePicker.Builder().apply {
            setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            setTimeFormat(clockFormat)
//            setHour() // [0, 23]
//            setMinute() // [0, 60)
        }.build()

        timePicker.show(requireActivity().supportFragmentManager, "tag")

        timePicker.addOnPositiveButtonClickListener {
            onTimeSet(timePicker.hour, timePicker.minute, isBeginOrEnd)
        }
    }

    private fun onTimeSet(newHour: Int, newMinute: Int, isBeginOrEnd: Int) {
        val timeToDatabase = String.format("%02d%02d", newHour, newMinute)
        when (isBeginOrEnd) {
            TIME_PICKER_BEGIN -> viewModel.submitCourseBeginTime(timeToDatabase)
            TIME_PICKER_END -> viewModel.submitCourseEndTime(timeToDatabase)
        }
    }

    companion object {
        private const val TAG = "CourseEditorFragment"
        private const val TIME_PICKER_BEGIN = 0
        private const val TIME_PICKER_END = 1
    }
}