package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
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
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class StudentClassFragment extends ListFragment {


    private AppPreferences _appPrefs;
    String METHOD_NAME = "";
    String SOAP_ACTION = "";
    static SoapSerializationEnvelope envelopeOutput;
    Activity activity;
    Student student;
    User user;
    ArrayList<StudentClass> classes = new ArrayList<StudentClass>();
    ArrayList<Student> students = new ArrayList<Student>();


    private OnStudentClassListener mListener;

    int position;


    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private StudentClassAdapter classAdapter;

    // TODO: Rename and change types of parameters
    public static StudentClassFragment newInstance(int position) {
        StudentClassFragment fragment = new StudentClassFragment();
        Bundle args = new Bundle();
        args.putInt("Position", position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StudentClassFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        _appPrefs = new AppPreferences(activity);

        students = _appPrefs.getStudent();
        position = getArguments().getInt("Position");

        student = students.get(position);

        getStudentClasses getClass = new getStudentClasses();
        getClass.execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container,
                savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_studentclass_list, container, false);

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getStudentClasses getClass = new getStudentClasses();
        getClass.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStudentClassListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onStudentClassInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setEmptyText(CharSequence emptyText) {
        ListView listView = (ListView) activity.findViewById(R.id.list);
        //listView.setEmptyView( activity.findViewById( R.id.empty_list_item ) );
        View emptyView = listView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
    public interface OnStudentClassListener {
        // TODO: Update argument type and name
        public void onStudentClassInteraction(String id);
    }

    class Data {

        private static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }

    public class getStudentClasses extends
            AsyncTask<Data, Void, ArrayList<StudentClass>> {

        @Override
        protected ArrayList<StudentClass> doInBackground(Data... data) {

            return getClasses();
        }

        protected void onPostExecute(ArrayList<StudentClass> result) {

            classes = result;
            classAdapter = new StudentClassAdapter(getActivity(),
                    R.layout.item_studentclass, result);
            setListAdapter(classAdapter);
            classAdapter.setNotifyOnChange(true);

        }
    }

    public ArrayList<StudentClass> getClasses() {
        String MethodName = "getStuClasses";
        SoapObject response = InvokeMethod(Data.URL, MethodName);
        return RetrieveFromSoap(response);

    }

    public SoapObject InvokeMethod(String URL, String MethodName) {

        SoapObject request = GetSoapObject(MethodName);
        user = _appPrefs.getUser();

        PropertyInfo StuID = new PropertyInfo();
        StuID.setName("StuID");
        StuID.setValue(student.StuID);
        request.addProperty(StuID);

        PropertyInfo SessionID = new PropertyInfo();
        SessionID.setName("SessionID");
        SessionID.setValue(_appPrefs.getSessionID());
        request.addProperty(SessionID);

        PropertyInfo OLReg = new PropertyInfo();
        OLReg.setName("OLReg");
        OLReg.setValue(false);
        request.addProperty(OLReg);

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
            envelope.addMapping(Data.NAMESPACE, "StudentClass",
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

        ArrayList<StudentClass> stuClasses = new ArrayList<StudentClass>();
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
            stuClasses.add(i, classes);
        }

        return stuClasses;
    }


}
