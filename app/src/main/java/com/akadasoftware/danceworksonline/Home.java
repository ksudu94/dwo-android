package com.akadasoftware.danceworksonline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AppPreferences;

import java.util.ArrayList;

public class Home extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        AccountListFragment.OnAccountSelectedListener,
        StudentsListFragment.OnStudentInteractionListener,
        FilterDialog.FilterDialogListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private static SharedPreferences loginPreferences;
    private static SharedPreferences.Editor loginEditor;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private AppPreferences _appPrefs;


    private CharSequence mTitle;
    private onFilterSelectedDialog fListener;

    public interface onFilterSelectedDialog {
        // TODO: Update argument type and name
        public void onFilterSelectedDialog();
    }


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     * Called to do initial creation of a fragment. This is called after onAttach(Activity) and
     * before onCreateView(LayoutInflater, ViewGroup, Bundle).Note that this can be called while
     * the fragment's activity is still in the process of being created. As such, you can not rely
     * on things like the activity's content view hierarchy being initialized at this point.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        _appPrefs = new AppPreferences(getApplicationContext());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.home:
                //Overrides to function to open nav drawer instead of going to specificed home
                //page. Done by enabling bar.setDisplayHomeAsUpEnabled(true);
                mNavigationDrawerFragment.openNavigationDrawer();
                break;
            case R.id.log_out:
                loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                loginEditor = loginPreferences.edit();
                loginEditor.clear();
                loginEditor.commit();
                try {
                    Toast.makeText(getApplicationContext(), "Logging out.", Toast.LENGTH_SHORT).show();
                    Class ourClass = Class.forName("com.akadasoftware.danceworksonline.Login");
                    Intent ourIntent = new Intent(Home.this, ourClass);
                    startActivity(ourIntent);
                } catch (Exception e) {

                }
                break;
            case R.id.profile:
                return true;
            case R.id.filter:
                onFilterSelectedDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Based on position of item selected in nav drawer, create the corresponding listFragment
     * and load into Home container. This is where you choose whether you have a tablet or a phone
     * and load or don't load a detail screen at the same time.
     */

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 0:
                //load AccountList fragment
                mTitle = getString(R.string.title_accounts);
                break;
            case 1:
                //load StudentList fragment
                mTitle = getString(R.string.title_students);
                break;
            case 2:
                //load ClassList fragment
                mTitle = getString(R.string.title_classes);
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_accounts);
                break;
            case 2:
                mTitle = getString(R.string.title_students);

            case 3:
                mTitle = getString(R.string.title_classes);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void OnAccountSelected(int id) {

        /**
         *  Starting a new activity (AccountInformation) which is handled by intents in the manifest.
         *  The position is saved to fill a matching account in the listarray.
         */


        _appPrefs.saveAccountListPosition(id);


        Intent openMainPage = new Intent("com.akadasoftware.danceworksonline.AccountInformation");
        startActivity(openMainPage);


    }

    @Override
    public void onStudentInteraction(int position) {


        _appPrefs.saveStudentListPosition(position);


    }

    public void onFilterSelectedDialog() {

        FragmentManager fm = getSupportFragmentManager();
        FilterDialog filterDialog = new FilterDialog();

        filterDialog.show(fm, "");


    }


    @Override
    public void onFilterDialogPositiveClick() {
        /**
         * Clears the saved Accounts List so that it refreshes it with the new select and sort values
         */
        ArrayList<Account> AccountsArray = new ArrayList<Account>();
        _appPrefs.saveAccounts(AccountsArray);

        Fragment newFragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        newFragment = new AccountListFragment();
        ft.replace(R.id.container, newFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onFilterDialogNegativeClick() {

    }

}


