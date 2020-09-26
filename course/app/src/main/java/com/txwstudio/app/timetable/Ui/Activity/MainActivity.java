package com.txwstudio.app.timetable.Ui.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Calendar;

import com.txwstudio.app.timetable.R;
import com.txwstudio.app.timetable.Ui.Fragments.Frag1;
import com.txwstudio.app.timetable.Ui.Fragments.Frag2;
import com.txwstudio.app.timetable.Ui.Fragments.Frag3;
import com.txwstudio.app.timetable.Ui.Fragments.Frag4;
import com.txwstudio.app.timetable.Ui.Fragments.Frag5;
import static com.txwstudio.app.timetable.Ui.Fragments.SettingsFragment.restartSchedule;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
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
            Intent refresh = getIntent();
            startActivity(refresh);
        }
        toolbar.setTitle(sharedPref.getString("tableTitle_Pref", String.valueOf(R.string.tableTitleDefault)));
    }


    private void initPrefManager() {
        PreferenceManager.setDefaultValues(this,  R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    }


    private void setupTheme() {
        setTheme(sharedPref.getBoolean("lightMode_Pref", false) ?
                    R.style.LightTheme_NoActionBar : R.style.AppTheme_NoActionBar);
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
                intent.setClass(MainActivity.this, CampusMapActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }


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
            /* Get the current day and set it as default when adding the course. */
            int autoWeekday = mViewPager.getCurrentItem();

            Intent intent = new Intent(this, CourseAddActivity.class);
            intent.putExtra("autoWeekday", autoWeekday);
            startActivity(intent);
            return true;

        } else if (id == R.id.menuMap) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, CampusMapActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.menuCalendar) {
            gotoCalendar();
            return true;

        } else if (id == R.id.menuSettings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void gotoCalendar() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String pdfPath = prefs.getString("schoolCalendarPath", "");
        Uri uri = Uri.parse(pdfPath);

        /**Old method, remove soon*/
//            File file = new File(pdfPath);
//            Uri uri = FileProvider.getUriForFile(MainActivity.this,
//                    BuildConfig.APPLICATION_ID + ".provider", file);

        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(uri,"application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = Intent.createChooser(target, String.valueOf(R.string.pdfOpenWithMsg));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.pdfNoAppMsg, Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            Toast.makeText(this, R.string.pdfFileNotFound, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.fileReadErrorMsg, Toast.LENGTH_LONG).show();
        }
    }


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
