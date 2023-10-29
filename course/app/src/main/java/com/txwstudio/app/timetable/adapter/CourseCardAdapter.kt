package com.txwstudio.app.timetable.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.txwstudio.app.timetable.data.Course3
import com.txwstudio.app.timetable.data.CourseCardAction
import com.txwstudio.app.timetable.databinding.DialogCourseCardActionBinding
import com.txwstudio.app.timetable.databinding.RowCourseCardBinding

class CourseCardAdapter(
    private val onItemClicked: (CourseCardAction, Course3) -> Unit
) :
    ListAdapter<Course3, CourseCardViewHolder>(CourseCardComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseCardViewHolder {
        val binding =
            RowCourseCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseCardViewHolder(binding) { action, course3 ->
            onItemClicked(action, course3)
        }
    }

    override fun onBindViewHolder(holder: CourseCardViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }
}

class CourseCardViewHolder(
    private val binding: RowCourseCardBinding,
    private val onItemClicked: (CourseCardAction, Course3) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var _item: Course3

    init {
        binding.courseCard.setOnLongClickListener {
            val dialogLayout =
                DialogCourseCardActionBinding.inflate(
                    LayoutInflater.from(binding.root.context),
                    binding.root,
                    false
                )

            val actionDialog = MaterialAlertDialogBuilder(binding.root.context).apply {
                setView(dialogLayout.root)
            }.show()

            dialogLayout.textViewDeleteTitle.setOnClickListener {
                actionDialog.dismiss()
                sendDeleteAction()
            }
            dialogLayout.textViewEditTitle.setOnClickListener {
                actionDialog.dismiss()
                sendEditAction()
            }

            true
        }
    }

    fun bind(item: Course3) {
        this._item = item
        item.courseStartTime?.replace("..(?!$)".toRegex(), "$0:").let {
            binding.textViewBeginTime.text = it
        }
        item.courseEndTime?.replace("..(?!$)".toRegex(), "$0:").let {
            binding.textViewEndTime.text = it
        }
        binding.textViewCourseName.text = item.courseName
        binding.textViewCourseLocation.text = item.coursePlace
        binding.textViewProfName.text =
            if (item.profName.isNullOrBlank()) "" else ", ${item.profName}"
    }

    private fun sendDeleteAction() {
        onItemClicked(CourseCardAction.DELETE, _item)
    }

    private fun sendEditAction() {
        onItemClicked(CourseCardAction.EDIT, _item)
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