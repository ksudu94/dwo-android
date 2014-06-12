package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.Globals;
import com.akadasoftware.danceworksonline.classes.Student;
import com.akadasoftware.danceworksonline.classes.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 4/9/2014.
 */
public class StudentInformationFragment extends Fragment {


    private AppPreferences _appPrefs;
    Activity activity;
    User user;
    Student oStudent;
    Globals globals;
    ArrayList<Student> Students;

    int position;
    String status;


    TextView tvName1, tvAddress1, tvContact1, tvStatus1, tvAccountName1;

    EditText etFName, etLName, etAddress, etCity, etState, etZip, etContact, etAccountName;

    Button btnEditStudent, btnEnrollStudent, btnSave, btnCancel;

    ViewFlipper studentSwitcher;

    Spinner StudentStatusSpinner;

    public static StudentInformationFragment newInstance(int position) {
        StudentInformationFragment fragment = new StudentInformationFragment();
        Bundle args = new Bundle();
        //Student list position
        args.putInt("Position", position);
        fragment.setArguments(args);

        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        globals = new Globals();
        _appPrefs = new AppPreferences(activity);
        Students = _appPrefs.getStudents();
        position = getArguments().getInt("Position");

        oStudent = Students.get(position);
        _appPrefs.saveStuID(oStudent.StuID);

    }

    public StudentInformationFragment() {
    }

    //Create the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_student_information, container, false);

        switch (oStudent.Status) {
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

        tvName1 = (TextView) rootView.findViewById(R.id.tvName1);
        tvAddress1 = (TextView) rootView.findViewById(R.id.tvAddress1);
        tvContact1 = (TextView) rootView.findViewById(R.id.tvContact1);
        tvStatus1 = (TextView) rootView.findViewById(R.id.tvStatus1);
        tvAccountName1 = (TextView) rootView.findViewById(R.id.tvAccountName1);

        etFName = (EditText) rootView.findViewById(R.id.etFName);
        etLName = (EditText) rootView.findViewById(R.id.etLName);
        etAddress = (EditText) rootView.findViewById(R.id.etAddress);
        etCity = (EditText) rootView.findViewById(R.id.etCity);
        etState = (EditText) rootView.findViewById(R.id.etState);
        etZip = (EditText) rootView.findViewById(R.id.etZip);
        etContact = (EditText) rootView.findViewById(R.id.etContact);
        etAccountName = (EditText) rootView.findViewById(R.id.etAccountName);


        btnEditStudent = (Button) rootView.findViewById(R.id.btnEditStudent);
        btnEnrollStudent = (Button) rootView.findViewById(R.id.btnEnrollStudent);
        btnSave = (Button) rootView.findViewById(R.id.btnSave);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);

        studentSwitcher = (ViewFlipper) rootView.findViewById(R.id.studentSwitcher);


        tvName1.setText(oStudent.FName + " " + oStudent.LName);
        tvAddress1.setText(oStudent.Address + ", " + oStudent.City + ", " + oStudent.City + ", " +
                oStudent.ZipCode);
        tvContact1.setText(oStudent.Phone);
        tvStatus1.setText(status);
        tvAccountName1.setText(oStudent.AcctName);


        btnEditStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    studentSwitcher.setDisplayedChild(1);
                    etFName.setText(oStudent.FName);
                    etLName.setText(oStudent.LName);
                    etAddress.setText(oStudent.Address);
                    etCity.setText(oStudent.City);
                    etState.setText(oStudent.State);
                    etZip.setText(oStudent.ZipCode);
                    etAccountName.setText(oStudent.AcctName);

                    List<String> spinnerlist = new ArrayList<String>();
                    spinnerlist.add("active");
                    spinnerlist.add("inactive");
                    spinnerlist.add("deleted");
                    if (status.equals("prospect"))
                        spinnerlist.add("prospect");

                    StudentStatusSpinner = (Spinner) activity.findViewById(R.id.StudentStatusSpinner);

                    ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, spinnerlist);

                    spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    StudentStatusSpinner.setAdapter(spinneradapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnEnrollStudent.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent openEnrollPage = new Intent("com.akadasoftware.danceworksonline.Enroll");
                startActivity(openEnrollPage);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    studentSwitcher.setDisplayedChild(0);
                    Toast toast = Toast.makeText(getActivity(), "Changes Canceled", Toast.LENGTH_LONG);
                    toast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;

    }


    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStackImmediate();
                break;
            default:
                break;
        }
        return true;
    }


    class Data {

        static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }
}
