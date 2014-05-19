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
import com.akadasoftware.danceworksonline.classes.Session;
import com.akadasoftware.danceworksonline.classes.Student;
import com.akadasoftware.danceworksonline.classes.StudentClass;
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
    Session session;
    int SessionID, position;
    Globals oGlobal;

    Button btnDone;

    private OnStudentEnrollListener enrollListener;

    ArrayList<StudentClass> studentClassArray = new ArrayList<StudentClass>();
    ArrayList<Student> Students = new ArrayList<Student>();

    ArrayList<Student> studentsArray = new ArrayList<Student>();
    ArrayList<Session> sessionArrayList = new ArrayList<Session>();
    SessionAdapter sessionAdapter;

    Spinner sessionStudentEnrollSpinner;
    private StudentClassAdapter classAdapter;

    private onEnrollDialog dialogListener;


    public interface onEnrollDialog {

        public void onEnrollDialog(StudentEnrollFragment objStudentEnroll);
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

        studentsArray = _appPrefs.getStudent();
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
     * Creates a new instance of the AccountTransaction = to the position of the listview
     */
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        super.onListItemClick(l, v, position, id);
        StudentEnrollFragment oStudentEnroll = (StudentEnrollFragment) this.getListAdapter().getItem(position);
        dialogListener.onEnrollDialog(oStudentEnroll);

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
                R.layout.fragment_studentclass_list, sess);

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
            AsyncTask<Globals.Data, Void, ArrayList<StudentClass>> {

        @Override
        protected ArrayList<StudentClass> doInBackground(Globals.Data... data) {

            return getClasses();
        }

        protected void onPostExecute(ArrayList<StudentClass> result) {

            studentClassArray = result;
            classAdapter = new StudentClassAdapter(getActivity(),
                    R.layout.item_studentclass, studentClassArray);
            setListAdapter(classAdapter);
            classAdapter.setNotifyOnChange(true);

        }
    }

    public ArrayList<StudentClass> getClasses() {
        String MethodName = "getStuClasses";
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

        PropertyInfo piStuID = new PropertyInfo();
        piStuID.setName("StuID");
        piStuID.setValue(oStudent.StuID);
        request.addProperty(piStuID);

        PropertyInfo piSessionID = new PropertyInfo();
        piSessionID.setName("SessionID");
        piSessionID.setValue(SessionID);
        request.addProperty(piSessionID);

        PropertyInfo piOLReg = new PropertyInfo();
        piOLReg.setName("OLReg");
        piOLReg.setValue(false);
        request.addProperty(piOLReg);

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
            envelope.addMapping(Globals.Data.NAMESPACE, "StudentClass",
                    new StudentClass().getClass());
            HttpTransport.call("getStuClasses", envelope);
            envelopeOutput = envelope;
            SoapObject response = (SoapObject) envelope.getResponse();

            return response;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static ArrayList<StudentClass> RetrieveFromSoap(SoapObject soap) {

        ArrayList<StudentClass> stuClassesArray = new ArrayList<StudentClass>();
        for (int i = 0; i < soap.getPropertyCount() - 1; i++) {

            SoapObject classItem = (SoapObject) soap.getProperty(i);

            StudentClass classes = new StudentClass();
            for (int j = 0; j < classItem.getPropertyCount() - 1; j++) {
                classes.setProperty(j, classItem.getProperty(j)
                        .toString());
                if (classItem.getProperty(j).equals("anyType{}")) {
                    classItem.setProperty(j, "");
                }

            }
            stuClassesArray.add(i, classes);
        }

        return stuClassesArray;
    }

}
