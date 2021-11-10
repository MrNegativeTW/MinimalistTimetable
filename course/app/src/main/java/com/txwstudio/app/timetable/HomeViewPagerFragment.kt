package com.txwstudio.app.timetable

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.txwstudio.app.timetable.adapter.CourseViewerPagerAdapter
import com.txwstudio.app.timetable.databinding.FragmentHomeViewPagerBinding
import com.txwstudio.app.timetable.ui.preferences.*
import com.txwstudio.app.timetable.utilities.DATA_TYPE_CALENDAR
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val FAB_ACTION_DEFAULT = "-1"
private const val FAB_ACTION_OPEN_MAP = "1"
private const val FAB_ACTION_OPEN_CALENDAR = "2"
private const val THREE_MINUTES_IN_MILLIS = 1800000

/**
 * A simple [Fragment] subclass.
 * Use the [HomeViewPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeViewPagerFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHomeViewPagerBinding

    private lateinit var sharedPref: SharedPreferences
    private lateinit var prefTableTitle: String
    private var prefWeekendCol = false
    private var prefWeekdayLengthLong = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)

        // Set toolbar and options menu
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarHomeFrag)
        setHasOptionsMenu(true)

        // Listen for preference change.
        sharedPref.registerOnSharedPreferenceChangeListener(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPrefValue()
        updateFabActionAndImage()
        updateToolbarTitle()
        setupTabLayoutAndViewPager()
        openTodayTimetable()
    }

    override fun onResume() {
        super.onResume()
        openTodayAfterIdleFor3Minutes()
    }

    override fun onPause() {
        super.onPause()
        saveLastTimeUsedTimestamp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedPref.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuAdd -> {
                val a =
                    HomeViewPagerFragmentDirections.actionHomeViewPagerFragmentToCourseEditorFragment(
                        currentViewPagerItem = binding.viewPagerHomeFrag.currentItem
                    )
                findNavController().navigate(a)
                true
            }
            R.id.menuMap -> {
                openMapsViewer()
                true
            }
            R.id.menuCalendar -> {
                openCalendar()
                true
            }
            R.id.menuSettings -> {
                val a =
                    HomeViewPagerFragmentDirections.actionHomeViewPagerFragmentToPreferenceActivity()
                findNavController().navigate(a)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Detect preference change to update UI. Called everytime when shared preference changed.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Get updated preference value
        getPrefValue()
        when (key) {
            PREFERENCE_FAB_ACTION -> {
                updateFabActionAndImage()
            }
            PREFERENCE_TABLE_TITLE -> {
                updateToolbarTitle()
            }
            PREFERENCE_WEEKEND_COL, PREFERENCE_WEEKDAY_LENGTH_LONG -> {
                setupTabLayoutAndViewPager()
                openTodayTimetable()
            }
        }
    }

    /**
     * Get the latest preference value.
     * */
    private fun getPrefValue() {
        prefTableTitle = sharedPref.getString(
            PREFERENCE_TABLE_TITLE,
            getString(R.string.settings_timetableTitleDefaultValue)
        )!!
        prefWeekendCol = sharedPref.getBoolean(PREFERENCE_WEEKEND_COL, false)
        prefWeekdayLengthLong = sharedPref.getBoolean(PREFERENCE_WEEKDAY_LENGTH_LONG, false)
    }

    /**
     * Setting up fab, one thing it does very well is to close your app.
     */
    private fun updateFabActionAndImage() {
        val userFabAction = sharedPref.getString(PREFERENCE_FAB_ACTION, FAB_ACTION_DEFAULT)

        // Set fab drawable
        val drawable = when (userFabAction) {
            FAB_ACTION_DEFAULT -> R.drawable.ic_exit_24dp
            FAB_ACTION_OPEN_MAP -> R.drawable.ic_map_24dp
            FAB_ACTION_OPEN_CALENDAR -> R.drawable.ic_event_note_24dp
            else -> R.drawable.ic_exit_24dp
        }
        binding.fabHomeFrag.setImageResource(drawable)

        // Set fab onClick action
        binding.fabHomeFrag.setOnClickListener {
            when (userFabAction) {
                FAB_ACTION_DEFAULT -> requireActivity().finish()
                FAB_ACTION_OPEN_MAP -> openMapsViewer()
                FAB_ACTION_OPEN_CALENDAR -> openCalendar()
            }
        }
    }

    /**
     * Update toolbar's title.
     * */
    private fun updateToolbarTitle() {
        (activity as AppCompatActivity).supportActionBar!!.title = prefTableTitle
    }

    /**
     * Set adapter for view pager and bind tab layout to it.
     * */
    private fun setupTabLayoutAndViewPager() {
        binding.viewPagerHomeFrag.adapter = CourseViewerPagerAdapter(this, prefWeekendCol)

        // Set the icon and text for each tab
        TabLayoutMediator(binding.tabLayoutHomeFrag, binding.viewPagerHomeFrag) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    /**
     * Get weekdays string as Mon or Monday base on user's preference.
     * */
    private fun getTabTitle(position: Int): String? {
        val array = if (prefWeekdayLengthLong) R.array.weekdayList else R.array.weekdayListShort
        return resources.getStringArray(array)[position]
    }

    /**
     * Automatically open today's timetable.
     * Get day of the week, start from SUNDAY (int == 1), then open the tab belongs today.
     * */
    private fun openTodayTimetable() {
        val dayOfWeek = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
        binding.viewPagerHomeFrag.setCurrentItem(if (dayOfWeek == 1) 8 else dayOfWeek - 2, false)
    }

    /**
     * Compare last time used timestamp and current timestamp, if greater than 3 minutes,
     * open today's timetable.
     */
    private fun openTodayAfterIdleFor3Minutes() {
        val currentTimeStamp = Calendar.getInstance().timeInMillis
        val lastTimUse = sharedPref.getLong(PREFERENCE_LAST_TIME_USE, 0)
        val idleTime = currentTimeStamp - lastTimUse
        if (idleTime > THREE_MINUTES_IN_MILLIS) {
            openTodayTimetable()
        }
    }

    /**
     * Called when onPause() to record the last timestamp.
     */
    private fun saveLastTimeUsedTimestamp() {
        val currentTimeStamp = Calendar.getInstance().timeInMillis
        sharedPref.edit().putLong(PREFERENCE_LAST_TIME_USE, currentTimeStamp).apply()
    }

    /**
     * Open MapsViewer fragment.
     */
    private fun openMapsViewer() {
        val a = HomeViewPagerFragmentDirections.actionHomeViewPagerFragmentToMapsViewerFragment()
        findNavController().navigate(a)
    }

    /**
     * Create an intent chooser to open calendar.
     * */
    private fun openCalendar() {
        val calendarPath = sharedPref.getString(PREFERENCE_CALENDAR_PATH, "")!!
        val uri = Uri.parse(calendarPath)

        val target = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, DATA_TYPE_CALENDAR)
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val intent = Intent.createChooser(target, java.lang.String.valueOf(R.string.pdfOpenWithMsg))

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Suitable app not found.
            Snackbar.make(requireView(), R.string.pdfNoAppMsg, Snackbar.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            // File not found.
            Snackbar.make(requireView(), R.string.pdfFileNotFound, Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Unknown exception.
            Snackbar.make(requireView(), R.string.fileReadErrorMsg, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeViewPagerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeViewPagerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}