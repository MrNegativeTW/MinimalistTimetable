package com.txwstudio.app.timetable.adapter

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.txwstudio.app.timetable.databinding.RowCourseCardBinding

class CourseCardAdapter : RecyclerView.Adapter<CourseCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(
            private val binding: RowCourseCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.courseCard.setOnLongClickListener {
                Log.d("TESTTT", "test")
                true
            }
        }

    }
}