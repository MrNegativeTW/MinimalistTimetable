package com.example.admin.course;

// For Dialog Interface
import android.content.DialogInterface;
// For Internet Connection
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

// For Alert Dialog
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
// For Get Date
import java.util.Calendar;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set theme from splash screen to Main theme.
        // Also set android:theme="@style/AppTheme.NoActionBar" in AndroidManifest.xml
        //super.setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        // Get weekday
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DAY_OF_WEEK);
        // Open tab by which weekday it is, DEFAULT = Monday.
        switch (date){
            case Calendar.MONDAY:
                mViewPager.setCurrentItem(0);
                break;
            case Calendar.TUESDAY:
                mViewPager.setCurrentItem(1);
                break;
            case Calendar.WEDNESDAY:
                mViewPager.setCurrentItem(2);
                break;
            case Calendar.THURSDAY:
                mViewPager.setCurrentItem(3);
                break;
            case Calendar.FRIDAY:
                mViewPager.setCurrentItem(4);
                break;
            case Calendar.SATURDAY:
                mViewPager.setCurrentItem(0);
                break;
            case Calendar.SUNDAY:
                mViewPager.setCurrentItem(0);
                break;
        }

        // Exit Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAndRemoveTask();
                /*
                ---Original Code---
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Menu Item Selected Action
        if (id == R.id.menuAbout) {
            AlertDialog.Builder aboutBox = new AlertDialog.Builder(this);
            // Show About Message, get string from string.xml
            aboutBox.setTitle(R.string.aboutTitle);
            aboutBox.setMessage(R.string.aboutMessage);
            // A OK button do nothing.
            aboutBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                }
            });
            // A Github button link to this project's Repo.
            aboutBox.setNeutralButton("GitHub", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MrNegativeTW/simpleCourseTable")));
                }
            });
            aboutBox.show();
            return true;

        } else if (id == R.id.menuLogin) {
            AlertDialog.Builder loginFeature = new AlertDialog.Builder(this);
            // Show Login Message, get string from string.xml
            loginFeature.setTitle(R.string.loginInfo);
            loginFeature.setMessage("On the road...");
            loginFeature.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
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
            // Show 3 total pages.
            return 5;
        }
    }



}
