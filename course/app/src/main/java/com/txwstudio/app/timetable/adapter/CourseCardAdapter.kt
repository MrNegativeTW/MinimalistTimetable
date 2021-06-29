package com.txwstudio.app.timetable.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.data.Course3
import com.txwstudio.app.timetable.data.CourseRepository
import com.txwstudio.app.timetable.databinding.RowCourseCardBinding
import com.txwstudio.app.timetable.viewmodels.CourseCardViewModel

class CourseCardAdapter(private val repository: CourseRepository) :
    ListAdapter<Course3, CourseCardAdapter.ViewHolder>(CourseCardComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RowCourseCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    inner class ViewHolder(
        private val binding: RowCourseCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.courseCard.setOnLongClickListener {
                MaterialAlertDialogBuilder(binding.root.context).apply {
                    setTitle(R.string.courseCardDialog_title)
                    setMessage(R.string.courseCardDialog_message)
                    setNeutralButton(R.string.courseCardDialog_deleteCourse) {_, _ ->
                        deleteCourse()
                    }
                    setPositiveButton(R.string.courseCardDialog_editCourseInfo) {_, _ ->
                        editCourse()
                    }
                    show()
                }
                true
            }
        }

        fun bind(item: Course3) {
            binding.apply {
                viewModel = CourseCardViewModel(item)
                executePendingBindings()
            }
        }

        private fun deleteCourse() {
            Log.i("TESTTT", "So you want to delete this course")
        }

        private fun editCourse() {
            Log.i("TESTTT", "Perform edit course")
        }
    }

    class CourseCardComparator : DiffUtil.ItemCallback<Course3>() {
        override fun areItemsTheSame(oldItem: Course3, newItem: Course3): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course3, newItem: Course3): Boolean {
            return oldItem.courseName == newItem.courseName
        }
    }
}