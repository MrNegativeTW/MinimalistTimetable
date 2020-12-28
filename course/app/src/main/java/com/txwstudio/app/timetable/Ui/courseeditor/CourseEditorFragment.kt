package com.txwstudio.app.timetable.ui.courseeditor

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.databinding.FragmentCourseEditorBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [CourseEditorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CourseEditorFragment : BottomSheetDialogFragment() {

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CourseEditorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
                CourseEditorFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                    }
                }
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null

    private lateinit var binding: FragmentCourseEditorBinding
    private val courseEditorViewModel: CourseEditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCourseEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeUi()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }

    private fun subscribeUi() {
        binding.buttonCourseEditorFragClose.setOnClickListener {
            val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireActivity())
            materialAlertDialogBuilder.apply {
                setTitle(R.string.courseEditor_exitConfirmDialogTitle)
                setMessage(R.string.courseEditor_exitConfirmDialogMsg)
                setPositiveButton(R.string.all_confirm) { _, _ ->
                    dismiss()
                }
                setNegativeButton(R.string.all_cancel) { _, _ ->

                }
                show()
            }
        }

        binding.buttonCourseEditorFragSave.setOnClickListener {

        }

        val items = listOf("Material", "Design", "Components", "Android")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.dropDownCourseEditorFrag as? AutoCompleteTextView)?.setAdapter(adapter)
    }


}