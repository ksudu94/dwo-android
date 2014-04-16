package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.Session;
import com.akadasoftware.danceworksonline.classes.Student;
import com.akadasoftware.danceworksonline.classes.StudentClass;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;


/**
 *
 *
 */
public class StudentAttendanceFragment extends ListFragment {

    Activity activity;
    private AppPreferences _appPrefs;
    String METHOD_NAME = "";
    String SOAP_ACTION = "";
    Student student;
    Session sessions;

    ArrayList<Session> sessionArrayList = new ArrayList<Session>();
    SessionAdapter sessionAdapter;
    ArrayList<StudentClass> classes = new ArrayList<StudentClass>();
    ArrayList<Student> students = new ArrayList<Student>();

    private OnAttendanceInteractionListener mListener;

    int position;

    Spinner sessionSpinner;


    public static StudentAttendanceFragment newInstance(int position) {
        StudentAttendanceFragment fragment = new StudentAttendanceFragment();
        Bundle args = new Bundle();
        args.putInt("Position", position);

        fragment.setArguments(args);
        return fragment;
    }

    public StudentAttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        _appPrefs = new AppPreferences(activity);

        students = _appPrefs.getStudent();
        position = getArguments().getInt("Position");

        student = students.get(position);

    }

    @Override
    public void onResume() {
        super.onResume();
        getSessionsAsync getSess = new getSessionsAsync();
        getSess.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_attendance, container, false);

        sessionSpinner = (Spinner) rootView.findViewById(R.id.sessionSpinner);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAttendanceInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAttendanceInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAttendanceInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnAttendanceInteractionListener {
        // TODO: Update argument type and name
        public void onAttendanceInteraction(Uri uri);
    }

    class Data {

        static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }

    //Asycn task to get the ChgDesc field to be used to populate the spinner
    public class getSessionsAsync extends
            AsyncTask<Data, Void, ArrayList<Session>> {

        @Override
        protected ArrayList<Session> doInBackground(Data... data) {
            SoapObject session = getSessions();
            return RetrieveSessionsFromSoap(session);

        }

        protected void onPostExecute(ArrayList<Session> result) {
            sessionArrayList = result;
            addItemsOnSpinner(sessionArrayList);

        }
    }

    public SoapObject getSessions() {
        SOAP_ACTION = "getSessions";
        METHOD_NAME = "getSessions";

        SoapObject Request = new SoapObject(Data.NAMESPACE, METHOD_NAME);


        PropertyInfo Order = new PropertyInfo();
        Order.setName("Order");
        Order.setValue(" WHERE SchID= " + student.SchID + "AND DisplaySession='True' ORDER BY SDate,EDate,SessionName");
        Request.addProperty(Order);

     /*   PropertyInfo UserID = new PropertyInfo();
        UserID.setName("UserID");
        UserID.setValue(_appPrefs.getUserID());
        Request.addProperty(UserID);

        PropertyInfo UserGUID = new PropertyInfo();
        UserGUID.setName("UserGUID");
        UserGUID.setValue(_appPrefs.getUserGUID());
        Request.addProperty(UserGUID);
*/

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        envelope.dotNet = true;
        envelope.setOutputSoapObject(Request);

        SoapObject response = null;
        HttpTransportSE HttpTransport = new HttpTransportSE(Data.URL);
        try {
            HttpTransport.call(SOAP_ACTION, envelope);

            response = (SoapObject) envelope.getResponse();

        } catch (Exception e) {

            e.printStackTrace();
        }
        return response;

    }

    public static ArrayList<Session> RetrieveSessionsFromSoap(SoapObject soap) {

        ArrayList<Session> sessionArray = new ArrayList<Session>();

        for (int i = 0; i < soap.getPropertyCount(); i++) {

            SoapObject soapsession = (SoapObject) soap.getProperty(i);

            Session Session = new Session();
            for (int j = 0; j < soapsession.getPropertyCount(); j++) {
                Session.setProperty(j, soapsession.getProperty(j)
                        .toString());
                if (soapsession.getProperty(j).equals("anyType{}")) {
                    soapsession.setProperty(j, "");
                }

            }
            sessionArray.add(i, Session);
        }

        return sessionArray;
    }

    //Adds all items from the Session field to the spinner
    public void addItemsOnSpinner(ArrayList<Session> sess) {

        sessionAdapter = new SessionAdapter(activity,
                R.layout.fragment_student_attendance, sess);

        sessionSpinner.setAdapter(sessionAdapter);

        sessionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setSelectedSession(sessionSpinner);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Handles if the selected field for the spinner
    public void setSelectedSession(Spinner sessionSpinner) {

        int selected = sessionSpinner.getSelectedItemPosition();
        sessions = (Session) sessionSpinner.getItemAtPosition(selected);
    }


}
