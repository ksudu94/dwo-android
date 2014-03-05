package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.text.NumberFormat;
import java.util.ArrayList;


/**
 * A fragment that is used to run both the getChargeCodes and getChargeAmount web methods.
 * It gets the account from _appPrefs as a position in the accounts array list and from that
 * account uses fields like acctid, billingfreq, mtuition etc. It also uses the ChargeCode class
 * for other values. Those values are retrieved from the web method with the values userid,
 * userguid and schid which are all in _appPrefs. The end result is a spinner populated by the
 * ChgDesc field and then values returned with DiscountedAmount and Total w/ tax textviews
 * only showing up if their values have changed.
 */


public class EnterChargeFragment extends Fragment {


    String SOAP_ACTION, METHOD_NAME, userguid;
    int schid, userid;


    private AppPreferences _appPrefs;
    Account account;
    Activity activity;
    ArrayList<Account> arrayAccounts;
    ArrayList<ChargeCodes> arrayChargeCodes = new ArrayList<ChargeCodes>();
    ChargeCodeAdapter adapterChargeCodes;
    ChargeCodes chargeCode;
    float floatAmount, floatDiscAmount, floatSTax1, floatSTax2;

    TextView tvDescription, tvAmount, tvChangeAmount, tvAmountHint, tvDiscAmount, tvDiscAmountDisplayed, tvTotal, tvTotalDisplayed;
    EditText etDescription;
    DatePicker datePicker;
    Spinner chargecodespinner;
    Button btnCharge;
    //Listener for the interace used to handle the dialog pop-up
    private onEditAmountDialog mListener;

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

        int position = getArguments().getInt("Position");

