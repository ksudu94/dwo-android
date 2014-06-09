package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.Globals;
import com.akadasoftware.danceworksonline.classes.SchoolClasses;
import com.akadasoftware.danceworksonline.classes.Session;
import com.akadasoftware.danceworksonline.classes.Student;
import com.akadasoftware.danceworksonline.classes.StudentClasses;
import com.akadasoftware.danceworksonline.classes.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by Kyle on 5/14/2014.
 */
public class StudentEnrollFragment extends ListFragment {

    private AppPreferences _appPrefs;
    String METHOD_NAME = "";
    String SOAP_ACTION = "";
    static SoapSerializationEnvelope envelopeOutput;
    Activity activity;
    Student oStudent;
    User oUser;
    SchoolClasses oSchoolClass;
    Session session;
    int SessionID, position;
    Globals oGlobal;

    Button btnDone;

    private OnStudentEnrollListener enrollListener;

    ArrayList<SchoolClasses> schoolClassesArray = new ArrayList<SchoolClasses>();
    ArrayList<Student> Students = new ArrayList<Student>();

    ArrayList<String> conflicksArray = new ArrayList<String>();

    ArrayList<Student> studentsArray = new ArrayList<Student>();
    ArrayList<Session> sessionArrayList = new ArrayList<Session>();
    SessionAdapter sessionAdapter;

    Spinner sessionStudentEnrollSpinner;
    private SchoolClassAdapter classAdapter;

    private onEnrollDialog dialogListener;


    public interface onEnrollDialog {

        public void onEnrollDialog(SchoolClasses objSchoolClass, Student oStudent, ArrayList<String> conflicksArray);
    }

    public interface OnStudentEnrollListener {

        public void onStudentEnrollInteraction(String id);
    }

    public StudentEnrollFragment() {
    }

    // TODO: Rename and change types of parameters
    public static StudentEnrollFragment newInstance(int position) {
        StudentEnrollFragment fragment = new StudentEnrollFragment();
        Bundle args = new Bundle();
        args.putInt("Position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        _appPrefs = new AppPreferences(activity);

        studentsArray = _appPrefs.getStudents();
        position = _appPrefs.getStudentListPosition();

        oStudent = studentsArray.get(position);
        oUser = _appPrefs.getUser();
        oGlobal = new Globals();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container,
                savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_studentenroll, container, false);

        btnDone = (Button) view.findViewById(R.id.btnDone);
        sessionStudentEnrollSpinner = (Spinner) view.findViewById(R.id.sessionStudentEnrollSpinner);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openStudentPage = new Intent("com.akadasoftware.danceworksonline.StudentInformation");
                startActivity(openStudentPage);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getSessionsAsync getSessions = new getSessionsAsync();
        getSessions.execute();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            enrollListener = (OnStudentEnrollListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onStudentEnrollInteraction");
        }

        try {
            dialogListener = (onEnrollDialog) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onEnrollDialog");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        enrollListener = null;
    }


    /**
     * Creates a new instance
     */
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        super.onListItemClick(l, v, position, id);
        oSchoolClass = (SchoolClasses) this.getListAdapter().getItem(position);

        checkClassConflicks checkConflicks = new checkClassConflicks();
        checkConflicks.execute();


    }

