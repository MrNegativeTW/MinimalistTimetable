package com.txwstudio.app.timetable.ui.courseviewer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.txwstudio.app.timetable.MyApplication
import com.txwstudio.app.timetable.adapter.CourseCardAdapter
import com.txwstudio.app.timetable.databinding.FragmentCourseViewerBinding
import com.txwstudio.app.timetable.ui.courseeditor.CourseEditorActivity
import com.txwstudio.app.timetable.utilities.INTENT_EXTRA_COURSE_ID

private const val WHICH_WEEKDAY = "WHICH_WEEKDAY"

class CourseViewerFragment : Fragment() {

    companion object {
        fun newInstance(weekday: Int) = CourseViewerFragment().apply {
            arguments = Bundle().apply {
                putInt(WHICH_WEEKDAY, weekday)
            }
        }
    }

    private lateinit var binding: FragmentCourseViewerBinding

    private val courseViewerViewModel: CourseViewerViewModel by viewModels {
        CourseViewerViewModelFactory(
            (requireActivity().application as MyApplication).courseRepository,
            weekday!!
        )
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * Because of lifecycle, the code below will not work inside onViewCreated,
     * therefore I move these into onStart, just like the old one.
     * */
    override fun onStart() {
        super.onStart()
    }

    private fun subscribeUi(courseCardAdapter: CourseCardAdapter) {
        courseViewerViewModel.courseByWeekday.observe(viewLifecycleOwner) {
            it?.let { courseCardAdapter.submitList(it) }
        }

        // Observe LiveData in viewModel, once it changed,
        // open CourseEditorActivity to edit course info.
        courseViewerViewModel.targetCourseIdToEdit.observe(viewLifecycleOwner) {
            if (it != -1) {
                val intent = Intent(requireActivity(), CourseEditorActivity::class.java)
                intent.putExtra(INTENT_EXTRA_COURSE_ID, it)
                startActivity(intent)
            }
        }
    }

}