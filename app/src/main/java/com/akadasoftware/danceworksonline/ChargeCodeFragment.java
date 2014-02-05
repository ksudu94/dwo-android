package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    ArrayList<ChargeCodes> codeArray = new ArrayList<ChargeCodes>();
    ChargeCodeAdapter chargeAdapter;
    ChargeCodes chargeCode;

    TextView tvDescription, tvAmount, tvTotal;
    EditText etDescription, etAmount, etTotal;
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

        getAccountChargeCodes codes = new getAccountChargeCodes();
        codes.execute();


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
        tvTotal = (TextView) rootView.findViewById(R.id.tvTotal);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);
        etAmount = (EditText) rootView.findViewById(R.id.etAmount);
        etTotal = (EditText) rootView.findViewById(R.id.etTotal);
        chargecodespinner = (Spinner) rootView.findViewById(R.id.chargecodespinner);
        datePicker = (DatePicker) rootView.findViewById(R.id.datePicker);
        btnCharge = (Button) rootView.findViewById(R.id.btnCharge);


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
    public class getAccountChargeCodes extends
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
            return RetrieveFromSoap(codes);

        }

        protected void onProgressUpdate(Integer... progress) {
            dialog.incrementProgressBy(progress[0]);

        }


        protected void onPostExecute(ArrayList<ChargeCodes> result) {
            dialog.dismiss();
            codeArray = result;
            addItemsOnSpinner(result);

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

    public static ArrayList<ChargeCodes> RetrieveFromSoap(SoapObject soap) {

        ArrayList<ChargeCodes> codes = new ArrayList<ChargeCodes>();
        for (int i = 0; i < soap.getPropertyCount() - 1; i++) {

            SoapObject accountchargecodes = (SoapObject) soap.getProperty(i);

            ChargeCodes chgcodes = new ChargeCodes();
            for (int j = 0; j < accountchargecodes.getPropertyCount() - 1; j++) {
                chgcodes.setProperty(j, accountchargecodes.getProperty(j)
                        .toString());
                if (accountchargecodes.getProperty(j).equals("anyType{}")) {
                    accountchargecodes.setProperty(j, "");
                }

            }
            codes.add(i, chgcodes);
        }

        return codes;
    }

    //Adds all items from the ChgDesc field to the spinner
    public void addItemsOnSpinner(ArrayList<ChargeCodes> codes) {

        chargeAdapter = new ChargeCodeAdapter(activity,
                R.layout.fragment_charge_code, codes);

        chargecodespinner.setAdapter(chargeAdapter);
        setSelectedSession(chargecodespinner);
    }

    //Handles if the selected field for the spinner
    public void setSelectedSession(Spinner spinnerChargeCode) {
        ChargeCodeAdapter adapter = (ChargeCodeAdapter) spinnerChargeCode.getAdapter();

        for (int position = 0; position < adapter.getCount(); position++) {
            try {
                chargeCode = (ChargeCodes) spinnerChargeCode.getItemAtPosition(position);
                etDescription.setText(chargeCode.ChgDesc);
                etAmount.setText(String.valueOf(chargeCode.Amount));
            } catch (Exception e) {
                Toast toast = Toast.makeText(activity, e.toString(),
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }

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
