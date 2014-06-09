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
import com.akadasoftware.danceworksonline.classes.Globals;
import com.akadasoftware.danceworksonline.classes.Globals.Data;
import com.akadasoftware.danceworksonline.classes.Session;
import com.akadasoftware.danceworksonline.classes.Student;
import com.akadasoftware.danceworksonline.classes.StudentAttendance;
import com.akadasoftware.danceworksonline.classes.User;

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
    Globals oGlobal;
    User oUser;
    static SoapSerializationEnvelope envelopeOutput;
    String METHOD_NAME = "";
    String SOAP_ACTION = "";
    Student oStudent;
    Session oSession;

    ArrayList<Session> sessionArrayList = new ArrayList<Session>();
    SessionAdapter sessionAdapter;

    ArrayList<StudentAttendance> studentAttenanceArray = new ArrayList<StudentAttendance>();
    private StudentAttendanceAdapter attendanceAdapter;

    ArrayList<Student> students = new ArrayList<Student>();

    private OnAttendanceInteractionListener mListener;

    int position, SessionID;

    Spinner sessionAttendanceSpinner;


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


        students = _appPrefs.getStudents();
        position = getArguments().getInt("Position");

        oStudent = students.get(position);
        oUser = _appPrefs.getUser();

        oGlobal = new Globals();
    }

    @Override
    public void onResume() {
        super.onResume();


        getSessionsAsync getSessions = new getSessionsAsync();
        getSessions.execute();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_attendance, container, false);

        sessionAttendanceSpinner = (Spinner) rootView.findViewById(R.id.sessionAttendanceSpinner);
        return rootView;
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

    //Asycn task to get the ChgDesc field to be used to populate the spinner
    public class getSessionsAsync extends
            AsyncTask<Data, Void, ArrayList<Session>> {

        @Override
        protected ArrayList<Session> doInBackground(Data... data) {

            SoapObject session = oGlobal.getSoapRequest(Data.NAMESPACE, "getSessions");
            session = oGlobal.setSessionPropertyInfo(session, oStudent.SchID, "getSessions", oUser);
            return oGlobal.RetrieveSessionsFromSoap(session);


        }

        protected void onPostExecute(ArrayList<Session> result) {
            sessionArrayList = result;
            addItemsOnSpinner(sessionArrayList);

        }
    }


    //Adds all items from the Session field to the spinner
    public void addItemsOnSpinner(ArrayList<Session> sess) {

        sessionAdapter = new SessionAdapter(activity,
                R.layout.fragment_student_attendance, sess);

        sessionAttendanceSpinner.setAdapter(sessionAdapter);

        sessionAttendanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setSelectedSession(sessionAttendanceSpinner);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Handles if the selected field for the spinner
    public void setSelectedSession(Spinner sessionSpinner) {

        int selected = sessionSpinner.getSelectedItemPosition();
        oSession = (Session) sessionSpinner.getItemAtPosition(selected);
        SessionID = oSession.SessionID;

        getStudentAttendanceAsync getStudentAttendance = new getStudentAttendanceAsync();
        getStudentAttendance.execute();
    }


    public class getStudentAttendanceAsync extends
            AsyncTask<Data, Void, ArrayList<StudentAttendance>> {

        @Override
        protected ArrayList<StudentAttendance> doInBackground(Data... data) {

            return getAttendance();
        }

        protected void onPostExecute(ArrayList<StudentAttendance> result) {

            studentAttenanceArray = result;
            attendanceAdapter = new StudentAttendanceAdapter(activity,
                    R.layout.item_student_attendance, studentAttenanceArray);
            setListAdapter(attendanceAdapter);
            attendanceAdapter.setNotifyOnChange(true);


        }
    }

    public ArrayList<StudentAttendance> getAttendance() {
        String MethodName = "getStudentAttendance";
        SoapObject response = InvokeMethod(Data.URL, MethodName);
        return RetrieveFromSoap(response);

    }

    public SoapObject InvokeMethod(String URL, String MethodName) {

        SoapObject request = GetSoapObject(MethodName);

        PropertyInfo piUserID = new PropertyInfo();
        piUserID.setName("UserID");
        piUserID.setValue(oUser.UserID);
        request.addProperty(piUserID);

        PropertyInfo piUserGUID = new PropertyInfo();
        piUserGUID.setType("STRING_CLASS");
        piUserGUID.setName("UserGUID");
        piUserGUID.setValue(oUser.UserGUID);
        request.addProperty(piUserGUID);

        PropertyInfo piStuID = new PropertyInfo();
        piStuID.setName("StuID");
        piStuID.setValue(oStudent.StuID);
        request.addProperty(piStuID);

        PropertyInfo piSessionID = new PropertyInfo();
        piSessionID.setName("SessionID");
        piSessionID.setValue(SessionID);
        request.addProperty(piSessionID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        return MakeCall(URL, envelope, Data.NAMESPACE, MethodName);
    }

    public static SoapObject GetSoapObject(String MethodName) {
        return new SoapObject(Data.NAMESPACE, MethodName);
    }

    public static SoapObject MakeCall(String URL,
                                      SoapSerializationEnvelope envelope, String NAMESPACE,
                                      String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        try {
            envelope.addMapping(NAMESPACE, "StudentAttendance",
                    new StudentAttendance().getClass());
            HttpTransport.call(METHOD_NAME, envelope);
            envelopeOutput = envelope;
            SoapObject response = (SoapObject) envelope.getResponse();

            return response;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static ArrayList<StudentAttendance> RetrieveFromSoap(SoapObject soap) {

        ArrayList<StudentAttendance> stuAttendance = new ArrayList<StudentAttendance>();
        for (int i = 0; i < soap.getPropertyCount(); i++) {

            SoapObject attendanceItem = (SoapObject) soap.getProperty(i);

            StudentAttendance attendance = new StudentAttendance();
            for (int j = 0; j < attendanceItem.getPropertyCount(); j++) {
                attendance.setProperty(j, attendanceItem.getProperty(j)
                        .toString());
                if (attendanceItem.getProperty(j).equals("anyType{}")) {
                    attendanceItem.setProperty(j, "");
                }

            }
            stuAttendance.add(i, attendance);
        }

        return stuAttendance;
    }

}
