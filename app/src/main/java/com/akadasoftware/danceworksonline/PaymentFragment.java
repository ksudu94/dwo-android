package com.akadasoftware.danceworksonline;

import android.app.Activity;
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

import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AppPreferences;

import java.util.ArrayList;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaymentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String ARG_SECTION_NUMBER = "section_number";


    String SOAP_ACTION, METHOD_NAME, userguid;
    int schid, userid;

    private AppPreferences _appPrefs;
    Account account;
    Activity activity;
    ArrayList<Account> arrayAccounts;

    TextView tvTitle, tvType, tvReference, tvDescription, tvAmount;
    EditText etReference, etDescription, etAmount;
    Button btnUseDifferentCard, btnPayment;
    DatePicker datePicker;
    Spinner typeSpinner;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentFragment newInstance(int sectionNumber) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PaymentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        _appPrefs = new AppPreferences(activity);

        arrayAccounts = _appPrefs.getAccounts();

        account = arrayAccounts.get(_appPrefs.getAccountListPosition());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_enterpayment, container, false);

        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvType = (TextView) rootView.findViewById(R.id.tvType);
        tvReference = (TextView) rootView.findViewById(R.id.tvReference);
        tvDescription = (TextView) rootView.findViewById(R.id.tvDescription);
        tvAmount = (TextView) rootView.findViewById(R.id.tvAmount);
        etReference = (EditText) rootView.findViewById(R.id.etReference);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);
        etAmount = (EditText) rootView.findViewById(R.id.etAmount);
        typeSpinner = (Spinner) rootView.findViewById(R.id.typeSpinner);
        datePicker = (DatePicker) rootView.findViewById(R.id.datePicker);
        btnUseDifferentCard = (Button) rootView.findViewById(R.id.btnUseDifferentCard);
        btnPayment = (Button) rootView.findViewById(R.id.btnPayment);


        // Returning the populated layout for this fragment
        return rootView;
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
