package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AppPreferences;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountInformationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountInformationFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private final String ARG_SECTION_NUMBER = "section_number";
    ViewPager mViewPager;
    private AppPreferences _appPrefs;
    Account account;
    Activity activity;
    ArrayList<Account> accounts;
    String status, expdate, cctype, SOAP_ACTION, METHOD_NAME;
    int acctid;

    Button btnEdit, btnSave, btnSaveCard, btnEditCreditCard;
    EditText etFirst, etLast, etAddress, etCity, etState, etZip, etPhone, etEmail, etCC, etcard;
    ViewSwitcher accountSwitcher;
    Spinner AccountStatusSpinner;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        _appPrefs = new AppPreferences(activity);
        accounts = _appPrefs.getAccounts();

        account = accounts.get(_appPrefs.getAccountListPosition());
        acctid = account.AcctID;
        _appPrefs.saveAcctID(account.AcctID);
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
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
                ccnum += account.CCTrail.charAt(account.CCTrail.length() - j);
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
        btnEditCreditCard = (Button) rootView.findViewById(R.id.btnEditCreditCard);
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
        if (cctype.length() > 0)
            type.setText(cctype + ": ");

        TextView tvcc = (TextView) rootView.findViewById(R.id.tvccfield);
        etcard = (EditText) rootView.findViewById(R.id.etcard);
        btnSaveCard = (Button) rootView.findViewById(R.id.btnSaveCard);


        tvcc.setVisibility(View.VISIBLE);
        etcard.setVisibility(View.VISIBLE);
        btnSaveCard.setVisibility(View.VISIBLE);
        btnEditCreditCard.setVisibility(View.VISIBLE);
        if (account.CCConsentID == 0) {
            tvcc.setVisibility(View.GONE);
            btnEditCreditCard.setVisibility(View.GONE);
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

                    List<String> spinnerlist = new ArrayList<String>();
                    spinnerlist.add("active");
                    spinnerlist.add("inactive");
                    spinnerlist.add("deleted");
                    if (status.equals("prospect"))
                        spinnerlist.add("prospect");

                    AccountStatusSpinner = (Spinner) activity.findViewById(R.id.AccountStatusSpinner);
                    ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, spinnerlist);

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
}
