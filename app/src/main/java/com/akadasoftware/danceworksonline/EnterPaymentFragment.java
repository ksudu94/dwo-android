package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AppPreferences;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String ARG_SECTION_NUMBER = "section_number";


    String SOAP_ACTION, METHOD_NAME, userguid;
    int schid, userid, chgid;

    private AppPreferences _appPrefs;
    Account account;
    Activity activity;
    ArrayList<Account> arrayAccounts;

    TextView tvDate, tvTitle, tvType, tvReference, tvDescription, tvAmount, tvChangeAmount;
    EditText etReference, etDescription;
    Button btnUseDifferentCard, btnPayment;
    Spinner typeSpinner;
    Calendar cal;

    // Listeners for the interface used to handle the dialog pop-ups
    private onEditAmountDialog mListener;
    private onEditDateDialog dateListener;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EnterPaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public EnterPaymentFragment() {
        // Required empty public constructor
    }

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
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
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
