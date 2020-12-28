package com.txwstudio.app.timetable.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.adapter.*
import com.txwstudio.app.timetable.databinding.ActivityMain2Binding
import kotlinx.android.synthetic.main.activity_main2.*
import java.util.*

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private val mainActivity2ViewModel: MainActivity2ViewModel by viewModels()

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)

//        mainActivity2ViewModel = ViewModelProvider(this).get(MainActivity2ViewModel::class.java)
        binding.viewModel = mainActivity2ViewModel

        setupToolBar()
        setupTabLayoutAndViewPager()
        openTodayTimetable()
        subscribeUi()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuAdd -> {
                val autoWeekday: Int = viewPager.currentItem
                val intent = Intent(this, CourseAddActivity::class.java).apply {
                    putExtra("autoWeekday", autoWeekday)
                }
                startActivity(intent)
                return true
            }
            R.id.menuMap -> {
                startActivity(Intent().setClass(this, CampusMapActivity::class.java))
                return true
            }
            R.id.menuCalendar -> {
                gotoCalendar()
                return true
            }
            R.id.menuSettings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar_mainActivity)
    }

    private fun setupTabLayoutAndViewPager() {
        val tabLayout: TabLayout = findViewById(R.id.tabLayout_mainActivity)
        viewPager = findViewById(R.id.viewPager_mainActivity)

        viewPager.adapter = CourseViewerPagerAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
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

    /**
     * Automatically open today's timetable.
     * TODO(Weekend Support)
     * */
    private fun openTodayTimetable() {
        // Get day of the week, start from SUNDAY, int == 1.
        // then open belongs today's tab.
        val c = Calendar.getInstance()
        val date = c[Calendar.DAY_OF_WEEK]
        viewPager.setCurrentItem(if (date == 1) 6 else date - 2, false)
    }

    private fun subscribeUi() {
        val fab: FloatingActionButton = findViewById(R.id.fab_mainActivity)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun gotoCalendar() {
        // TODO(Fix Old PreferenceManager)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val pdfPath = prefs.getString("schoolCalendarPath", "")
        val uri = Uri.parse(pdfPath)

        val target = Intent(Intent.ACTION_VIEW)
        target.setDataAndType(uri, "application/pdf")
        target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val intent = Intent.createChooser(target, java.lang.String.valueOf(R.string.pdfOpenWithMsg))
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.pdfNoAppMsg, Toast.LENGTH_LONG).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, R.string.pdfFileNotFound, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.fileReadErrorMsg, Toast.LENGTH_LONG).show()
        }
    }
}