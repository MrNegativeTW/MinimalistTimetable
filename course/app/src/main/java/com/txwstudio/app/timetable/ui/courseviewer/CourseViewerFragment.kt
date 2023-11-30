package com.txwstudio.app.timetable.ui.courseviewer

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.txwstudio.app.timetable.HomeViewPagerFragmentDirections
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.adapter.CourseCardAdapter
import com.txwstudio.app.timetable.data.CourseCardAction
import com.txwstudio.app.timetable.databinding.FragmentCourseViewerBinding
import com.txwstudio.app.timetable.utilities.WHICH_WEEKDAY
import com.txwstudio.app.timetable.widget.TimetableWidgetProvider

/**
 * A fragment that Display the classes for the specific day.
 * */
class CourseViewerFragment : Fragment() {

    private lateinit var binding: FragmentCourseViewerBinding

    private val courseViewerViewModel: CourseViewerViewModel by viewModels {
        CourseViewerViewModel.Factory
    }

    private lateinit var courseCardAdapter: CourseCardAdapter
    private var weekday: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weekday = it.getInt(WHICH_WEEKDAY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseViewerBinding.inflate(layoutInflater)

        setupAdapter()

        subscribeUi(courseCardAdapter)
        return binding.root
    }

    private fun setupAdapter() {
        courseCardAdapter = CourseCardAdapter() { action, course3 ->
            when (action) {
                CourseCardAction.EDIT -> {
                    val direction =
                        HomeViewPagerFragmentDirections.actionHomeViewPagerFragmentToCourseEditorFragment(
                            courseId = course3.id!!
                        )
                    findNavController().navigate(direction)
                }
                CourseCardAction.DELETE -> {
                    courseViewerViewModel.deleteCourse(course3)
                }
            }
        }
        binding.recyclerViewCourseViewer.adapter = courseCardAdapter
    }

    private fun subscribeUi(courseCardAdapter: CourseCardAdapter) {
        // Display "delete success" message when the course got deleted.
        // From: CodingInFlow
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            courseViewerViewModel.courseViewerEvent.collect { event ->
                when (event) {
                    is CourseViewerViewModel.CourseViewerEvent.ShowUndoDeleteMessage -> {
                        // Show message
                        Snackbar.make(requireView(), R.string.dialogDeleted, Snackbar.LENGTH_LONG).show()

                        // Update widget
                        // Update widget
                        val component = ComponentName(requireContext(), TimetableWidgetProvider::class.java)
                        with(AppWidgetManager.getInstance(requireContext())) {
                            val appWidgetIds = getAppWidgetIds(component)
                            notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview_appwidget)
                        }
                    }
                }
            }
        }

        // Observe database change then update the UI.
        courseViewerViewModel.courseByWeekday.observe(viewLifecycleOwner) {
            // Set no class message.
            binding.linearLayoutCourseViewerEmptyMsg.visibility =
                if (it.isNullOrEmpty()) View.VISIBLE else View.GONE

            // Submit course list to adapter.
            it.let { courseCardAdapter.submitList(it) }
        }
    }

    companion object {
        private const val TAG = "CourseViewerFragment"
        fun newInstance(weekday: Int) = CourseViewerFragment().apply {
            arguments = Bundle().apply {
                putInt(WHICH_WEEKDAY, weekday)
            }
        }
    }
}