    //Asycn task to get the ChgDesc field to be used to populate the spinner
    public class getSessionsAsync extends
            AsyncTask<Globals.Data, Void, ArrayList<Session>> {

        @Override
        protected ArrayList<Session> doInBackground(Globals.Data... data) {

            SoapObject session = oGlobal.getSoapRequest(Globals.Data.NAMESPACE, "getSessions");
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
                R.layout.fragment_studentenroll, sess);

        sessionStudentEnrollSpinner.setAdapter(sessionAdapter);

        sessionStudentEnrollSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setSelectedSession(sessionStudentEnrollSpinner);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Handles if the selected field for the spinner
    public void setSelectedSession(Spinner sessionSpinner) {

        int selected = sessionSpinner.getSelectedItemPosition();
        session = (Session) sessionSpinner.getItemAtPosition(selected);
        SessionID = session.SessionID;

        getStudentClassesAsync getStudentClass = new getStudentClassesAsync();
        getStudentClass.execute();

    }


    public class getStudentClassesAsync extends
            AsyncTask<Globals.Data, Void, ArrayList<SchoolClasses>> {

        @Override
        protected ArrayList<SchoolClasses> doInBackground(Globals.Data... data) {

            return getClasses();
        }

        protected void onPostExecute(ArrayList<SchoolClasses> result) {

            schoolClassesArray = result;
            classAdapter = new SchoolClassAdapter(getActivity(),
                    R.layout.item_studentclass, schoolClassesArray);
            setListAdapter(classAdapter);
            classAdapter.setNotifyOnChange(true);

        }
    }

    public ArrayList<SchoolClasses> getClasses() {
        String MethodName = "getSchoolClasses";
        SoapObject response = InvokeMethod(Globals.Data.URL, MethodName);
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

        PropertyInfo piSessionID = new PropertyInfo();
        piSessionID.setName("intSessionID");
        piSessionID.setValue(SessionID);
        request.addProperty(piSessionID);

        PropertyInfo piBoolEnroll = new PropertyInfo();
        piBoolEnroll.setName("boolEnroll");
        piBoolEnroll.setValue(true);
        request.addProperty(piBoolEnroll);

        PropertyInfo piStuID = new PropertyInfo();
        piStuID.setName("intStuID");
        piStuID.setValue(oStudent.StuID);
        request.addProperty(piStuID);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        return MakeCall(URL, envelope, Globals.Data.NAMESPACE, MethodName);
    }

    public static SoapObject GetSoapObject(String MethodName) {
        return new SoapObject(Globals.Data.NAMESPACE, MethodName);
    }

    public static SoapObject MakeCall(String URL,
                                      SoapSerializationEnvelope envelope, String NAMESPACE,
                                      String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        try {
            envelope.addMapping(Globals.Data.NAMESPACE, "SchoolClasses",
                    new StudentClasses().getClass());
            HttpTransport.call("getSchoolClasses", envelope);
            envelopeOutput = envelope;
            SoapObject response = (SoapObject) envelope.getResponse();

            return response;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static ArrayList<SchoolClasses> RetrieveFromSoap(SoapObject soap) {

        ArrayList<SchoolClasses> schClassesArray = new ArrayList<SchoolClasses>();
        for (int i = 0; i < soap.getPropertyCount() - 1; i++) {

            SoapObject classItem = (SoapObject) soap.getProperty(i);

            SchoolClasses classes = new SchoolClasses();
            for (int j = 0; j < classItem.getPropertyCount() - 1; j++) {
                classes.setProperty(j, classItem.getProperty(j)
                        .toString());
                if (classItem.getProperty(j).equals("anyType{}")) {
                    classItem.setProperty(j, "");
                }

            }
            schClassesArray.add(i, classes);
        }

        return schClassesArray;
    }


    public class checkClassConflicks extends AsyncTask<Globals.Data, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Globals.Data... data) {

            return CheckConflicks();
        }

        protected void onPostExecute(ArrayList<String> result) {

            conflicksArray = result;
            dialogListener.onEnrollDialog(oSchoolClass, oStudent, conflicksArray);

        }
    }

    public ArrayList<String> CheckConflicks() {
        String MethodName = "checkClassEnrollment";
        SoapObject response = InvokeEnrollmentMethod(Globals.Data.URL, MethodName);
        return RetrieveConflicksFromSoap(response);

    }

    public static SoapObject MakeConflickCall(String URL,
                                              SoapSerializationEnvelope envelope, String NAMESPACE,
                                              String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        try {
            envelope.addMapping(Globals.Data.NAMESPACE, "SchoolClasses",
                    new SchoolClasses().getClass());
            HttpTransport.call(METHOD_NAME, envelope);
            envelopeOutput = envelope;
            SoapObject response = (SoapObject) envelope.getResponse();

            return response;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public SoapObject InvokeEnrollmentMethod(String URL, String MethodName) {

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

        PropertyInfo piClMax = new PropertyInfo();
        piClMax.setName("intClMax");
        piClMax.setValue(oSchoolClass.ClMax);
        request.addProperty(piClMax);

        PropertyInfo piClCur = new PropertyInfo();
        piClCur.setName("intClCur");
        piClCur.setValue(oSchoolClass.ClCur);
        request.addProperty(piClCur);

        PropertyInfo piClTime = new PropertyInfo();
        piClTime.setName("strClTime");
        piClTime.setValue(oSchoolClass.ClTime);
        request.addProperty(piClTime);

        PropertyInfo piClStop = new PropertyInfo();
        piClStop.setName("strClStop");
        piClStop.setValue(oSchoolClass.ClStop);
        request.addProperty(piClStop);

        PropertyInfo piMultiDay = new PropertyInfo();
        piMultiDay.setName("boolMultiDay");
        piMultiDay.setValue(oSchoolClass.MultiDay);
        request.addProperty(piMultiDay);

        PropertyInfo piMonday = new PropertyInfo();
        piMonday.setName("boolMonday");
        piMonday.setValue(oSchoolClass.Monday);
        request.addProperty(piMonday);

        PropertyInfo piTuesday = new PropertyInfo();
        piTuesday.setName("boolTuesday");
        piTuesday.setValue(oSchoolClass.Tuesday);
        request.addProperty(piTuesday);

        PropertyInfo piWednesday = new PropertyInfo();
        piWednesday.setName("boolWednesday");
        piWednesday.setValue(oSchoolClass.Wednesday);
        request.addProperty(piWednesday);

        PropertyInfo piThursday = new PropertyInfo();
        piThursday.setName("boolThursday");
        piThursday.setValue(oSchoolClass.Thursday);
        request.addProperty(piThursday);

        PropertyInfo piFriday = new PropertyInfo();
        piFriday.setName("boolFriday");
        piFriday.setValue(oSchoolClass.Friday);
        request.addProperty(piFriday);

        PropertyInfo piSaturday = new PropertyInfo();
        piSaturday.setName("boolSaturday");
        piSaturday.setValue(oSchoolClass.Saturday);
        request.addProperty(piSaturday);

        PropertyInfo piSunday = new PropertyInfo();
        piSunday.setName("boolSunday");
        piSunday.setValue(oSchoolClass.Sunday);
        request.addProperty(piSunday);

        PropertyInfo piClDayNo = new PropertyInfo();
        piClDayNo.setName("intClDayNo");
        piClDayNo.setValue(oSchoolClass.ClDayNo);
        request.addProperty(piClDayNo);

        PropertyInfo piStuID = new PropertyInfo();
        piStuID.setName("intStuID");
        piStuID.setValue(oStudent.StuID);
        request.addProperty(piStuID);

        PropertyInfo piClID = new PropertyInfo();
        piClID.setName("intClID");
        piClID.setValue(oSchoolClass.ClID);
        request.addProperty(piClID);

        PropertyInfo piSessionID = new PropertyInfo();
        piSessionID.setName("intSessionID");
        piSessionID.setValue(SessionID);
        request.addProperty(piSessionID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        return MakeConflickCall(URL, envelope, Globals.Data.NAMESPACE, MethodName);
    }

    public static ArrayList<String> RetrieveConflicksFromSoap(SoapObject soap) {

        ArrayList<String> strConflicksArray = new ArrayList<String>();
        for (int i = 0; i < soap.getPropertyCount(); i++) {

            //SoapObject conflickItem = (SoapObject) soap.getProperty(i);
            if (soap.getProperty(i).equals("anyType{}"))
                strConflicksArray.add(i, "");
            else
                strConflicksArray.add(i, soap.getProperty(i).toString());

        }

        return strConflicksArray;
    }

}
