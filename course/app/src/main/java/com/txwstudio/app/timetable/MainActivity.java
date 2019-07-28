package com.txwstudio.app.timetable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.Calendar;

import com.txwstudio.app.timetable.Fragments.Frag1;
import com.txwstudio.app.timetable.Fragments.Frag2;
import com.txwstudio.app.timetable.Fragments.Frag3;
import com.txwstudio.app.timetable.Fragments.Frag4;
import com.txwstudio.app.timetable.Fragments.Frag5;
import static com.txwstudio.app.timetable.SettingsFragment.restartSchedule;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private SharedPreferences sharedPref;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initPrefManager();
        setupTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupFragments();
        setupExitButton();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (restartSchedule) {
            restartSchedule = false;
            this.finish();
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
        }
        toolbar.setTitle(sharedPref.getString("tableTitle_Pref", String.valueOf(R.string.tableTitleDefault)));
    }


    private void initPrefManager() {
        PreferenceManager.setDefaultValues(this,  R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    }


    private void setupTheme() {
        Boolean lightMode = sharedPref.getBoolean("lightMode_Pref", false);
        setTheme(lightMode ? R.style.LightTheme_NoActionBar : R.style.AppTheme_NoActionBar);
    }


    /**
     * Handle which tab to open based on which day it is.
     * */
    private void setupFragments() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // Get day of the week, start from SUNDAY, int == 1.
        // then open belongs today's tab.
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DAY_OF_WEEK);
        mViewPager.setCurrentItem(date == 1 ? 6 : date-2, false);
    }


    /**
     * Handle exit button action
     *
     * onClick: Exit app.
     * onLongClick: Open map.
     * */
    private void setupExitButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SchoolMap.class);
                startActivity(intent);
                return false;
            }
        });
    } // .setupExitButton()


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /** Handle Menu Item Selected. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuAdd) {
            Intent intent = new Intent(this, CourseAddActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.menuSettings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    } // .onOptionsItemSelected


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position){
                case 0:
                    fragment = new Frag1();
                    break;
                case 1:
                    fragment = new Frag2();
                    break;
                case 2:
                    fragment = new Frag3();
                    break;
                case 3:
                    fragment = new Frag4();
                    break;
                case 4:
                    fragment = new Frag5();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

}
