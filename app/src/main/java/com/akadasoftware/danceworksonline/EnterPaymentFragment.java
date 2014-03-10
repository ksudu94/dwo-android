package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

    TextView tvDate, tvTitle, tvType, tvReference, tvDescription, tvAmount;
    EditText etReference, etDescription, etAmount;
    Button btnUseDifferentCard, btnPayment;
    DatePicker datePicker;
    Spinner typeSpinner;
    Calendar cal;

    private onEditDatePaymentDialog dateListener;


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

    public interface onEditDatePaymentDialog {
        // TODO: Update argument type and name
        public void onEditDatePaymentDialog(Calendar today);
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
        etAmount.getText().clear();
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
        etReference = (EditText) rootView.findViewById(R.id.etReference);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);
        etAmount = (EditText) rootView.findViewById(R.id.etAmount);
        typeSpinner = (Spinner) rootView.findViewById(R.id.typeSpinner);
        datePicker = (DatePicker) rootView.findViewById(R.id.datePicker);
        btnUseDifferentCard = (Button) rootView.findViewById(R.id.btnUseDifferentCard);
        btnPayment = (Button) rootView.findViewById(R.id.btnPayment);


        cal = Calendar.getInstance();
        setDate(cal);
        tvDate.setTextSize(25);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateListener.onEditDatePaymentDialog(cal);
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
            dateListener = (onEditDatePaymentDialog) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onEditDatePaymentDialog");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
