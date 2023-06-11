package com.txwstudio.app.timetable.ui.courseeditor

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
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
import com.txwstudio.app.timetable.utilities.INTENT_TIMETABLE_CHANGED
import com.txwstudio.app.timetable.utilities.StringUtils
import com.txwstudio.app.timetable.widget.TimetableWidgetProvider
import java.util.*

private const val TAG_TIME_PICKER_BEGIN_TIME = 0
private const val TAG_TIME_PICKER_END_TIME = 1

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

        viewModel.isEditMode.observe(viewLifecycleOwner) {
            (activity as AppCompatActivity).supportActionBar!!.title = if (it) {
                getString(R.string.courseEditor_titleEdit)
            } else {
                getString(R.string.courseEditor_titleAdd)
            }
        }
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
            showMaterialTimePicker(TAG_TIME_PICKER_BEGIN_TIME)
        }

        // Select course end time
        binding.textViewCourseEditorFragCourseEndTime.setOnClickListener {
            showMaterialTimePicker(TAG_TIME_PICKER_END_TIME)
        }
    }

    private fun subscribeViewModel() {
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
                StringUtils.getHumanReadableTimeFormat(it)
            )
        }

        viewModel.courseEndTime.observe(viewLifecycleOwner) {
            binding.textViewCourseEditorFragCourseEndTime.setText(
                StringUtils.getHumanReadableTimeFormat(it)
            )
        }

        viewModel.isSavedSuccessfully.observe(viewLifecycleOwner) {
            if (it) {
                // Send broadcast intent to update the widget.
                val intent = Intent(requireContext(), TimetableWidgetProvider::class.java).apply {
                    action = INTENT_TIMETABLE_CHANGED
                }
                requireContext().sendBroadcast(intent)

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
            viewModel.submitCourseBeginTime(timeToDatabase)
        } else {
            viewModel.submitCourseEndTime(timeToDatabase)
        }
    }

    companion object {
        private const val TAG = "CourseEditorFragment"
    }
}