        account = arrayAccounts.get(position);

    }

    @Override
    public void onResume() {
        super.onResume();
        getChargeCodesAsync chargeCodes = new getChargeCodesAsync();
        chargeCodes.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onEditAmountDialog) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onEditAmountDialog");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*Uses a interface so that it can communicate with the parent activity which is AccountInformation
    * First it comes here and then goes to AccountInformation where it is then created with the
    * onEditAmountDialog method.. That method goes to the EditAmountDialog class which takes the
    * new Amount from the dialog and returns it to the onFinishEditAmountDialog method that from there
    * runs the runChargeAmountAsync method because we could not access it otherwise from outside this
    * class.
    */
    public interface onEditAmountDialog {
        // TODO: Update argument type and name
        public void onEditAmountDialog(String input);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static EnterChargeFragment newInstance(int position) {
        EnterChargeFragment fragment = new EnterChargeFragment();
        Bundle args = new Bundle();
        args.putInt("Position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public EnterChargeFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_entercharge, container, false);

        //use inflated view to find elements on page
        tvDescription = (TextView) rootView.findViewById(R.id.tvDescription);
        tvAmount = (TextView) rootView.findViewById(R.id.tvAmount);
        tvChangeAmount = (TextView) rootView.findViewById(R.id.tvChangeAmount);
        tvAmountHint = (TextView) rootView.findViewById(R.id.tvAmountHint);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);
        chargecodespinner = (Spinner) rootView.findViewById(R.id.chargecodespinner);
        datePicker = (DatePicker) rootView.findViewById(R.id.datePicker);
        btnCharge = (Button) rootView.findViewById(R.id.btnCharge);
        tvDiscAmount = (TextView) rootView.findViewById(R.id.tvDiscAmount);
        tvDiscAmountDisplayed = (TextView) rootView.findViewById(R.id.tvDiscAmountDisplayed);
        tvTotal = (TextView) rootView.findViewById(R.id.tvTotal);
        tvTotalDisplayed = (TextView) rootView.findViewById(R.id.tvTotalDisplayed);

        setTotalsDisplay(false, false);


        //Handles when values are changed and a new charge is to be added
        btnCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvChangeAmount.getText().toString().trim().length() > 0) {
                    Float floatAmount = Float.parseFloat(tvChangeAmount.getText().toString());
                    if (floatAmount == 0) {
                        Toast toast = Toast.makeText(getActivity(), "Cannot enter a charge with an amount of $0 ",
                                Toast.LENGTH_LONG);
                        toast.show();
                    } else if (etDescription.getText().toString().trim().length() == 0) {
                        Toast toast = Toast.makeText(getActivity(), "Cannot enter a charge with a blank Description",
                                Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        enterChargeAsync enter = new enterChargeAsync();
                        enter.execute();
                    }
                } else {
                    Toast toast = Toast.makeText(getActivity(), "Cannot enter a charge with an amount of $0 ",
                            Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        tvChangeAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onEditAmountDialog(tvChangeAmount.getText().toString());
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    class Data {

        static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }

    //Asycn task to get the ChgDesc field to be used to populate the spinner
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
        ChargeCodes defaultCC = new ChargeCodes();
        defaultCC.ChgDesc = "Select Charge Type";
        defaultCC.Kind = "X";
        defaultCC.ChgNo = "0";
        defaultCC.ChgID = 0;
        defaultCC.Amount = 0;
        codes.add(0, defaultCC);
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
            codes.add(i + 1, chargeCode);
        }

        return codes;
    }

    //Adds all items from the ChgDesc field to the spinner
    public void addItemsOnSpinner(ArrayList<ChargeCodes> codes) {

        adapterChargeCodes = new ChargeCodeAdapter(activity,
                R.layout.fragment_entercharge, codes);

        chargecodespinner.setAdapter(adapterChargeCodes);

        chargecodespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setSelectedCharge(chargecodespinner);

                if (!tvChangeAmount.getText().toString().equals("0.00")) {
                    runChargeAmountAsync();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /*We need this method so that we can run it form onFinishEditAmoutnDialog which is in the parent
    activity.
    */
    public void runChargeAmountAsync() {
        getChargeAmountAsync ChargeAmount = new getChargeAmountAsync();
        ChargeAmount.execute();
    }

    //Handles if the selected field for the spinner
    public void setSelectedCharge(Spinner spinnerChargeCode) {

        int selected = spinnerChargeCode.getSelectedItemPosition();
        chargeCode = (ChargeCodes) spinnerChargeCode.getItemAtPosition(selected);


        if (chargeCode.ChgID == 0) {
            tvChangeAmount.setText("0.00");
            etDescription.setText("");

        } else if (chargeCode.Kind.equals("T") && Integer.parseInt(chargeCode.ChgNo) < 4) {

            tvChangeAmount.setText(String.valueOf(account.MTuition));
            etDescription.setText(chargeCode.ChgDesc);
        } else {
            tvChangeAmount.setText(String.valueOf(chargeCode.Amount));
            etDescription.setText(chargeCode.ChgDesc);
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
            newCodes = getChargeAmount(userid, userguid, chargeCode.ChgID, account.AcctID, account.BillingFreq, Float.parseFloat(tvChangeAmount.getText().toString()), account.TuitionSel, account.AccountFeeAmount, ST1Rate, ST2Rate);
            return RetrieveChargeCodeFromSoap(newCodes);


        }

        protected void onProgressUpdate(Integer... progress) {
            // dialog.incrementProgressBy(progress[0]);

        }


        protected void onPostExecute(Float[] result) {
            //dialog.dismiss();
            NumberFormat format = NumberFormat.getCurrencyInstance();
            floatAmount = Float.parseFloat(tvChangeAmount.getText().toString());
            floatDiscAmount = result[0];
            floatSTax1 = result[1];
            floatSTax2 = result[2];

            Boolean discount = false;
            Boolean tax = false;

            if (floatDiscAmount != floatAmount) {
                discount = true;
                tvDiscAmountDisplayed.setText(String.valueOf(format.format(floatDiscAmount)));
            }
            if (floatSTax1 + floatSTax2 > 0) {
                tax = true;
                float total = floatDiscAmount + floatSTax1 + floatSTax2;
                tvTotalDisplayed.setText(String.valueOf(format.format(total)));
            }

            setTotalsDisplay(discount, tax);
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


    public class enterChargeAsync extends
            AsyncTask<Data, Void, Boolean> {

        ProgressDialog dialog;

        protected void onPreExecute() {
            dialog = new ProgressDialog(activity);
            dialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Data... data) {
            SoapPrimitive enterCharge = null;
            float totalAmount = 0;
            float DiscAmount = 0;
            User user = _appPrefs.getUser();
            ;

            if (tvDiscAmountDisplayed.isShown()) {
                totalAmount = floatDiscAmount;
                DiscAmount = chargeCode.Amount - floatDiscAmount;
            } else if (tvTotalDisplayed.isShown()) {
                totalAmount = floatDiscAmount + floatSTax1 + floatSTax2;
                DiscAmount = 0;
            } else
                totalAmount = chargeCode.Amount;

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();

            String date = String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day);


            enterCharge = EnterCharge(userid, userguid, schid, account.AcctID, date, chargeCode.ChgDesc,
                    chargeCode.GLNo, floatDiscAmount, totalAmount, chargeCode.Kind, chargeCode.Tax,
                    false, 0, chargeCode.PayOnline, 0, _appPrefs.getSessionID(), DiscAmount,
                    floatSTax1, floatSTax2, user.DisplayName);
            return EnterChargeFromSoap(enterCharge);


        }

        protected void onProgressUpdate(Integer... progress) {
            dialog.incrementProgressBy(progress[0]);

        }


        protected void onPostExecute(Boolean result) {
            dialog.dismiss();

            if (result) {
                Toast toast = Toast.makeText(getActivity(), "Charge Successfully entered", Toast.LENGTH_LONG);
                toast.show();
                chargecodespinner.setSelection(0);
                setTotalsDisplay(false, false);
            } else {
                Toast toast = Toast.makeText(getActivity(), "Charge failed", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public SoapPrimitive EnterCharge(int UserID, String UserGUID, int SchID, int AcctID, String ChgDate,
                                     String ChgDesc, String GLNo, float Amount, float totalAmount,
                                     String Kind, int STax, Boolean POSTrans, int POSInv, Boolean PayOnline,
                                     int TransPostHistID, int SessionID, float DiscAmt, float STax1,
                                     float STax2, String DisplayName) {

        SOAP_ACTION = "enterCharge";
        METHOD_NAME = "enterCharge";

        SoapObject requestEnterCharge = new SoapObject(Data.NAMESPACE, METHOD_NAME);

        PropertyInfo userID = new PropertyInfo();
        userID.setName("UserID");
        userID.setValue(UserID);
        requestEnterCharge.addProperty(userID);

        PropertyInfo userGUID = new PropertyInfo();
        userGUID.setName("UserGUID");
        userGUID.setValue(UserGUID);
        requestEnterCharge.addProperty(userGUID);

        PropertyInfo schid = new PropertyInfo();
        schid.setName("SchID");
        schid.setValue(SchID);
        requestEnterCharge.addProperty(schid);


        PropertyInfo acctid = new PropertyInfo();
        acctid.setName("AcctID");
        acctid.setValue(AcctID);
        requestEnterCharge.addProperty(acctid);

        PropertyInfo chgdate = new PropertyInfo();
        chgdate.setName("strChgDate");
        chgdate.setValue(ChgDate);
        requestEnterCharge.addProperty(chgdate);

        PropertyInfo chgdesc = new PropertyInfo();
        chgdesc.setName("ChgDesc");
        chgdesc.setValue(ChgDesc);
        requestEnterCharge.addProperty(chgdesc);

        PropertyInfo glno = new PropertyInfo();
        glno.setName("GLNo");
        glno.setValue(GLNo);
        requestEnterCharge.addProperty(glno);


        PropertyInfo amount = new PropertyInfo();
        amount.setName("Amount");
        amount.setType(Float.class);
        amount.setValue(Amount);
        requestEnterCharge.addProperty(amount);

        PropertyInfo totalamount = new PropertyInfo();
        totalamount.setName("totalAmount");
        totalamount.setType(Float.class);
        totalamount.setValue(totalAmount);
        requestEnterCharge.addProperty(totalamount);

        PropertyInfo kind = new PropertyInfo();
        kind.setName("Kind");
        kind.setValue(Kind);
        requestEnterCharge.addProperty(kind);

        PropertyInfo stax = new PropertyInfo();
        stax.setName("STax");
        stax.setValue(STax);
        requestEnterCharge.addProperty(stax);

        PropertyInfo posttrans = new PropertyInfo();
        posttrans.setName("POSTrans");
        posttrans.setValue(POSTrans);
        requestEnterCharge.addProperty(posttrans);

        PropertyInfo posinv = new PropertyInfo();
        posinv.setName("POSInv");
        posinv.setValue(POSInv);
        requestEnterCharge.addProperty(posinv);

        PropertyInfo payonline = new PropertyInfo();
        payonline.setName("PayOnline");
        payonline.setValue(PayOnline);
        requestEnterCharge.addProperty(payonline);


        PropertyInfo transposthistid = new PropertyInfo();
        transposthistid.setName("TransPostHistID");
        transposthistid.setValue(TransPostHistID);
        requestEnterCharge.addProperty(transposthistid);

        PropertyInfo sessionid = new PropertyInfo();
        sessionid.setName("SessionID");
        sessionid.setValue(SessionID);
        requestEnterCharge.addProperty(sessionid);

        PropertyInfo discamt = new PropertyInfo();
        discamt.setName("DiscAmt");
        discamt.setType(Float.class);
        discamt.setValue(DiscAmt);
        requestEnterCharge.addProperty(discamt);

        PropertyInfo stax1 = new PropertyInfo();
        stax1.setName("STax1");
        stax1.setType(Float.class);
        stax1.setValue(STax1);
        requestEnterCharge.addProperty(stax1);


        PropertyInfo stax2 = new PropertyInfo();
        stax2.setName("STax2");
        stax2.setType(Float.class);
        stax2.setValue(STax2);
        requestEnterCharge.addProperty(stax2);

        PropertyInfo displayname = new PropertyInfo();
        displayname.setName("DisplayName");
        displayname.setValue(DisplayName);
        requestEnterCharge.addProperty(displayname);


        SoapSerializationEnvelope envelopeEnterCharge = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        MarshalFloat mf = new MarshalFloat();
        mf.register(envelopeEnterCharge);

        //MarshalDate md = new MarshalDate();
        //md.register(envelopeCodes);

        envelopeEnterCharge.dotNet = true;
        envelopeEnterCharge.setOutputSoapObject(requestEnterCharge);

        SoapPrimitive responseEnterCharge = null;
        HttpTransportSE HttpTransport = new HttpTransportSE(Data.URL);
        try {
            HttpTransport.call(SOAP_ACTION, envelopeEnterCharge);

            responseEnterCharge = (SoapPrimitive) envelopeEnterCharge.getResponse();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return responseEnterCharge;
    }

    public void setTotalsDisplay(Boolean discount, Boolean tax) {
        if (discount) {

            tvDiscAmount.setVisibility(View.VISIBLE);
            tvDiscAmountDisplayed.setVisibility(View.VISIBLE);
        } else {
            tvDiscAmount.setVisibility(View.GONE);
            tvDiscAmountDisplayed.setVisibility(View.GONE);
        }

        if (tax) {
            tvTotal.setVisibility(View.VISIBLE);
            tvTotalDisplayed.setVisibility(View.VISIBLE);
        } else {
            tvTotal.setVisibility(View.GONE);
            tvTotalDisplayed.setVisibility(View.GONE);
        }
    }

    public static Boolean EnterChargeFromSoap(SoapPrimitive soap) {

        Boolean success = Boolean.parseBoolean(soap.toString());

        return success;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */


}
