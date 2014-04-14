package com.akadasoftware.danceworksonline;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.akadasoftware.danceworksonline.classes.AppPreferences;

import java.util.Locale;

/**
 * Created by Kyle on 4/9/2014.
 */
public class StudentInformation extends ActionBarActivity implements ActionBar.TabListener,
        StudentClassFragment.OnStudentClassListener,
        StudentWaitListFragment.OnWaitListListener {

    ViewPager mViewPager;
    private AppPreferences _appPrefs;
    StudentPagerAdapter mSectionsPagerAdapter;

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        int position = tab.getPosition();
        mViewPager.setCurrentItem(position, true);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }


    /**
     * Uses the saved position from the onAccountSelected method in Home.java to fill an empty
     * account with the matching position in the account list array.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_information);

        _appPrefs = new AppPreferences(getApplicationContext());

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new StudentPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter. setOFfScreenPageLimit handles the number
        // of tabs that are preloaded
        mViewPager = (ViewPager) findViewById(R.id.pager);
        //How many adjacent pages it loads
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        int pageCount = mSectionsPagerAdapter.getCount();
        for (int i = 0; i < pageCount; i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student_informatoin, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }


    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class StudentPagerAdapter extends FragmentPagerAdapter {

        public StudentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //Handles the tabs and which fragments fill them
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a newFragment that is defined based on the switch case

            Fragment newFragment;

            int listPosition = _appPrefs.getStudentListPosition();


            switch (position) {
                case 0:
                    newFragment = StudentInformationFragment.newInstance(listPosition);
                    break;
                case 1:
                    newFragment = StudentClassFragment.newInstance(listPosition);
                    break;
                case 2:
                    newFragment = StudentWaitListFragment.newInstance(listPosition);
                    break;
                default:
                    newFragment = StudentClassFragment.newInstance(listPosition);
                    break;
            }

            return newFragment;
        }

        @Override
        public int getCount() {
            return 5;
        }

        //Tab titles
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_student_info).toUpperCase(l);
                case 1:
                    return getString(R.string.title_student_classes).toUpperCase(l);
                case 2:
                    return getString(R.string.title_student_waitlist).toUpperCase(l);
                case 3:
                    return getString(R.string.title_student_Attendance).toUpperCase(l);
                case 4:
                    return getString(R.string.title_student_enroll).toUpperCase(l);
                default:
                    return "";
            }
        }
    }

    public void onStudentClassInteraction(String id) {
    }

    @Override
    public void onWaitListInteraction(String id) {

    }

}
