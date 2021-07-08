package com.txwstudio.app.timetable

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.txwstudio.app.timetable.adapter.CourseViewerPagerAdapter
import com.txwstudio.app.timetable.databinding.FragmentHomeViewPagerBinding
import com.txwstudio.app.timetable.ui.activity.CampusMapActivity
import com.txwstudio.app.timetable.ui.activity.PreferenceActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeViewPagerBinding.inflate(inflater, container, false)
        val tabLayout = binding.tabLayoutHomeFrag
        val viewPager = binding.viewPagerHomeFrag

        viewPager.adapter = CourseViewerPagerAdapter(this, false)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        setupToolBar()
        openTodayTimetable()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuAdd -> {
                Log.i("TESTTT", "menuAdd")
                binding.coordinatorLayout.fitsSystemWindows = false
                val a =
                    HomeViewPagerFragmentDirections.actionHomeViewPagerFragmentToCourseEditorFragment()
                findNavController().navigate(a)
                return true
            }
            R.id.menuMap -> {
                startActivity(Intent().setClass(requireContext(), CampusMapActivity::class.java))
                return true
            }
            R.id.menuCalendar -> {
                Log.i("TESTTT", "menuCalendar")
                return true
            }
            R.id.menuSettings -> {
                startActivity(Intent(requireContext(), PreferenceActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getTabTitle(position: Int): String? {
        val array = R.array.weekdayList
        return resources.getStringArray(array)[position]
    }

    private fun setupToolBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarHomeFrag)
        (activity as AppCompatActivity).supportActionBar!!.title = "Title"
        setHasOptionsMenu(true)
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