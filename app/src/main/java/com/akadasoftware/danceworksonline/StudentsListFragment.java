package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.Globals;
import com.akadasoftware.danceworksonline.classes.Student;
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
public class StudentsListFragment extends ListFragment implements AbsListView.OnItemClickListener {


    ArrayList<Student> studentsArray = new ArrayList<Student>();
    private AppPreferences _appPrefs;
    String METHOD_NAME = "";
    static String SOAP_ACTION = "getStudents";
    static String strQuery = "";
    static SoapSerializationEnvelope envelopeOutput;
    Activity activity;
    static User user;
    Globals globals;


    StudentListAdapter stuListAdapter;

    private OnStudentInteractionListener mListener;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
    public interface OnStudentInteractionListener {
        // TODO: Update argument type and name
        public void onStudentSelected(int id);
    }


    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StudentsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        globals = new Globals();
        _appPrefs = new AppPreferences(activity);
        studentsArray = _appPrefs.getStudent();
        strQuery = _appPrefs.getStudentQuery();
        if (strQuery.length() == 0) {
            strQuery = globals.BuildQuery(_appPrefs.getStudentSelectBy(), _appPrefs.getStudentSortBy(), "Students");
        }
        user = _appPrefs.getUser();


        if (studentsArray.size() > 0) {
            stuListAdapter = new StudentListAdapter(activity,
                    R.layout.item_studentlist, studentsArray);
            setListAdapter(stuListAdapter);
            stuListAdapter.setNotifyOnChange(true);
        } else {
            getStudentsList stuList = new getStudentsList();
            stuList.execute();
        }


    }

    /**
     * Change the divider line color
     *
     * @Override public void onActivityCreated(Bundle savedInstanceState) {
     * super.onActivityCreated(savedInstanceState);
     * ColorDrawable light_blue = new ColorDrawable(this.getResources().getColor(R.color.light_blue));
     * getListView().setDivider(light_blue);
     * getListView().setDividerHeight(2);
     * }
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStudentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnStudentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        mListener.onStudentSelected(position);
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText("There are no students in this school.");
        }
    }

    class Data {

        private static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }

    private class getStudentsList extends AsyncTask<Data, Void, ArrayList<Student>> {

        protected ArrayList<Student> doInBackground(Data... data) {
            return getStudents();
        }

        protected void onPostExecute(ArrayList<Student> result) {
            studentsArray = result;
            stuListAdapter = new StudentListAdapter(activity, R.layout.item_studentlist, studentsArray);
            setListAdapter(stuListAdapter);
            _appPrefs.saveStudents(result);
            stuListAdapter.setNotifyOnChange(true);

        }
    }

    public static ArrayList<Student> getStudents() {
        String MethodName = "getStudents";
        SoapObject response = InvokeMethod(Data.URL, MethodName);
        return RetrieveFromSoap(response);
    }

    public static SoapObject InvokeMethod(String URL, String MethodName) {

        SoapObject request = GetSoapObject(MethodName);

        PropertyInfo Where = new PropertyInfo();
        Where.setType("STRING_CLASS");
        Where.setName("Where");
        Where.setValue(strQuery);
        request.addProperty(Where);

        PropertyInfo SchID = new PropertyInfo();
        SchID.setName("SchID");
        SchID.setValue(user.SchID);
        request.addProperty(SchID);

        PropertyInfo AcctID = new PropertyInfo();
        AcctID.setName("AcctID");
        AcctID.setValue(0);
        request.addProperty(AcctID);

        PropertyInfo UserID = new PropertyInfo();
        UserID.setName("UserID");
        UserID.setValue(user.UserID);
        request.addProperty(UserID);

        PropertyInfo UserGUID = new PropertyInfo();
        UserGUID.setType("STRING_CLASS");
        UserGUID.setName("UserGUID");
        UserGUID.setValue(user.UserGUID);
        request.addProperty(UserGUID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        return MakeCall(URL, envelope, Data.NAMESPACE, MethodName);
    }

    public static SoapObject GetSoapObject(String MethodName) {
        return new SoapObject(Data.NAMESPACE, MethodName);
    }

    public static SoapObject MakeCall(String URL, SoapSerializationEnvelope envelope, String NAMESPACE,
                                      String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        try {
            envelope.addMapping(Data.NAMESPACE, "Student",
                    new Student().getClass());
            HttpTransport.call(SOAP_ACTION, envelope);
            envelopeOutput = envelope;
            SoapObject response = (SoapObject) envelope.getResponse();

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Student> RetrieveFromSoap(SoapObject soap) {
        ArrayList<Student> Students = new ArrayList<Student>();
        for (int i = 0; i < soap.getPropertyCount() - 1; i++) {

            SoapObject studentlistitem = (SoapObject) soap.getProperty(i);
            Student student = new Student();
            for (int j = 0; j < studentlistitem.getPropertyCount() - 1; j++) {
                student.setProperty(j, studentlistitem.getProperty(j)
                        .toString());
                if (studentlistitem.getProperty(j).equals("anyType{}")) {
                    studentlistitem.setProperty(j, "");
                }

            }
            Students.add(i, student);
        }

        return Students;
    }


}
