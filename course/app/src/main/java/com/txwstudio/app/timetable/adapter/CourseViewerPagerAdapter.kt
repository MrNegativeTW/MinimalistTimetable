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

class CourseViewerPagerAdapter(fragment: Fragment, enableWeekend: Boolean) :
        FragmentStateAdapter(fragment) {

    private val enableWeekends = enableWeekend

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: MutableMap<Int, () -> Fragment> by lazy {
        createMap()
    }

    private val weekdayFragments: MutableMap<Int, () -> Fragment> = mutableMapOf(
            WEEKDAY_1 to { CourseViewerFragment.newInstance(WEEKDAY_1) },
            WEEKDAY_2 to { CourseViewerFragment.newInstance(WEEKDAY_2) },
            WEEKDAY_3 to { CourseViewerFragment.newInstance(WEEKDAY_3) },
            WEEKDAY_4 to { CourseViewerFragment.newInstance(WEEKDAY_4) },
            WEEKDAY_5 to { CourseViewerFragment.newInstance(WEEKDAY_5) }
    )

    private val weekendFragments: MutableMap<Int, () -> Fragment> = mutableMapOf(
            WEEKDAY_6 to { CourseViewerFragment.newInstance(WEEKDAY_6) },
            WEEKDAY_7 to { CourseViewerFragment.newInstance(WEEKDAY_7) }
    )

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

    override fun getItemCount(): Int {
        return tabFragmentsCreators.size
    }

    private fun createMap(): MutableMap<Int, () -> Fragment> {
        return if (enableWeekends) {
            weekdayFragments.putAll(weekendFragments)
            weekdayFragments
        } else {
            weekdayFragments
        }
    }
}