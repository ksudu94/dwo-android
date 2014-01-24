package com.akadasoftware.danceworksonline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.akadasoftware.danceworksonline.classes.AppPreferences;

public class Home extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, AccountListFragment.OnAccountSelectedListener {

    /**
     *
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private static SharedPreferences loginPreferences;
    private static SharedPreferences.Editor loginEditor;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private AppPreferences _appPrefs;


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

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
                break;
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

        /* Starting a new activity (AccountInformation) which is handled by intents in the manifest.
        The position is saved to fill a matching account in the listarray.
         */

        _appPrefs = new AppPreferences(getApplicationContext());

        _appPrefs.saveAccountListPosition(id);
        Intent openMainPage = new Intent("com.akadasoftware.danceworksonline.AccountInformation");
        startActivity(openMainPage);


    }

}


