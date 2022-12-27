package com.txwstudio.app.timetable.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.data.Course3
import com.txwstudio.app.timetable.data.CourseCardAction
import com.txwstudio.app.timetable.databinding.RowCourseCardBinding
import com.txwstudio.app.timetable.ui.courseviewer.CourseViewerViewModel
import com.txwstudio.app.timetable.viewmodels.CourseCardViewModel

class CourseCardAdapter(
    private val viewModel: CourseViewerViewModel,
    private val onItemClicked: (CourseCardAction, Int) -> Unit
) :
    ListAdapter<Course3, CourseCardAdapter.ViewHolder>(CourseCardComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RowCourseCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) { action, id ->
            onItemClicked(action, id)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class ViewHolder(
        private val rowCourseCardBinding: RowCourseCardBinding,
        onItemClicked: (CourseCardAction, Int) -> Unit
    ) : RecyclerView.ViewHolder(rowCourseCardBinding.root) {

        init {
            rowCourseCardBinding.courseCard.setOnLongClickListener {
                MaterialAlertDialogBuilder(rowCourseCardBinding.root.context).apply {
                    setTitle(R.string.courseCardDialog_title)
                    setMessage(R.string.courseCardDialog_message)
                    setNeutralButton(R.string.courseCardDialog_deleteCourse) { _, _ ->
                        // Call CourseViewerViewModel.deleteCourse to delete the course from db.
                        rowCourseCardBinding.viewModel?.course?.let { viewModel.deleteCourse(it) }
                    }
                    setPositiveButton(R.string.courseCardDialog_editCourseInfo) { _, _ ->
                        rowCourseCardBinding.viewModel?.id?.let {
                            onItemClicked(CourseCardAction.EDIT, it)
                        }
                    }
                    show()
                }
                true
            }
        }

        fun bind(item: Course3) {
            rowCourseCardBinding.apply {
                viewModel = CourseCardViewModel(item)
                executePendingBindings()
            }
        }
    }
}

class CourseCardComparator : DiffUtil.ItemCallback<Course3>() {
    override fun areItemsTheSame(oldItem: Course3, newItem: Course3): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Course3, newItem: Course3): Boolean {
        return oldItem == newItem
    }
}