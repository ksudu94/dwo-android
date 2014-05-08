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
import com.akadasoftware.danceworksonline.classes.Globals;
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
    Account oAccount;
    School oSchool;
    User oUser;
    Globals globals;
    Activity activity;
    ArrayList<Account> arrayListAccounts;
    String strStatus, strExpDate, strCCType, strCCNum, SOAP_ACTION, METHOD_NAME;
    int AcctID, position;

    Button btnEditAccount, btnSave, btnCancel, btnSaveCard, btnEditCreditCard, btnCancelSaveCreditCard, btnAddCreditCard;
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
        globals = new Globals();
        _appPrefs = new AppPreferences(activity);
        arrayListAccounts = _appPrefs.getAccounts();
        position = getArguments().getInt("Position");

        oAccount = arrayListAccounts.get(position);
        AcctID = oAccount.AcctID;
        _appPrefs.saveAcctID(oAccount.AcctID);
    }


    // Used to create an instance of this fragment
    public AccountInformationFragment() {
    }

    //Before anything to run, the view is created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_information, container, false);

        switch (oAccount.Status) {
            case 0:
                strStatus = "active";
                break;
            case 1:
                strStatus = "inactive";
                break;
            case 2:
                strStatus = "prospect";
                break;
            case 3:
                strStatus = "deleted";
                break;
            default:
                strStatus = "I have no freaking clue";
                break;
        }

        switch (oAccount.CCType) {
            case 1:
                strCCType = "amex";
                break;
            case 2:
                strCCType = "disc";
                break;
            case 3:
                strCCType = "mc";
                break;
            case 4:
                strCCType = "visa";
                break;
            default:
                strCCType = "";
                break;
        }
        strCCNum = "";
        if (oAccount.CCTrail.equals(""))
            strCCNum = " ";
        else {
            for (int j = 4; j > 0; j--) {
                strCCNum += oAccount.CCTrail.charAt(oAccount.CCTrail.length() - j);
            }
        }
        //Used to make exp date a string so we can use substring to put a / in b/w
        String test = oAccount.CCExpire.toString();
        if (test.equals("")) {
            strExpDate = "";
        } else {
            strExpDate = test.substring(0, 2) + "/" + test.substring(2, test.length() - 1);
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
        btnAddCreditCard = (Button) rootView.findViewById(R.id.btnAddCreditCard);

        btnSave = (Button) rootView.findViewById(R.id.btnSave);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);

        btnSaveCard = (Button) rootView.findViewById(R.id.btnSaveCard);
        btnCancelSaveCreditCard = (Button) rootView.findViewById(R.id.btnCancelSaveCreditCard);

        accountSwitcher = (ViewFlipper) rootView.findViewById(R.id.accountSwitcher);

        TextView acctname = (TextView) rootView.findViewById(R.id.tvacctnamefield);
        acctname.setText(oAccount.FName + " " + oAccount.LName);

        TextView acctaddress = (TextView) rootView.findViewById(R.id.tvacctaddressfield);
        acctaddress.setText(oAccount.Address + ", " + oAccount.City + ", " + oAccount.State + " " + oAccount.ZipCode);

        TextView acctphone = (TextView) rootView.findViewById(R.id.tvacctphonefield);
        acctphone.setText(oAccount.Phone);

        TextView tvemail = (TextView) rootView.findViewById(R.id.tvacctemailfield);
        tvemail.setText(oAccount.EMail);

        TextView tvstatus = (TextView) rootView.findViewById(R.id.tvstatusfield);
        tvstatus.setText(strStatus);

        TextView type = (TextView) rootView.findViewById(R.id.tvcc);
        if (strCCType.length() > 0)
            type.setText(strCCType + ": ");

        tvcc = (TextView) rootView.findViewById(R.id.tvccfield);
        btnSaveCard = (Button) rootView.findViewById(R.id.btnSaveCard);


        tvcc.setVisibility(View.VISIBLE);
        btnSaveCard.setVisibility(View.VISIBLE);
        btnEditCreditCard.setVisibility(View.VISIBLE);
        btnAddCreditCard.setVisibility(View.VISIBLE);
        btnCancelSaveCreditCard.setVisibility(View.VISIBLE);


        //No credit card saved on file
        if (oAccount.CCConsentID == 0) {
            tvcc.setVisibility(View.GONE);
            btnEditCreditCard.setVisibility(View.GONE);


        } else {
            tvcc.setText(oAccount.CCFName + " " + oAccount.CCLName + " - ...." + strCCNum + " - Exp. " + strExpDate);

        }


        /**
         * Update screen to show changes as well!
         */

        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    accountSwitcher.setDisplayedChild(1);
                    etFirst.setText(oAccount.FName);
                    etLast.setText(oAccount.LName);
                    etAddress.setText(oAccount.Address);
                    etAddress.setText(oAccount.City);
                    etAddress.setText(oAccount.State);
                    etAddress.setText(oAccount.ZipCode);
                    etPhone.setText(oAccount.Phone);
                    etEmail.setText(oAccount.EMail);

                    List<String> spinnerlist = new ArrayList<String>();
                    spinnerlist.add("active");
                    spinnerlist.add("inactive");
                    spinnerlist.add("deleted");
                    if (strStatus.equals("prospect"))
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
                etFirstCC.setText(oAccount.FName);
                etLastCC.setText(oAccount.LName);
                etAddressCC.setText(oAccount.CCAddress);
                etCityCC.setText(oAccount.CCCity);
                etStateCC.setText(oAccount.CCState);
                etZipCC.setText(oAccount.CCZip);


            }
        });

        btnAddCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountSwitcher.setDisplayedChild(2);
                etFirstCC.setText(oAccount.FName);
                etLastCC.setText(oAccount.LName);
                etAddressCC.setText(oAccount.CCAddress);
                etCityCC.setText(oAccount.CCCity);
                etStateCC.setText(oAccount.CCState);
                etZipCC.setText(oAccount.CCZip);


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
                    saveCreditCardChangesAsync saveCreditCardChanges = new saveCreditCardChangesAsync();
                    saveCreditCardChanges.execute();

                }
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
                tvcc.setVisibility(View.VISIBLE);
                btnEditCreditCard.setVisibility(View.VISIBLE);
                btnAddCreditCard.setVisibility(View.GONE);
                tvcc.setText(oAccount.CCFName + " " + oAccount.CCLName + " - ...." + strCCNum + " - Exp. " + strExpDate);

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
            globals.updateAccount(oAccount, position, activity);
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

        AcctID = _appPrefs.getAcctID();

        PropertyInfo piAcctID = new PropertyInfo();
        piAcctID.setName("AcctID");
        piAcctID.setValue(AcctID);
        Request.addProperty(piAcctID);

        PropertyInfo piFName = new PropertyInfo();
        piFName.setName("FName");
        piFName.setValue(etFirst.getText().toString());
        Request.addProperty(piFName);

        PropertyInfo piLName = new PropertyInfo();
        piLName.setName("LName");
        piLName.setValue(etLast.getText().toString());
        Request.addProperty(piLName);

        String strAddress;
        if (etAddress.getText().toString().trim().equals(""))
            strAddress = oAccount.Address;
        else
            strAddress = etAddress.getText().toString().trim();
        PropertyInfo piAddress = new PropertyInfo();
        piAddress.setName("Address");
        piAddress.setValue(strAddress);
        Request.addProperty(piAddress);

        String strCity;
        if (etCity.getText().toString().trim().equals(""))
            strCity = oAccount.City;
        else
            strCity = etCity.getText().toString().trim();
        PropertyInfo piCity = new PropertyInfo();
        piCity.setName("City");
        piCity.setValue(strCity);
        Request.addProperty(piCity);

        String strState;
        if (etState.getText().toString().trim().equals(""))
            strState = oAccount.State;
        else
            strState = etState.getText().toString().trim();
        PropertyInfo piState = new PropertyInfo();
        piState.setName("State");
        piState.setValue(strState);
        Request.addProperty(piState);

        String strZip;
        if (etZip.getText().toString().trim().equals(""))
            strZip = oAccount.ZipCode;
        else
            strZip = etZip.getText().toString().trim();

        PropertyInfo piZip = new PropertyInfo();
        piZip.setName("ZipCode");
        piZip.setValue(strZip);
        Request.addProperty(piZip);

        String strPhone;
        if (etPhone.getText().toString().trim().equals(""))
            strPhone = oAccount.Phone;
        else
            strPhone = etEmail.getText().toString().trim();
        PropertyInfo piPhone = new PropertyInfo();
        piPhone.setName("Phone");
        piPhone.setValue(strPhone);
        Request.addProperty(piPhone);

        String strEmail;
        if (etEmail.getText().toString().trim().equals(""))
            strEmail = oAccount.EMail;
        else
            strEmail = etEmail.getText().toString().trim();
        PropertyInfo piEmail = new PropertyInfo();
        piEmail.setName("EMail");
        piEmail.setValue(strEmail);
        Request.addProperty(piEmail);

        PropertyInfo piCheckName = new PropertyInfo();
        piCheckName.setName("checkName");
        piCheckName.setValue(true);
        Request.addProperty(piCheckName);


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

    public class saveCreditCardChangesAsync extends AsyncTask<Data, Void, SoapPrimitive> {


        @Override
        protected SoapPrimitive doInBackground(Data... data) {
            publishProgress();

            return saveCreditCard();
        }


        protected void onPostExecute(SoapPrimitive result) {

            int response = Integer.valueOf(result.toString());
            if (response > 0) {
                oAccount.CCConsentID = response;
                globals.updateAccount(oAccount, position, activity);
                Toast toast = Toast.makeText(getActivity(), "Save successful", Toast.LENGTH_LONG);
                toast.show();
                accountSwitcher.setDisplayedChild(0);
            } else {

                Toast toast = Toast.makeText(getActivity(), "Credit Card could not be saved.", Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

    public SoapPrimitive saveCreditCard() {
        SOAP_ACTION = "saveCreditCard";
        METHOD_NAME = "saveCreditCard";

        SoapObject Request = new SoapObject(Data.NAMESPACE, METHOD_NAME);

        oSchool = _appPrefs.getSchool();
        oUser = _appPrefs.getUser();

        PropertyInfo piUserID = new PropertyInfo();
        piUserID.setName("UserID");
        piUserID.setValue(oUser.UserID);
        Request.addProperty(piUserID);

        PropertyInfo piUserGUID = new PropertyInfo();
        piUserGUID.setName("UserGUID");
        piUserGUID.setValue(oUser.UserGUID);
        Request.addProperty(piUserGUID);

        PropertyInfo piAcctID = new PropertyInfo();
        piAcctID.setName("AcctID");
        piAcctID.setValue(oAccount.AcctID);
        Request.addProperty(piAcctID);

        PropertyInfo piCCUser = new PropertyInfo();
        piCCUser.setName("ccuser");
        piCCUser.setValue(oSchool.CCUserName);
        Request.addProperty(piCCUser);

        PropertyInfo piCCPass = new PropertyInfo();
        piCCPass.setName("ccpass");
        piCCPass.setValue(oSchool.CCPassword);
        Request.addProperty(piCCPass);

        PropertyInfo piCardNumber = new PropertyInfo();
        piCardNumber.setName("CardNumber");
        piCardNumber.setValue(etNewCC.getText().toString().trim());
        Request.addProperty(piCardNumber);

        PropertyInfo piCardExpire = new PropertyInfo();
        piCardExpire.setName("CardExpire");
        piCardExpire.setValue(etNewCCExp.getText().toString().trim());
        Request.addProperty(piCardExpire);

        PropertyInfo piFName = new PropertyInfo();
        piFName.setName("FirstName");
        piFName.setValue(etFirstCC.getText().toString().trim());
        Request.addProperty(piFName);

        PropertyInfo piLName = new PropertyInfo();
        piLName.setName("LastName");
        piLName.setValue(etLastCC.getText().toString().trim());
        Request.addProperty(piLName);

        String strAddress;
        if (etAddressCC.getText().toString().trim().equals(""))
            strAddress = oAccount.Address;
        else
            strAddress = etAddressCC.getText().toString().trim();
        PropertyInfo piAddress = new PropertyInfo();
        piAddress.setName("Address");
        piAddress.setValue(strAddress);
        Request.addProperty(piAddress);

        String strCity;
        if (etCityCC.getText().toString().trim().equals(""))
            strCity = oAccount.City;
        else
            strCity = etCityCC.getText().toString().trim();
        PropertyInfo piCity = new PropertyInfo();
        piCity.setName("City");
        piCity.setValue(strCity);
        Request.addProperty(piCity);

        String strState;
        if (etStateCC.getText().toString().trim().equals(""))
            strState = oAccount.State;
        else
            strState = etStateCC.getText().toString().trim();
        PropertyInfo piState = new PropertyInfo();
        piState.setName("State");
        piState.setValue(strState);
        Request.addProperty(piState);

        String strZip;
        if (etZipCC.getText().toString().trim().equals(""))
            strZip = oAccount.ZipCode;
        else
            strZip = etZipCC.getText().toString().trim();
        PropertyInfo piZipCode = new PropertyInfo();
        piZipCode.setName("ZipCode");
        piZipCode.setValue(strZip);
        Request.addProperty(piZipCode);

        PropertyInfo piMaxAmount = new PropertyInfo();
        piMaxAmount.setName("MaxAmount");
        piMaxAmount.setType(Float.class);
        piMaxAmount.setValue(oSchool.CCMaxAmt);
        Request.addProperty(piMaxAmount);

        PropertyInfo piMerchantNumber = new PropertyInfo();
        piMerchantNumber.setName("MerchantNumber");
        piMerchantNumber.setValue(oSchool.CCMerchantNo);
        Request.addProperty(piMerchantNumber);

        PropertyInfo piUserName = new PropertyInfo();
        piUserName.setName("UserName");
        piUserName.setValue(oUser.UserName);
        Request.addProperty(piUserName);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        MarshalFloat mf = new MarshalFloat();
        mf.register(envelope);

        envelope.dotNet = true;
        envelope.setOutputSoapObject(Request);

        SoapPrimitive responseToSaveCreditCard = null;
        HttpTransportSE HttpTransport = new HttpTransportSE(Data.URL);
        try {
            HttpTransport.call(SOAP_ACTION, envelope);

            responseToSaveCreditCard = (SoapPrimitive) envelope.getResponse();

        } catch (Exception e) {

            e.printStackTrace();
        }
        return responseToSaveCreditCard;
    }

}
