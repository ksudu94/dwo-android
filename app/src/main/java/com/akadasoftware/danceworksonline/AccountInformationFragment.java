package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.School;
import com.akadasoftware.danceworksonline.classes.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
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
 * Use the {@link AccountInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountInformationFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    private AppPreferences _appPrefs;
    Account account;
    School school;
    User user;
    Activity activity;
    ArrayList<Account> accounts;
    String status, expdate, cctype, ccnum, SOAP_ACTION, METHOD_NAME;
    int acctid;

    Button btnEditAccount, btnSave, btnCancel, btnSaveCard, btnEditCreditCard, btnCancelSaveCreditCard, btnCancelNewCreditCard;
    EditText etFirst, etLast, etAddress, etCity, etState, etZip, etPhone, etEmail, etCC, etcard,
            etFirstCC, etLastCC, etNewCC, etNewCCExp, etAddressCC, etCityCC, etStateCC, etZipCC;
    TextView tvcc;
    ViewFlipper accountSwitcher;
    Spinner AccountStatusSpinner;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AccountInformationFragment newInstance(int position) {
        AccountInformationFragment fragment = new AccountInformationFragment();
        Bundle args = new Bundle();
        args.putInt("Position", position);
        fragment.setArguments(args);

        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();

        _appPrefs = new AppPreferences(activity);
        accounts = _appPrefs.getAccounts();
        int position = getArguments().getInt("Position");

        account = accounts.get(position);
        acctid = account.AcctID;
        _appPrefs.saveAcctID(account.AcctID);
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
        ccnum = "";
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
        etFirstCC = (EditText) rootView.findViewById(R.id.etFirstCC);
        etLastCC = (EditText) rootView.findViewById(R.id.etLastCC);
        etNewCC = (EditText) rootView.findViewById(R.id.etNewCC);
        etNewCCExp = (EditText) rootView.findViewById(R.id.etNewCCExp);
        etAddressCC = (EditText) rootView.findViewById(R.id.etAddressCC);
        etCityCC = (EditText) rootView.findViewById(R.id.etCityCC);
        etStateCC = (EditText) rootView.findViewById(R.id.etStateCC);
        etZipCC = (EditText) rootView.findViewById(R.id.etZipCC);
        btnEditAccount = (Button) rootView.findViewById(R.id.btnEditAccount);
        btnEditCreditCard = (Button) rootView.findViewById(R.id.btnEditCreditCard);

        btnSave = (Button) rootView.findViewById(R.id.btnSave);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);

        btnSaveCard = (Button) rootView.findViewById(R.id.btnSaveCard);
        btnCancelSaveCreditCard = (Button) rootView.findViewById(R.id.btnCancelSaveCreditCard);

        accountSwitcher = (ViewFlipper) rootView.findViewById(R.id.accountSwitcher);

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

        tvcc = (TextView) rootView.findViewById(R.id.tvccfield);
        etcard = (EditText) rootView.findViewById(R.id.etcard);
        btnSaveCard = (Button) rootView.findViewById(R.id.btnSaveCard);


        tvcc.setVisibility(View.VISIBLE);
        etcard.setVisibility(View.VISIBLE);
        btnSaveCard.setVisibility(View.VISIBLE);
        btnEditCreditCard.setVisibility(View.VISIBLE);
        btnCancelSaveCreditCard.setVisibility(View.VISIBLE);


        //No credit card saved on file
        if (account.CCConsentID == 0) {
            tvcc.setVisibility(View.GONE);
            btnEditCreditCard.setVisibility(View.GONE);


        } else {
            etcard.setVisibility(View.GONE);
            tvcc.setText(account.CCFName + " " + account.CCLName + " - ...." + ccnum + " - Exp. " + expdate);

        }


        /**
         * Update screen to show changes as well!
         */

        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    accountSwitcher.setDisplayedChild(1);
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
        /**
         * Saves changes to account information. Returns you back to main page
         */
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveAccountChanges save = new saveAccountChanges();
                    save.execute();
                    accountSwitcher.setDisplayedChild(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         * Cancel changes to account information. Returns you back to main page
         */

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    accountSwitcher.setDisplayedChild(0);
                    Toast toast = Toast.makeText(getActivity(), "Changes Canceled", Toast.LENGTH_LONG);
                    toast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        btnEditCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountSwitcher.setDisplayedChild(2);
                etFirstCC.setText(account.FName);
                etLastCC.setText(account.LName);
                etAddressCC.setText(account.CCAddress);
                etCityCC.setText(account.CCCity);
                etStateCC.setText(account.CCState);
                etZipCC.setText(account.CCZip);


            }
        });

        /**
         * Figure out how to save credit card to webservice later
         */
        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String creditCard = etNewCC.getText().toString();

                //No blank fields
                if (creditCard.trim().length() == 0) {
                    Toast toast = Toast.makeText(activity, "No Credit Card entered", Toast.LENGTH_LONG);
                    toast.show();

                } else {
                    //Amex, needs to be length 15
                    if (creditCard.charAt(0) == '3') {
                        if (creditCard.length() != 15) {
                            Toast toast = Toast.makeText(activity, "Invalid Credit Card #", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        //All other card lengths are 16
                    } else {
                        if (creditCard.length() != 16) {
                            Toast toast = Toast.makeText(activity, "Invalid Credit Card #", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                    saveCreditCardChanges changes = new saveCreditCardChanges();
                    changes.execute();


                }
                /*
                accountSwitcher.setDisplayedChild(0);
                etcard.setVisibility(View.GONE);
                tvcc.setVisibility(View.VISIBLE);
                btnEditCreditCard.setVisibility(View.VISIBLE);
                tvcc.setText(account.CCFName + " " + account.CCLName + " - ...." + ccnum + " - Exp. " + expdate);*/

            }
        });
        /**
         * Cancels edit of credit card informatoin. Takes you back to main paige.
         */
        btnCancelSaveCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountSwitcher.setDisplayedChild(0);
                Toast toast = Toast.makeText(getActivity(), "Changes Canceled", Toast.LENGTH_LONG);
                toast.show();
                etcard.setVisibility(View.GONE);
                tvcc.setVisibility(View.VISIBLE);
                btnEditCreditCard.setVisibility(View.VISIBLE);
                tvcc.setText(account.CCFName + " " + account.CCLName + " - ...." + ccnum + " - Exp. " + expdate);

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

    public class saveCreditCardChanges extends AsyncTask<Data, Void, SoapPrimitive> {


        @Override
        protected SoapPrimitive doInBackground(Data... data) {
            publishProgress();
            return saveCreditCard();
        }


        protected void onPostExecute(SoapPrimitive result) {

            int response = Integer.valueOf(result.toString());
            if (response > 0) {
                account.CCConsentID = response;
                Toast toast = Toast.makeText(getActivity(), "Save successful", Toast.LENGTH_LONG);
                toast.show();
                accountSwitcher.setDisplayedChild(0);
            } else {

                Toast toast = Toast.makeText(getActivity(), "Credit Card could not be saved.", Toast.LENGTH_LONG);
                toast.show();
            }

            //getAccount getAccount = new getAccount();
            //getAccount.execute();
        }
    }

    public SoapPrimitive saveCreditCard() {
        SOAP_ACTION = "saveCreditCard";
        METHOD_NAME = "saveCreditCard";

        SoapObject Request = new SoapObject(Data.NAMESPACE, METHOD_NAME);

        school = _appPrefs.getSchool();
        user = _appPrefs.getUser();

        PropertyInfo userid = new PropertyInfo();
        userid.setName("UserID");
        userid.setValue(user.UserID);
        Request.addProperty(userid);

        PropertyInfo userguid = new PropertyInfo();
        userguid.setName("UserGUID");
        userguid.setValue(user.UserGUID);
        Request.addProperty(userguid);

        PropertyInfo acctid = new PropertyInfo();
        acctid.setName("AcctID");
        acctid.setValue(account.AcctID);
        Request.addProperty(acctid);

        PropertyInfo ccuser = new PropertyInfo();
        ccuser.setName("ccuser");
        ccuser.setValue(school.CCUserName);
        Request.addProperty(ccuser);

        PropertyInfo ccpass = new PropertyInfo();
        ccpass.setName("ccpass");
        ccpass.setValue(school.CCPassword);
        Request.addProperty(ccpass);

        PropertyInfo cardnumber = new PropertyInfo();
        cardnumber.setName("CardNumber");
        cardnumber.setValue(etNewCC.getText().toString().trim());
        Request.addProperty(cardnumber);

        PropertyInfo cardexpire = new PropertyInfo();
        cardexpire.setName("CardExpire");
        cardexpire.setValue(etNewCCExp.getText().toString().trim());
        Request.addProperty(cardexpire);

        PropertyInfo first = new PropertyInfo();
        first.setName("FirstName");
        first.setValue(etFirstCC.getText().toString().trim());
        Request.addProperty(first);

        PropertyInfo last = new PropertyInfo();
        last.setName("LastName");
        last.setValue(etLastCC.getText().toString().trim());
        Request.addProperty(last);

        String address;
        if (etAddressCC.getText().toString().trim().equals(""))
            address = account.Address;
        else
            address = etAddressCC.getText().toString().trim();
        PropertyInfo Address = new PropertyInfo();
        Address.setName("Address");
        Address.setValue(address);
        Request.addProperty(Address);

        String city;
        if (etCityCC.getText().toString().trim().equals(""))
            city = account.City;
        else
            city = etCityCC.getText().toString().trim();
        PropertyInfo City = new PropertyInfo();
        City.setName("City");
        City.setValue(city);
        Request.addProperty(City);

        String state;
        if (etStateCC.getText().toString().trim().equals(""))
            state = account.State;
        else
            state = etStateCC.getText().toString().trim();
        PropertyInfo State = new PropertyInfo();
        State.setName("State");
        State.setValue(state);
        Request.addProperty(State);

        String zip;
        if (etZipCC.getText().toString().trim().equals(""))
            zip = account.ZipCode;
        else
            zip = etZipCC.getText().toString().trim();
        PropertyInfo ZipCode = new PropertyInfo();
        ZipCode.setName("ZipCode");
        ZipCode.setValue(zip);
        Request.addProperty(ZipCode);

        PropertyInfo maxamount = new PropertyInfo();
        maxamount.setName("MaxAmount");
        maxamount.setType(Float.class);
        maxamount.setValue(school.CCMaxAmt);
        Request.addProperty(maxamount);

        PropertyInfo merchantnumber = new PropertyInfo();
        merchantnumber.setName("MerchantNumber");
        merchantnumber.setValue(school.CCMerchantNo);
        Request.addProperty(merchantnumber);

        PropertyInfo username = new PropertyInfo();
        username.setName("UserName");
        username.setValue(user.UserName);
        Request.addProperty(username);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        MarshalFloat mf = new MarshalFloat();
        mf.register(envelope);

        envelope.dotNet = true;
        envelope.setOutputSoapObject(Request);

        SoapPrimitive response = null;
        HttpTransportSE HttpTransport = new HttpTransportSE(Data.URL);
        try {
            HttpTransport.call(SOAP_ACTION, envelope);

            response = (SoapPrimitive) envelope.getResponse();

        } catch (Exception e) {

            e.printStackTrace();
        }
        return response;
    }

}
