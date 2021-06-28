package com.txwstudio.app.timetable.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.adapter.CourseViewerPagerAdapter
import com.txwstudio.app.timetable.databinding.ActivityMain2Binding
import com.txwstudio.app.timetable.ui.courseeditor.CourseEditorActivity
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_NAME_CALENDAR_REQUEST
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_TABLE_TITLE
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_WEEKDAY_LENGTH_LONG
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_WEEKEND_COL
import java.util.*

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private val mainActivity2ViewModel: MainActivity2ViewModel by viewModels()

    private lateinit var sharedPref: SharedPreferences
    private lateinit var prefTableTitle: String
    private var prefWeekendCol = false
    private var prefWeekdayLengthLong = false
    private lateinit var prefCalendarPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)

        binding.viewModel = mainActivity2ViewModel
        binding.lifecycleOwner = this

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        setupToolBar()
        subscribeUi()
        MobileAds.initialize(this)
    }

    override fun onResume() {
        super.onResume()
        getPrefValue()
        setupTabLayoutAndViewPager()
        openTodayTimetable()
        supportActionBar?.title = prefTableTitle
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuAdd -> {
                val currentViewPagerItem: Int = binding.viewPagerMainActivity2.currentItem
                val intent = Intent(this, CourseEditorActivity::class.java).apply {
                    putExtra("currentViewPagerItem", currentViewPagerItem)
                }
                startActivity(intent)
//                CourseEditorFragment().show(supportFragmentManager, "")
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
                startActivity(Intent(this, PreferenceActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getPrefValue() {
        prefTableTitle = sharedPref.getString(PREFERENCE_TABLE_TITLE,
                getString(R.string.settings_timetableTitleDefaultValue))!!
        prefWeekendCol = sharedPref.getBoolean(PREFERENCE_WEEKEND_COL, false)
        prefWeekdayLengthLong = sharedPref.getBoolean(PREFERENCE_WEEKDAY_LENGTH_LONG, false)
        prefCalendarPath = sharedPref.getString(PREFERENCE_NAME_CALENDAR_REQUEST, "")!!
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.toolbarMainActivity2)
    }

    private fun setupTabLayoutAndViewPager() {
        binding.viewPagerMainActivity2.adapter = CourseViewerPagerAdapter(this, prefWeekendCol)

        TabLayoutMediator(binding.tabLayoutMainActivity2, binding.viewPagerMainActivity2) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun getTabTitle(position: Int): String? {
        val array = if (prefWeekdayLengthLong) {
            R.array.weekdayList
        } else {
            R.array.weekdayListShort
        }
        return resources.getStringArray(array)[position]
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
        binding.viewPagerMainActivity2.setCurrentItem(if (date == 1) 8 else date - 2, false)
    }

    private fun subscribeUi() {
        binding.fabMainActivity2.setOnClickListener { view ->
            finish()
        }
    }

    private fun gotoCalendar() {
        val uri = Uri.parse(prefCalendarPath)

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