package com.txwstudio.app.timetable.ui.courseviewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.txwstudio.app.timetable.Adapter
import com.txwstudio.app.timetable.DBHandler
import com.txwstudio.app.timetable.databinding.FragmentCourseViewerBinding

private const val WHICH_WEEKDAY = "WHICH_WEEKDAY"

class CourseViewerFragment : Fragment() {

    companion object {
        fun newInstance(weekday: Int) = CourseViewerFragment().apply {
            arguments = Bundle().apply {
                putInt(WHICH_WEEKDAY, weekday)
            }
        }
    }

    private var weekday: Int? = null
    private var db: DBHandler? = null

    private lateinit var binding: FragmentCourseViewerBinding
    private val viewModel: CourseViewerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            weekday = it.getInt(WHICH_WEEKDAY)
        }

        db = DBHandler(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCourseViewerBinding.inflate(inflater, container, false)

        val adapter = Adapter(requireActivity(), db!!.getCourse(weekday!!), weekday!!)
        binding.recyclerViewCourseViewer.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: Use the ViewModel
    }

}