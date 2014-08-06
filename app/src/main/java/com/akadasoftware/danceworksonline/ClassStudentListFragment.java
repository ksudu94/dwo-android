package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.akadasoftware.danceworksonline.Adapters.StudentListAdapter;
import com.akadasoftware.danceworksonline.Classes.AppPreferences;
import com.akadasoftware.danceworksonline.Classes.Globals;
import com.akadasoftware.danceworksonline.Classes.SchoolClasses;
import com.akadasoftware.danceworksonline.Classes.Student;
import com.akadasoftware.danceworksonline.Classes.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;


public class ClassStudentListFragment extends ListFragment {

    int classPosition;

    Globals oGlobals;
    User oUser;
    Student oStudent;
    SchoolClasses oSchoolClass;

    ArrayList<SchoolClasses> schoolClassList;
    ArrayList<Student> studentsArray = new ArrayList<Student>();
    ArrayList<String> conflictsArray = new ArrayList<String>();

    private AppPreferences _appPrefs;
    Activity activity;

    StudentListAdapter stuListAdapter;

    //private OnClassListInteractionListener mListener;

    private onClassStudentListDialog dListener;

    Button btnDone;

    /**
     * public interface OnClassListInteractionListener {
     * public void onClassListInteraction(Uri uri); }
     */

    public interface onClassStudentListDialog {
        public void onClassStudentListDialog(ArrayList<String> conflictsArray, SchoolClasses oSchoolClass, Student oStudent, int position);
    }

    public static ClassStudentListFragment newInstance(int position) {
        ClassStudentListFragment fragment = new ClassStudentListFragment();
        Bundle args = new Bundle();
        args.putInt("Position", position);
        fragment.setArguments(args);
        return fragment;
    }

    public ClassStudentListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();


        oGlobals = new Globals();
        _appPrefs = new AppPreferences(activity);
        oUser = _appPrefs.getUser();
        classPosition = _appPrefs.getClassListPosition();

        schoolClassList = _appPrefs.getSchoolClassList();
        oSchoolClass = schoolClassList.get(classPosition);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_class_list_of_students, container, false);
        btnDone = (Button) view.findViewById(R.id.btnDone);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openClassPage = new Intent("com.akadasoftware.danceworksonline.ClassInformation");
                startActivity(openClassPage);
            }
        });
        return view;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            //mListener = (OnClassListInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        try {
            dListener = (onClassStudentListDialog) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onClassStudentListDialog");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getStudentsListAsync getStudentsList = new getStudentsListAsync();
        getStudentsList.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
        dListener = null;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        super.onListItemClick(l, v, position, id);

        oStudent = (Student) this.getListAdapter().getItem(position);

        checkClassConflicts conflictCheck = new checkClassConflicts();
        conflictCheck.execute();

        dListener.onClassStudentListDialog(conflictsArray, oSchoolClass, oStudent, classPosition);
    }


    /**
     * Gets student list
     */
    private class getStudentsListAsync extends
            AsyncTask<Globals.Data, Void, ArrayList<Student>> {
        ProgressDialog progress;

        protected void onPreExecute() {
            progress = ProgressDialog.show(activity, "Refreshing List", "Loading...", true);
        }

        protected ArrayList<Student> doInBackground(Globals.Data... data) {
            return oGlobals.getStudents(_appPrefs, 0);
        }

        protected void onPostExecute(ArrayList<Student> result) {
            progress.dismiss();
            _appPrefs.saveStudents(result);
            studentsArray = result;
            stuListAdapter = new StudentListAdapter(activity,
                    R.layout.item_studentlist, studentsArray);
            setListAdapter(stuListAdapter);
            stuListAdapter.setNotifyOnChange(true);
        }

    }


    /**
     * Checking if class that is to be enrolled conflicks with any other previously registered
     * Classes
     */
    public class checkClassConflicts extends AsyncTask<Globals.Data, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(Globals.Data... data) {

            return CheckConflicts();
        }

        protected void onPostExecute(ArrayList<String> result) {
            conflictsArray = result;

        }
    }

    public ArrayList<String> CheckConflicts() {
        String MethodName = "checkClassEnrollment";
        SoapObject response = InvokeEnrollmentMethod(Globals.Data.URL, MethodName);
        return RetrieveConflictsFromSoap(response);

    }

    public static SoapObject GetSoapObject(String MethodName) {
        return new SoapObject(Globals.Data.NAMESPACE, MethodName);
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
        piClDayNo.setName("strClDayNo");
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
        piSessionID.setValue(oSchoolClass.SessionID);
        request.addProperty(piSessionID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        return MakeConflictCall(URL, envelope, Globals.Data.NAMESPACE, MethodName);
    }

    public static SoapObject MakeConflictCall(String URL,
                                              SoapSerializationEnvelope envelope, String NAMESPACE,
                                              String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        SoapObject response = null;
        try {

            HttpTransport.call(METHOD_NAME, envelope);
            response = (SoapObject) envelope.getResponse();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static ArrayList<String> RetrieveConflictsFromSoap(SoapObject soap) {

        ArrayList<String> strConflictsArray = new ArrayList<String>();

        for (int i = 0; i < soap.getPropertyCount(); i++) {

            if (soap.getProperty(i).equals("anyType{}"))
                strConflictsArray.add(i, "");
            else
                strConflictsArray.add(i, soap.getProperty(i).toString());

        }

        return strConflictsArray;
    }


}
