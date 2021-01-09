package com.txwstudio.app.timetable.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.txwstudio.app.timetable.ui.courseviewer.CourseViewerFragment

const val WEEKDAY_1 = 0
const val WEEKDAY_2 = 1
const val WEEKDAY_3 = 2
const val WEEKDAY_4 = 3
const val WEEKDAY_5 = 4
const val WEEKDAY_6 = 5
const val WEEKDAY_7 = 6

class CourseViewerPagerAdapter(fragment: FragmentActivity, enableWeekend: Boolean) :
        FragmentStateAdapter(fragment) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
            WEEKDAY_1 to { CourseViewerFragment.newInstance(WEEKDAY_1) },
            WEEKDAY_2 to { CourseViewerFragment.newInstance(WEEKDAY_2) },
            WEEKDAY_3 to { CourseViewerFragment.newInstance(WEEKDAY_3) },
            WEEKDAY_4 to { CourseViewerFragment.newInstance(WEEKDAY_4) },
            WEEKDAY_5 to { CourseViewerFragment.newInstance(WEEKDAY_5) }
    )

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }
}