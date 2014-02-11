package com.akadasoftware.danceworksonline;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ViewSwitcher;

import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AppPreferences;

import java.util.ArrayList;
import java.util.Locale;

public class AccountInformation extends ActionBarActivity implements ActionBar.TabListener, AccountTransactionsFragment.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private AppPreferences _appPrefs;
    Account account;
    ArrayList<Account> accounts;
    String status, expdate, cctype, SOAP_ACTION, METHOD_NAME;
    int acctid;

    Button btnEdit, btnSave, btnSaveCard;
    EditText etFirst, etLast, etAddress, etCity, etState, etZip, etPhone, etEmail, etCC, etcard;
    ViewSwitcher accountSwitcher;
    Spinner AccountStatusSpinner;


    /*Uses the saved position from the onAccountSelected method in Home.java to fill an empty
     account with the matching position in the accountlist array.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);

        _appPrefs = new AppPreferences(getApplicationContext());
        accounts = _appPrefs.getAccounts();

        account = accounts.get(_appPrefs.getAccountListPosition());
        _appPrefs.saveAcctID(account.AcctID);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });


        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
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
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
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
                    newFragment = new AccountInformationFragment();
                    break;
                case 1:
                    newFragment = new AccountTransactionsFragment();
                    break;
                case 2:
                    newFragment = new AccountTransactionsFragment();
                    break;
                case 3:
                    newFragment = new AccountTransactionsFragment();
                    break;
                case 4:
                    newFragment = new ChargeCodeFragment();
                    break;
                default:
                    newFragment = new AccountInformationFragment();
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
            }
            return null;
        }
    }

    /**
     * A fragment containing a textviews for misc. account informations such as Name and address etc.
     */
    /*public class AccountInformationFragment extends Fragment {
        *//**
     * The fragment argument representing the section number for this
     * fragment.
     *//*
        private final String ARG_SECTION_NUMBER = "section_number";

        */

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     *//*
        public AccountInformationFragment newInstance(int sectionNumber) {
            AccountInformationFragment fragment = new AccountInformationFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        // Used to create an instance of this fragment
        public AccountInformationFragment() {
        }

        //Before anything to run, the view is created
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_account_information, container, false);

            switch (account.Status) {
                case 0:
                    status = "active";
                    break;
                case 1:
                    status = "inactive";
                    break;
                case 2:
                    status = "prospect";
                    break;
                case 3:
                    status = "deleted";
                    break;
                default:
                    status = "I have no freaking clue";
                    break;
            }

            switch (account.CCType) {
                case 1:
                    cctype = "amex";
                    break;
                case 2:
                    cctype = "disc";
                    break;
                case 3:
                    cctype = "mc";
                    break;
                case 4:
                    cctype = "visa";
                    break;
                default:
                    cctype = "";
                    break;
            }
            String ccnum = "";
            if (account.CCTrail.equals(""))
                ccnum = " ";
            else {
                for (int j = 4; j > 0; j--) {
                    ccnum += account.CCTrail.charAt(account.CCTrail.length()-j);
                }
            }
            //Used to make exp date a string so we can use substring to put a / in b/w
            String test = account.CCExpire.toString();
            if (test.equals("")) {
                expdate = "";
            } else {
                expdate = test.substring(0, 2) + "/" + test.substring(2, test.length() - 1);
            }


            etFirst = (EditText) rootView.findViewById(R.id.etFirst);
            etLast = (EditText) rootView.findViewById(R.id.etLast);
            etAddress = (EditText) rootView.findViewById(R.id.etAddress);
            etCity = (EditText) rootView.findViewById(R.id.etCity);
            etState = (EditText) rootView.findViewById(R.id.etState);
            etZip = (EditText) rootView.findViewById(R.id.etZip);
            etPhone = (EditText) rootView.findViewById(R.id.etPhone);
            etEmail = (EditText) rootView.findViewById(R.id.etEmail);
            btnEdit = (Button) rootView.findViewById(R.id.btnEdit);
            btnSave = (Button) rootView.findViewById(R.id.btnSave);
            accountSwitcher = (ViewSwitcher) rootView.findViewById(R.id.accountSwitcher);


            TextView acctname = (TextView) rootView.findViewById(R.id.tvacctnamefield);
            acctname.setText(account.FName + " " + account.LName);

            TextView acctaddress = (TextView) rootView.findViewById(R.id.tvacctaddressfield);
            acctaddress.setText(account.Address + ", " + account.City + ", " + account.State + " " + account.ZipCode);

            TextView acctphone = (TextView) rootView.findViewById(R.id.tvacctphonefield);
            acctphone.setText(account.Phone);

            TextView tvemail = (TextView) rootView.findViewById(R.id.tvacctemailfield);
            tvemail.setText(account.EMail);

            TextView tvstatus = (TextView) rootView.findViewById(R.id.tvstatusfield);
            tvstatus.setText(status);

            TextView type = (TextView) rootView.findViewById(R.id.tvcc);
            if(cctype.length() > 0)
            type.setText(cctype + ": ");

            TextView tvcc = (TextView) rootView.findViewById(R.id.tvccfield);
            etcard = (EditText) rootView.findViewById(R.id.etcard);
            btnSaveCard = (Button) rootView.findViewById(R.id.btnSaveCard);


            tvcc.setVisibility(View.VISIBLE);
            etcard.setVisibility(View.VISIBLE);
            btnSaveCard.setVisibility(View.VISIBLE);
            if (account.CCConsentID == 0) {
                tvcc.setVisibility(View.GONE);

                btnSaveCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

            } else {
                etcard.setVisibility(View.GONE);
                btnSaveCard.setVisibility(View.GONE);
                tvcc.setText(account.CCFName + " " + account.CCLName + " - ...." + ccnum + " - Exp. " + expdate);
            }


            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        accountSwitcher.showNext();
                        etFirst.setText(account.FName);
                        etLast.setText(account.LName);
                        etAddress.setText(account.Address);
                        etAddress.setText(account.City);
                        etAddress.setText(account.State);
                        etAddress.setText(account.ZipCode);
                        etPhone.setText(account.Phone);
                        etEmail.setText(account.EMail);

                        List<String> spinnerlist =new ArrayList<String>();
                        spinnerlist.add("active");
                        spinnerlist.add("inactive");
                        spinnerlist.add("deleted");
                        if(status.equals("prospect"))
                            spinnerlist.add("prospect");

                        AccountStatusSpinner = (Spinner) findViewById(R.id.AccountStatusSpinner);
                        ArrayAdapter<String> spinneradapter= new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_list_item_1,spinnerlist);

                        spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        AccountStatusSpinner.setAdapter(spinneradapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        saveAccountChanges save = new saveAccountChanges();
                        save.execute();
                        accountSwitcher.showNext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return rootView;
        }

        class Data {

            static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
            private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
        }

        public class saveAccountChanges extends AsyncTask<Data, Void, String> {

            ProgressDialog dialog;

            protected void onPreExecute() {
                dialog = new ProgressDialog(getActivity());
                dialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMax(100);
                dialog.show();
            }

            @Override
            protected String doInBackground(Data... data) {
                publishProgress();
                return saveAccountChanges();
            }

            protected void onProgressUpdate(Integer... progress) {
                dialog.incrementProgressBy(progress[0]);
            }

            protected void onPostExecute(String result) {
                dialog.dismiss();

                Toast toast = Toast.makeText(getActivity(), result, Toast.LENGTH_LONG);
                toast.show();
                //getAccount getAccount = new getAccount();
                //getAccount.execute();
            }
        }

        public String saveAccountChanges() {

            SOAP_ACTION = "saveAccountInformation";
            METHOD_NAME = "saveAccountInformation";

            SoapObject Request = new SoapObject(Data.NAMESPACE, METHOD_NAME);

            acctid = _appPrefs.getAcctID();

            PropertyInfo AcctID = new PropertyInfo();
            AcctID.setName("AcctID");
            AcctID.setValue(acctid);
            Request.addProperty(AcctID);

            PropertyInfo first = new PropertyInfo();
            first.setName("FName");
            first.setValue(etFirst.getText().toString());
            Request.addProperty(first);

            PropertyInfo last = new PropertyInfo();
            last.setName("LName");
            last.setValue(etLast.getText().toString());
            Request.addProperty(last);

            String address;
            if (etAddress.getText().toString().trim().equals(""))
                address = account.Address;
            else
                address = etAddress.getText().toString().trim();
            PropertyInfo Address = new PropertyInfo();
            Address.setName("Address");
            Address.setValue(address);
            Request.addProperty(Address);

            String city;
            if (etCity.getText().toString().trim().equals(""))
                city = account.City;
            else
                city = etCity.getText().toString().trim();
            PropertyInfo City = new PropertyInfo();
            City.setName("City");
            City.setValue(city);
            Request.addProperty(City);

            String state;
            if (etState.getText().toString().trim().equals(""))
                state = account.State;
            else
                state = etState.getText().toString().trim();
            PropertyInfo State = new PropertyInfo();
            State.setName("State");
            State.setValue(state);
            Request.addProperty(State);

            String zip;
            if (etZip.getText().toString().trim().equals(""))
                zip = account.ZipCode;
            else
                zip = etZip.getText().toString().trim();
            PropertyInfo ZipCode = new PropertyInfo();
            ZipCode.setName("ZipCode");
            ZipCode.setValue(zip);
            Request.addProperty(ZipCode);

            String phone;
            if (etPhone.getText().toString().trim().equals(""))
                phone = account.Phone;
            else
                phone = etEmail.getText().toString().trim();
            PropertyInfo Phone = new PropertyInfo();
            Phone.setName("Phone");
            Phone.setValue(phone);
            Request.addProperty(Phone);

            String email;
            if (etEmail.getText().toString().trim().equals(""))
                email = account.EMail;
            else
                email = etEmail.getText().toString().trim();
            PropertyInfo emailinfo = new PropertyInfo();
            emailinfo.setName("EMail");
            emailinfo.setValue(email);
            Request.addProperty(emailinfo);

            PropertyInfo checkName = new PropertyInfo();
            checkName.setName("checkName");
            checkName.setValue(true);
            Request.addProperty(checkName);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.dotNet = true;
            envelope.setOutputSoapObject(Request);

            SoapPrimitive response = null;
            HttpTransportSE HttpTransport = new HttpTransportSE(Data.URL);
            try {
                HttpTransport.call(SOAP_ACTION, envelope);

                response = (SoapPrimitive) envelope.getResponse();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                Toast toast = Toast.makeText(getActivity(), e.toString(),
                        Toast.LENGTH_LONG);
                toast.show();
            } catch (XmlPullParserException e) {
                // TODO Auto-generated catch block
                Toast toast = Toast.makeText(getActivity(), e.toString(),
                        Toast.LENGTH_LONG);
                toast.show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Toast toast = Toast.makeText(getActivity(), e.toString(),
                        Toast.LENGTH_LONG);
                toast.show();
            }
            return response.toString();

        }

    }*/
    public void OnFragmentInteraction(int id) {

        /* Starting a new activity (AccountInformation) which is handled by intents in the manifest.
        The position is saved to fill a matching account in the listarray.
         */

        // _appPrefs = new AppPreferences(getApplicationContext());
        // _appPrefs.saveAccountListPosition(id);
        Intent openMainPage = new Intent("com.akadasoftware.danceworksonline.AccountInformation");
        startActivity(openMainPage);


    }


}
