package com.txwstudio.app.timetable.ui.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.adapter.*
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setupToolBar()

        val tabLayout: TabLayout = findViewById(R.id.tabLayout_mainActivity)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager_mainActivity)

        viewPager.adapter = CourseViewerPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        val fab: FloatingActionButton = findViewById(R.id.fab_mainActivity)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_mainActivity)
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            WEEKDAY_1 -> getString(R.string.tab_text_1)
            WEEKDAY_2 -> getString(R.string.tab_text_2)
            WEEKDAY_3 -> getString(R.string.tab_text_3)
            WEEKDAY_4 -> getString(R.string.tab_text_4)
            WEEKDAY_5 -> getString(R.string.tab_text_5)
            else -> "null"
        }
    }
}