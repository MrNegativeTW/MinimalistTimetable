package com.txwstudio.app.timetable.ui.courseviewer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.adapter.CourseCardAdapter
import com.txwstudio.app.timetable.databinding.FragmentCourseViewerBinding
import com.txwstudio.app.timetable.utilities.INTENT_TIMETABLE_CHANGED
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
        binding = FragmentCourseViewerBinding.inflate(inflater, container, false).apply {
            viewModel = courseViewerViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        val courseCardAdapter = CourseCardAdapter(courseViewerViewModel)
        binding.recyclerViewCourseViewer.adapter = courseCardAdapter

        subscribeUi(courseCardAdapter)
        return binding.root
    }

    private fun subscribeUi(courseCardAdapter: CourseCardAdapter) {
        // Display "delete success" message when the course got deleted.
        // From: CodingInFlow
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            courseViewerViewModel.courseViewerEvent.collect { event ->
                when (event) {
                    is CourseViewerViewModel.CourseViewerEvent.ShowUndoDeleteMessage -> {
                        // Show message
                        Snackbar.make(requireView(), R.string.dialogDeleted, Snackbar.LENGTH_LONG)
                            .show()

                        // Update widget
                        val intent =
                            Intent(requireContext(), TimetableWidgetProvider::class.java).apply {
                                action = INTENT_TIMETABLE_CHANGED
                            }
                        requireContext().sendBroadcast(intent)
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
        fun newInstance(weekday: Int) = CourseViewerFragment().apply {
            arguments = Bundle().apply {
                putInt(WHICH_WEEKDAY, weekday)
            }
        }
    }
}