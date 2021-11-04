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
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_CALENDAR_PATH
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_TABLE_TITLE
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_WEEKDAY_LENGTH_LONG
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_WEEKEND_COL
import com.txwstudio.app.timetable.utilities.DATA_TYPE_CALENDAR
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeViewPagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeViewPagerFragment : Fragment() {
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

        // Fab, one thing it does very well is to close your app.
        binding.fabHomeFrag.setOnClickListener { requireActivity().finish() }

        return binding.root
    }

    /**
     * TODO: Fix wired UX, it opens today's timetable when onResume.
     * Bad experience when after added the course.
     * */
    override fun onResume() {
        super.onResume()
        getPrefValue()
        setupToolBar()
        setupTabLayoutAndViewPager()
        openTodayTimetable()
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
                return true
            }
            R.id.menuMap -> {
                val a =
                    HomeViewPagerFragmentDirections.actionHomeViewPagerFragmentToMapsViewerFragment()
                findNavController().navigate(a)
                return true
            }
            R.id.menuCalendar -> {
                openCalendar()
                return true
            }
            R.id.menuSettings -> {
                val a =
                    HomeViewPagerFragmentDirections.actionHomeViewPagerFragmentToPreferenceActivity()
                findNavController().navigate(a)
                /**
                 * Maybe use startActivityForResult
                 * {@link #getPrefValue}
                 * */
//                startActivity(Intent(requireContext(), PreferenceActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Get preference value in order to decide what UI should to be present.
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
     * Set support action and it's title, also enable options menu.
     * */
    private fun setupToolBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarHomeFrag)
        (activity as AppCompatActivity).supportActionBar!!.title = prefTableTitle

        setHasOptionsMenu(true)
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
        val c = Calendar.getInstance()
        val date = c[Calendar.DAY_OF_WEEK]
        binding.viewPagerHomeFrag.setCurrentItem(if (date == 1) 8 else date - 2, false)
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