package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.ChargeCodes;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChargeCodeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChargeCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class ChargeCodeFragment extends Fragment {
    private static String ARG_SECTION_NUMBER = "section_number";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    String SOAP_ACTION, METHOD_NAME, userguid;
    int schid, userid;

    private OnFragmentInteractionListener mListener;
    private AppPreferences _appPrefs;
    Account account;
    Activity activity;
    ArrayList<Account> arrayAccounts;
    ArrayList<ChargeCodes> arrayChargeCodes = new ArrayList<ChargeCodes>();
    ChargeCodeAdapter adapterChargeCodes;
    ChargeCodes chargeCode;
    float[] amountArray;

    TextView tvDescription, tvAmount, tvDiscAmount, tvDiscAmountDisplayed, tvTotal, tvTotalDisplayed;
    EditText etDescription, etAmount;
    DatePicker datePicker;
    Spinner chargecodespinner;
    Button btnCharge;

    //Called to do initial creation of a fragment. This is called after onAttach(Activity) and
    // before onCreateView(LayoutInflater, ViewGroup, Bundle).Note that this can be called while
    // the fragment's activity is still in the process of being created. As such, you can not rely
    // on things like the activity's content view hierarchy being initialized at this point.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        _appPrefs = new AppPreferences(activity);

        arrayAccounts = _appPrefs.getAccounts();

        account = arrayAccounts.get(_appPrefs.getAccountListPosition());

        getChargeCodesAsync chargeCodes = new getChargeCodesAsync();
        chargeCodes.execute();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ChargeCodeFragment newInstance(int sectionNumber) {
        ChargeCodeFragment fragment = new ChargeCodeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ChargeCodeFragment() {
        // Required empty public constructor
    }

    //This method is to handle if your activity is every paused by a semi-transparent activity starts
    //ie opening an app drawer. Here you would handle such things as pausing a video or stopping
    //an animation.
    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
    }

    // Called to have the fragment instantiate its user interface view. This is optional, and
    // non-graphical fragments can return null (which is the default implementation)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_charge_code, container, false);

        //use inflated view to find elements on page
        tvDescription = (TextView) rootView.findViewById(R.id.tvDescription);
        tvAmount = (TextView) rootView.findViewById(R.id.tvAmount);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);
        etAmount = (EditText) rootView.findViewById(R.id.etAmount);
        chargecodespinner = (Spinner) rootView.findViewById(R.id.chargecodespinner);
        datePicker = (DatePicker) rootView.findViewById(R.id.datePicker);
        btnCharge = (Button) rootView.findViewById(R.id.btnCharge);


        etAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Toast toast = Toast.makeText(getActivity(), "Amount changed, other values updated.",
                        Toast.LENGTH_LONG);
                toast.show();
                float newAmount = Float.parseFloat(etAmount.getText().toString());
                if (newAmount != chargeCode.Amount) {

                    getChargeAmountAsync ChargeAmount = new getChargeAmountAsync();
                    ChargeAmount.execute();
                }
                return false;
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    class Data {

        static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }

    //Asygn task to get the ChgDesc field to be used to populate the spinner
    public class getChargeCodesAsync extends
            AsyncTask<Data, Void, ArrayList<ChargeCodes>> {

        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(activity);
            dialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected ArrayList<ChargeCodes> doInBackground(Data... data) {
            SoapObject codes = getChargeCodes();
            return RetrieveChargeCodesFromSoap(codes);

        }

        protected void onProgressUpdate(Integer... progress) {
            dialog.incrementProgressBy(progress[0]);

        }


        protected void onPostExecute(ArrayList<ChargeCodes> result) {
            dialog.dismiss();
            arrayChargeCodes = result;
            addItemsOnSpinner(arrayChargeCodes);

        }
    }

    public SoapObject getChargeCodes() {
        SOAP_ACTION = "getChargeCodes";
        METHOD_NAME = "getChargeCodes";

        SoapObject Request = new SoapObject(Data.NAMESPACE, METHOD_NAME);

        schid = _appPrefs.getSchID();
        userid = _appPrefs.getUserID();
        userguid = _appPrefs.getUserGUID();

        PropertyInfo Where = new PropertyInfo();
        Where.setName("Where");
        Where.setValue(" where schid =" + schid);
        Request.addProperty(Where);

        PropertyInfo UserID = new PropertyInfo();
        UserID.setName("UserID");
        UserID.setValue(userid);
        Request.addProperty(UserID);

        PropertyInfo UserGUID = new PropertyInfo();
        UserGUID.setName("UserGUID");
        UserGUID.setValue(userguid);
        Request.addProperty(UserGUID);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        envelope.dotNet = true;
        envelope.setOutputSoapObject(Request);

        SoapObject response = null;
        HttpTransportSE HttpTransport = new HttpTransportSE(Data.URL);
        try {
            HttpTransport.call(SOAP_ACTION, envelope);

            response = (SoapObject) envelope.getResponse();


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
        return response;

    }

    public static ArrayList<ChargeCodes> RetrieveChargeCodesFromSoap(SoapObject soap) {

        ArrayList<ChargeCodes> codes = new ArrayList<ChargeCodes>();
        for (int i = 0; i < soap.getPropertyCount() - 1; i++) {

            SoapObject accountchargecodes = (SoapObject) soap.getProperty(i);

            ChargeCodes chargeCode = new ChargeCodes();
            for (int j = 0; j < accountchargecodes.getPropertyCount() - 1; j++) {
                chargeCode.setProperty(j, accountchargecodes.getProperty(j)
                        .toString());
                if (accountchargecodes.getProperty(j).equals("anyType{}")) {
                    accountchargecodes.setProperty(j, "");
                }

            }
            codes.add(i, chargeCode);
        }

        return codes;
    }

    //Adds all items from the ChgDesc field to the spinner
    public void addItemsOnSpinner(ArrayList<ChargeCodes> codes) {

        adapterChargeCodes = new ChargeCodeAdapter(activity,
                R.layout.fragment_charge_code, codes);

        chargecodespinner.setAdapter(adapterChargeCodes);

        chargecodespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setSelectedCharge(chargecodespinner);

                if (etAmount.getText().toString() != "0.00") {
                    getChargeAmountAsync ChargeAmount = new getChargeAmountAsync();
                    ChargeAmount.execute();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Handles if the selected field for the spinner
    public void setSelectedCharge(Spinner spinnerChargeCode) {

        int selected = spinnerChargeCode.getSelectedItemPosition();
        chargeCode = (ChargeCodes) spinnerChargeCode.getItemAtPosition(selected);
        etDescription.setText(chargeCode.ChgDesc);

        if (chargeCode.Kind.equals("T") && Integer.parseInt(chargeCode.ChgNo) < 4) {

            etAmount.setText(String.valueOf(account.MTuition));
        } else {
            etAmount.setText(String.valueOf(chargeCode.Amount));
        }
    }


    public class getChargeAmountAsync extends
            AsyncTask<Data, Void, Float[]> {

        ProgressDialog dialog;

        protected void onPreExecute() {
          /*  dialog = new ProgressDialog(activity);
            dialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.show();*/
        }

        @Override
        protected Float[] doInBackground(Data... data) {
            SoapObject newCodes = null;

            float ST1Rate = _appPrefs.getST1Rate();
            float ST2Rate = _appPrefs.getST2Rate();
            newCodes = getChargeAmount(userid, userguid, chargeCode.ChgID, account.AcctID, account.BillingFreq, Float.parseFloat(etAmount.getText().toString()), account.TuitionSel, account.AccountFeeAmount, ST1Rate, ST2Rate);
            return RetrieveChargeCodeFromSoap(newCodes);


        }

        protected void onProgressUpdate(Integer... progress) {
            // dialog.incrementProgressBy(progress[0]);

        }


        protected void onPostExecute(Float[] result) {
            //dialog.dismiss();
            float floatAmount = Float.parseFloat(etAmount.getText().toString());
            float floatDiscAmount = result[0];
            float floatSTax1 = result[1];
            float floatSTax2 = result[2];

            tvDiscAmount = (TextView) activity.findViewById(R.id.tvDiscAmount);
            tvDiscAmountDisplayed = (TextView) activity.findViewById(R.id.tvDiscAmountDisplayed);
            tvTotal = (TextView) activity.findViewById(R.id.tvTotal);
            tvTotalDisplayed = (TextView) activity.findViewById(R.id.tvTotalDisplayed);

            tvDiscAmount.setVisibility(View.GONE);
            tvDiscAmountDisplayed.setVisibility(View.GONE);
            tvTotal.setVisibility(View.GONE);
            tvTotalDisplayed.setVisibility(View.GONE);

            if (floatDiscAmount != floatAmount) {
                tvDiscAmount.setVisibility(View.VISIBLE);
                tvDiscAmount.setText("Discounted Amount");
                tvDiscAmountDisplayed.setVisibility(View.VISIBLE);
                tvDiscAmountDisplayed.setTextSize(20);
                tvDiscAmountDisplayed.setText(String.valueOf(floatDiscAmount));
            }
            if (floatSTax1 + floatSTax2 > 0) {
                tvTotal.setVisibility(View.VISIBLE);
                tvTotal.setText("Total w/ Tax");
                tvTotalDisplayed.setVisibility(View.VISIBLE);
                tvTotalDisplayed.setTextSize(20);
                float total = floatDiscAmount + floatSTax1 + floatSTax2;
                tvTotalDisplayed.setText(String.valueOf(total));
            }
        }
    }

    public SoapObject getChargeAmount(int UserID, String UserGUID, int ChgID, int AcctID, int BillingFreq, float Amount, int TuitionSel, float AccountFeeAmount, float ST1Rate, float ST2Rate) {

        SOAP_ACTION = "getChargeAmount";
        METHOD_NAME = "getChargeAmount";

        SoapObject RequestCodes = new SoapObject(Data.NAMESPACE, METHOD_NAME);

        PropertyInfo userID = new PropertyInfo();
        userID.setName("UserID");
        userID.setValue(UserID);
        RequestCodes.addProperty(userID);

        PropertyInfo userGUID = new PropertyInfo();
        userGUID.setName("UserGUID");
        userGUID.setValue(UserGUID);
        RequestCodes.addProperty(userGUID);

        PropertyInfo chgID = new PropertyInfo();
        chgID.setName("ChgID");
        chgID.setValue(ChgID);
        RequestCodes.addProperty(chgID);


        PropertyInfo acctid = new PropertyInfo();
        acctid.setName("AcctID");
        acctid.setValue(AcctID);
        RequestCodes.addProperty(acctid);

        PropertyInfo billingfreq = new PropertyInfo();
        billingfreq.setName("BillingFreq");
        billingfreq.setValue(BillingFreq);
        RequestCodes.addProperty(billingfreq);

        PropertyInfo amount = new PropertyInfo();
        amount.setName("Amount");
        amount.setType(Float.class);
        amount.setValue(Amount);
        RequestCodes.addProperty(amount);

        PropertyInfo tuitionsel = new PropertyInfo();
        tuitionsel.setName("TuitionSel");
        tuitionsel.setValue(TuitionSel);
        RequestCodes.addProperty(tuitionsel);


        PropertyInfo accountfeeamount = new PropertyInfo();
        accountfeeamount.setName("AccountFeeAmount");
        accountfeeamount.setType(Float.class);
        accountfeeamount.setValue(AccountFeeAmount);
        RequestCodes.addProperty(accountfeeamount);

        PropertyInfo st1rate = new PropertyInfo();
        st1rate.setName("ST1Rate");
        st1rate.setType(Float.class);
        st1rate.setValue(ST1Rate);
        RequestCodes.addProperty(st1rate);

        PropertyInfo st2rate = new PropertyInfo();
        st2rate.setName("ST2Rate");
        st2rate.setType(Float.class);
        st2rate.setValue(ST2Rate);
        RequestCodes.addProperty(st2rate);

        SoapSerializationEnvelope envelopeCodes = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        MarshalFloat mf = new MarshalFloat();
        mf.register(envelopeCodes);

        envelopeCodes.dotNet = true;
        envelopeCodes.setOutputSoapObject(RequestCodes);

        SoapObject responseCodes = null;
        HttpTransportSE HttpTransport = new HttpTransportSE(Data.URL);
        try {
            HttpTransport.call(SOAP_ACTION, envelopeCodes);

            responseCodes = (SoapObject) envelopeCodes.getResponse();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            //Toast toast = Toast.makeText(getActivity(), e2.toString(), Toast.LENGTH_LONG);
            //toast.show();
            e.printStackTrace();
        }

        return responseCodes;
    }


    public static Float[] RetrieveChargeCodeFromSoap(SoapObject soap) {

        Float[] codes = new Float[3];

        codes[0] = Float.parseFloat(soap.getProperty(0).toString());
        codes[1] = Float.parseFloat(soap.getProperty(1).toString());
        codes[2] = Float.parseFloat(soap.getProperty(2).toString());

        return codes;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
