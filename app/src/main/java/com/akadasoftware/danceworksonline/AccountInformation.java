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

import java.text.NumberFormat;
import java.util.Locale;

public class AccountInformation extends ActionBarActivity implements ActionBar.TabListener, AccountTransactionsFragment.OnTransactionSelected, AccountStudentsFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AccountPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private AppPreferences _appPrefs;

    /*Uses the saved position from the onAccountSelected method in Home.java to fill an empty
     account with the matching position in the accountlist array.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);

        _appPrefs = new AppPreferences(getApplicationContext());

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new AccountPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        //mViewPager.setOffscreenPageLimit(5);
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
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        int position = tab.getPosition();
        mViewPager.setCurrentItem(position);

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onFragmentInteraction(String id) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class AccountPagerAdapter extends FragmentPagerAdapter {

        public AccountPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        //Handles the tabs and which fragments fill them
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a newFragment that is defined based on the switch case

            Fragment newFragment;


            switch (position) {
                case 0:
                    newFragment = AccountInformationFragment.newInstance(_appPrefs.getAccountListPosition());
                    break;
                case 1:
                    newFragment = AccountStudentsFragment.newInstance(_appPrefs.getAccountListPosition());
                    break;
                case 2:
                    newFragment = AccountTransactionsFragment.newInstance(_appPrefs.getAccountListPosition());
                    break;
                case 3:
                    newFragment = EnterPaymentFragment.newInstance(_appPrefs.getAccountListPosition(), "", Float.parseFloat("0.00"));
                    break;
                case 4:
                    newFragment = EnterChargeFragment.newInstance(_appPrefs.getAccountListPosition());
                    break;
                default:
                    newFragment = AccountInformationFragment.newInstance(_appPrefs.getAccountListPosition());
                    break;
            }

            return newFragment;
        }

        @Override
        public int getCount() {
            // Show x number of pages.
            return 5;
        }

        //Tab titles
        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_account_info).toUpperCase(l);
                case 1:
                    return getString(R.string.title_account_students).toUpperCase(l);
                case 2:
                    return getString(R.string.title_account_transactions).toUpperCase(l);
                case 3:
                    return getString(R.string.title_account_payment).toUpperCase(l);
                case 4:
                    return getString(R.string.title_account_charge).toUpperCase(l);
                default:
                    return "";
            }
        }

    }

    //Interface created on the Transaction page.. Is handled in the on click of a list item
    // passes in balance and TID from the listview and then does appropriate actions with them
    public void OnTransactionSelected(float balance, int TID) {

        //Change to whatever activity I want to start.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setSelectedNavigationItem(3);

        NumberFormat format = NumberFormat.getCurrencyInstance();
        EnterPaymentFragment pf = (EnterPaymentFragment) mSectionsPagerAdapter.instantiateItem(mViewPager, 3);
        pf.etAmount.setText(String.valueOf(format.format(balance)));
        _appPrefs.saveChgID(TID);
        pf.etDescription.setText(String.valueOf(_appPrefs.getChgID()));


    }


}
