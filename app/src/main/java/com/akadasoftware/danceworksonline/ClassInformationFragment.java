package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.content.Intent;
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

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.Globals;
import com.akadasoftware.danceworksonline.classes.SchoolClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ksudu94 on 7/1/2014.
 */
public class ClassInformationFragment extends Fragment {

    Activity activity;
    Globals oGlobal;

    int position;

    String strSessionName;

    ArrayList<SchoolClasses> schoolClassList;
    SchoolClasses oSchoolClass;

    TextView tvWhichSession, tvClassType1, tvClassLevel1, tvClassDescription1, tvNumberEnrolled1, tvNumberWaitlisted1;

    private AppPreferences _appPrefs;

    public static ClassInformationFragment newInstance(int position, String strSessionName) {
        ClassInformationFragment fragment = new ClassInformationFragment();
        Bundle args = new Bundle();
        //Student list position
        args.putInt("Position", position);
        args.putString("SessionName", strSessionName);

        fragment.setArguments(args);

        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        oGlobal = new Globals();
        _appPrefs = new AppPreferences(activity);
        position = getArguments().getInt("Position");

        schoolClassList = _appPrefs.getSchoolClassList();
        oSchoolClass = schoolClassList.get(position);


    }

    public ClassInformationFragment() {
    }

    //Create the view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_class_information, container, false);

        tvWhichSession = (TextView)rootView.findViewById(R.id.tvWhichSession);

        tvClassType1 = (TextView)rootView.findViewById(R.id.tvClassType1);
        tvClassLevel1 = (TextView)rootView.findViewById(R.id.tvClassLevel1);
        tvClassDescription1 = (TextView)rootView.findViewById(R.id.tvClassDescription1);
        tvNumberEnrolled1 = (TextView)rootView.findViewById(R.id.tvNumberEnrolled1);
        tvNumberWaitlisted1 = (TextView)rootView.findViewById(R.id.tvNumberWaitlisted1);

        tvClassType1.setText(oSchoolClass.ClType );
        tvClassLevel1.setText(oSchoolClass.ClLevel);
        tvClassDescription1.setText(oSchoolClass.ClDescription);
        tvNumberEnrolled1.setText(oSchoolClass.ClInstructor);
        tvNumberWaitlisted1.setText(oSchoolClass.ClWait);
        tvWhichSession.setText("This class is in session: " + strSessionName );
        return rootView;

    }

}

