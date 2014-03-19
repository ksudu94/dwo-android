package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AccountTransactions;
import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EnterPaymentFragment} interface
 * to handle interaction events.
 * Use the {@link EnterPaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterPaymentFragment extends Fragment {


    String SOAP_ACTION, METHOD_NAME, UserGUID, PDate, PDesc, ChkNo, Kind, CCard, CCDate, CCAuth,
            PaymentID, ProcessData, RefNo, AuthCode, Invoice, AcqRefData, CardHolderName, CCToken;

    int SchID, UserID, chgid, AcctID, CCRecNo, TransPostHistID, SessionID, ConsentID;

    Boolean POSTrans;

    Float Amount;
    private AppPreferences _appPrefs;
    Account account;
    User user;
    Activity activity;
    ArrayList<Account> arrayAccounts;
    ArrayList<AccountTransactions> TransactionArray;

    TextView tvDate, tvTitle, tvType, tvReference, tvDescription, tvAmount, tvChangeAmount;
    EditText etReference, etDescription;
    Button btnUseDifferentCard, btnPayment;
    Spinner typeSpinner;
    Calendar cal;

    // Listeners for the interface used to handle the dialog pop-ups
    private onEditAmountDialog mListener;
    private onEditDateDialog dateListener;


    /**
     * Uses a interface so that it can communicate with the parent activity which is AccountInformation
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

    public interface onEditDateDialog {
        // TODO: Update argument type and name
        public void onEditDateDialog(Calendar today);
    }

    public static EnterPaymentFragment newInstance(int position, String description, Float amount) {
        EnterPaymentFragment fragment = new EnterPaymentFragment();
        Bundle args = new Bundle();
        args.putFloat("Amount", amount);
        args.putString("Description", description);
        args.putInt("Position", position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EnterPaymentFragment.
     */
    public EnterPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        _appPrefs = new AppPreferences(activity);

        arrayAccounts = _appPrefs.getAccounts();
        int position = getArguments().getInt("Position");

        account = arrayAccounts.get(position);

        ViewPager mViewPager = (ViewPager) activity.findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int position) {

                refreshEnterPayment();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    public void refreshEnterPayment() {
        chgid = 0;
        etDescription.getText().clear();
        tvChangeAmount.setText("0.00");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_enterpayment, container, false);

        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvDate = (TextView) rootView.findViewById(R.id.tvDate);
        tvType = (TextView) rootView.findViewById(R.id.tvType);
        tvReference = (TextView) rootView.findViewById(R.id.tvReference);
        tvDescription = (TextView) rootView.findViewById(R.id.tvDescription);
        tvAmount = (TextView) rootView.findViewById(R.id.tvAmount);
        tvChangeAmount = (TextView) rootView.findViewById(R.id.tvChangeAmount);
        etReference = (EditText) rootView.findViewById(R.id.etReference);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);
        typeSpinner = (Spinner) rootView.findViewById(R.id.typeSpinner);
        btnUseDifferentCard = (Button) rootView.findViewById(R.id.btnUseDifferentCard);
        btnPayment = (Button) rootView.findViewById(R.id.btnPayment);


        tvChangeAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onEditAmountDialog(tvChangeAmount.getText().toString());
            }
        });


        cal = Calendar.getInstance();
        setDate(cal);

        tvDate.setTextSize(25);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateListener.onEditDateDialog(cal);
            }
        });


        /**
         * Button click to submit payment to database. Makes sure there are no 0 payments and there
         * description field is not blank.
         */
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvChangeAmount.getText().toString().trim().length() > 0) {
                    Float floatAmount = Float.parseFloat(tvChangeAmount.getText().toString());
                    if (floatAmount == 0) {
                        Toast toast = Toast.makeText(getActivity(), "Cannot enter a payment with an amount of $0 ",
                                Toast.LENGTH_LONG);
                        toast.show();
                    } else if (etDescription.getText().toString().trim().length() == 0) {
                        Toast toast = Toast.makeText(getActivity(), "Cannot enter a payment with a blank Description",
                                Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        enterPaymentAsync enter = new enterPaymentAsync();
                        enter.execute();
                    }
                } else {
                    Toast toast = Toast.makeText(getActivity(), "Cannot enter a payment with an amount of $0 ",
                            Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });

        /**
         * Creating a list of resources to populate the spinner with. Because the list is pre-determined
         * I created a resource of string array and put them in there and created a simple array
         * adapter to populate the list with.
         */
        ArrayAdapter<CharSequence> paymentType = ArrayAdapter.createFromResource(activity,
                R.array.payment_types, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        paymentType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        typeSpinner.setAdapter(paymentType);


        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                int position = typeSpinner.getSelectedItemPosition();

                switch (position) {
                    case 1:
                        etReference.setText("Cash");
                        break;
                    case 2:
                        etReference.setText("Chk");
                        break;
                    case 3:
                        etReference.setText("Credit Card");
                        break;
                    case 4:
                        etReference.setText("Other");
                        break;
                    default:
                        etReference.getText().clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // Returning the populated layout for this fragment
        return rootView;
    }

    public void setDate(Calendar calinput) {
        cal = calinput;
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String today = dateFormat.format(cal.getTime());
        tvDate.setText(today);
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
        try {
            dateListener = (onEditDateDialog) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onEditDateDialog");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        dateListener = null;
    }


    class Data {

        static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }

    public class enterPaymentAsync extends AsyncTask<Data, Void, Boolean> {

        public Boolean doInBackground(Data... data) {
            SoapPrimitive enterPayment = null;

            UserID = _appPrefs.getUserID();
            UserGUID = _appPrefs.getUserGUID();
            SchID = account.SchID;
            AcctID = account.AcctID;
            SessionID = _appPrefs.getSessionID();
            Amount = Float.valueOf(tvChangeAmount.getText().toString());
            POSTrans = false;
            Kind = "C";
            ChkNo = etReference.getText().toString();

            enterPayment = EnterPayment(UserID, UserGUID, SchID, AcctID, PDate, PDesc, ChkNo, Amount, Kind, CCard, CCDate, CCAuth,
                    CCRecNo, POSTrans, TransPostHistID, SessionID, ConsentID, PaymentID, ProcessData, RefNo,
                    AuthCode, Invoice, AcqRefData, CardHolderName, CCToken);
            return EnterPaymentFromSoap(enterPayment);
        }

        protected void onPostExecute(Boolean result) {

            if (result) {
                Toast toast = Toast.makeText(getActivity(), "Payment Successfully entered", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getActivity(), "Payment failed", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    /**
     * Some of the variables aren't being used because they end up being 0/"" anyway so i just set
     * that manually in the property info side to save time. I just left them in there so it'd match
     * our webmethod. They can easily be removed later.
     *
     * @return Returns a soap object which is basically a success or fail message.
     */
    public SoapPrimitive EnterPayment(int UserID, String UserGUID, int SchID, int AcctID, String PDate, String PDesc, String ChkNo, float Amount, String Kind, String CCard, String CCDate, String CCAuth,
                                      int CCRecNo, Boolean POSTrans, int TransPostHistID, int SessionID, int ConsentID, String PaymentID, String ProcessData, String RefNo,
                                      String AuthCode, String Invoice, String AcqRefData, String CardHolderName, String CCToken) {
        SOAP_ACTION = "enterPayment";
        METHOD_NAME = "enterPayment";

        SoapObject requestEnterPayment = new SoapObject(Data.NAMESPACE, METHOD_NAME);

        PropertyInfo userID = new PropertyInfo();
        userID.setName("UserID");
        userID.setValue(UserID);
        requestEnterPayment.addProperty(userID);

        PropertyInfo userGUID = new PropertyInfo();
        userGUID.setName("UserGUID");
        userGUID.setValue(UserGUID);
        requestEnterPayment.addProperty(userGUID);

        PropertyInfo schid = new PropertyInfo();
        schid.setName("SchID");
        schid.setValue(SchID);
        requestEnterPayment.addProperty(schid);


        PropertyInfo acctid = new PropertyInfo();
        acctid.setName("AcctID");
        acctid.setValue(AcctID);
        requestEnterPayment.addProperty(acctid);

        PropertyInfo pdate = new PropertyInfo();
        pdate.setName("PDate");
        pdate.setValue("");
        requestEnterPayment.addProperty(pdate);

        PropertyInfo pdesc = new PropertyInfo();
        pdesc.setName("PDesc");
        pdesc.setValue("");
        requestEnterPayment.addProperty(pdesc);

        PropertyInfo chkno = new PropertyInfo();
        chkno.setName("ChkNo");
        chkno.setValue(ChkNo);
        requestEnterPayment.addProperty(chkno);


        PropertyInfo amount = new PropertyInfo();
        amount.setName("Amount");
        amount.setType(Float.class);
        amount.setValue(Amount);
        requestEnterPayment.addProperty(amount);

        PropertyInfo kind = new PropertyInfo();
        kind.setName("Kind");
        kind.setValue(Kind);
        requestEnterPayment.addProperty(kind);

        PropertyInfo ccard = new PropertyInfo();
        ccard.setName("CCard");
        ccard.setValue("");
        requestEnterPayment.addProperty(ccard);

        PropertyInfo ccdate = new PropertyInfo();
        ccdate.setName("CCDate");
        ccdate.setValue("");
        requestEnterPayment.addProperty(ccdate);

        PropertyInfo ccauth = new PropertyInfo();
        ccauth.setName("CCAuth");
        ccauth.setValue("");
        requestEnterPayment.addProperty(ccauth);

        PropertyInfo ccrecno = new PropertyInfo();
        ccrecno.setName("CCRecNo");
        ccrecno.setValue(0);
        requestEnterPayment.addProperty(ccrecno);

        PropertyInfo postrans = new PropertyInfo();
        postrans.setName("POSTrans");
        postrans.setValue(POSTrans);
        requestEnterPayment.addProperty(postrans);


        PropertyInfo transposthistid = new PropertyInfo();
        transposthistid.setName("TransPostHistID");
        transposthistid.setValue(0);
        requestEnterPayment.addProperty(transposthistid);

        PropertyInfo sessionid = new PropertyInfo();
        sessionid.setName("SessionID");
        sessionid.setValue(SessionID);
        requestEnterPayment.addProperty(sessionid);

        PropertyInfo consentid = new PropertyInfo();
        consentid.setName("ConsentID");
        consentid.setValue(0);
        requestEnterPayment.addProperty(consentid);

        PropertyInfo paymentid = new PropertyInfo();
        paymentid.setName("PaymentID");
        paymentid.setValue(0);
        requestEnterPayment.addProperty(paymentid);


        PropertyInfo processdata = new PropertyInfo();
        processdata.setName("ProcessData");
        processdata.setValue("");
        requestEnterPayment.addProperty(processdata);

        PropertyInfo refno = new PropertyInfo();
        refno.setName("RefNo");
        refno.setValue("");
        requestEnterPayment.addProperty(refno);

        PropertyInfo authcode = new PropertyInfo();
        authcode.setName("AuthCode");
        authcode.setValue("");
        requestEnterPayment.addProperty(authcode);

        PropertyInfo invoice = new PropertyInfo();
        invoice.setName("Invoice");
        invoice.setValue("");
        requestEnterPayment.addProperty(invoice);


        PropertyInfo acqrefdata = new PropertyInfo();
        acqrefdata.setName("AcqRefData");
        acqrefdata.setValue("");
        requestEnterPayment.addProperty(acqrefdata);

        PropertyInfo cardholdername = new PropertyInfo();
        cardholdername.setName("CardHolderName");
        cardholdername.setValue("");
        requestEnterPayment.addProperty(cardholdername);

        PropertyInfo cctoken = new PropertyInfo();
        cctoken.setName("CCToken");
        cctoken.setValue("");
        requestEnterPayment.addProperty(cctoken);

        SoapSerializationEnvelope envelopePayment = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        MarshalFloat mf = new MarshalFloat();
        mf.register(envelopePayment);

        envelopePayment.dotNet = true;
        envelopePayment.setOutputSoapObject(requestEnterPayment);

        SoapPrimitive responsePayment = null;
        HttpTransportSE HttpTransport = new HttpTransportSE("http://app.akadasoftware.com/MobileAppWebService/Android.asmx");
        try {
            HttpTransport.call(SOAP_ACTION, envelopePayment);

            responsePayment = (SoapPrimitive) envelopePayment.getResponse();


        } catch (Exception e) {
            e.printStackTrace();
        }

        return responsePayment;
    }


    public Boolean EnterPaymentFromSoap(SoapPrimitive soap) {

        Boolean success = Boolean.parseBoolean(soap.toString());

        return success;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
}
