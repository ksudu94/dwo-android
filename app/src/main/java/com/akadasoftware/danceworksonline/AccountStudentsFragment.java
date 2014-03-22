package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.Student;

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
 */
public class AccountStudentsFragment extends ListFragment implements AbsListView.OnItemClickListener {


    ArrayList<Student> StudentsArray = new ArrayList<Student>();
    ArrayList<Account> arrayAccounts;
    private AppPreferences _appPrefs;
    private OnStudentFragmentInteractionListener mListener;
    private AccountStudentsAdapter stuAdapter;
    static SoapSerializationEnvelope envelopeOutput;
    String METHOD_NAME = "";
    static String SOAP_ACTION = "getStudentsByAccountID";
    Activity activity;
    Account account;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static AccountStudentsFragment newInstance(int position) {
        AccountStudentsFragment fragment = new AccountStudentsFragment();
        Bundle args = new Bundle();
        args.putInt("Position", position);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AccountStudentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = getActivity();
        _appPrefs = new AppPreferences(activity);

        arrayAccounts = _appPrefs.getAccounts();
        int position = getArguments().getInt("Position");

        account = arrayAccounts.get(position);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container,
                savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_accountstudents_list, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setListAdapter(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStudentFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnStudentFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAccountStudents students = new getAccountStudents();
        students.execute();
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    class Data {

        private static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }

    public class getAccountStudents extends
            AsyncTask<Data, Void, ArrayList<Student>> {

        @Override
        protected ArrayList<Student> doInBackground(Data... data) {

            return getStudents();
        }

        protected void onPostExecute(ArrayList<Student> students) {

            StudentsArray = students;
            stuAdapter = new AccountStudentsAdapter(activity,
                    R.layout.item_accountstudents, StudentsArray);
            setListAdapter(stuAdapter);
            stuAdapter.setNotifyOnChange(true);

        }
    }

    public ArrayList<Student> getStudents() {
        String MethodName = "getStudentsByAccountID";
        SoapObject response = InvokeMethod(Data.URL, MethodName);
        return RetrieveFromSoap(response);

    }

    public SoapObject InvokeMethod(String URL, String MethodName) {

        SoapObject request = GetSoapObject(MethodName);

        PropertyInfo AcctID = new PropertyInfo();
        AcctID.setName("AcctID");
        AcctID.setValue(_appPrefs.getAcctID());
        request.addProperty(AcctID);

        PropertyInfo UserID = new PropertyInfo();
        UserID.setName("UserID");
        UserID.setValue(_appPrefs.getUserID());
        request.addProperty(UserID);

        PropertyInfo UserGUID = new PropertyInfo();
        UserGUID.setType("STRING_CLASS");
        UserGUID.setName("UserGUID");
        UserGUID.setValue(_appPrefs.getUserGUID());
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

    public static SoapObject MakeCall(String URL,
                                      SoapSerializationEnvelope envelope, String NAMESPACE,
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

            SoapObject studentitem = (SoapObject) soap.getProperty(i);

            Student students = new Student();
            for (int j = 0; j < studentitem.getPropertyCount() - 1; j++) {
                students.setProperty(j, studentitem.getProperty(j)
                        .toString());
                if (studentitem.getProperty(j).equals("anyType{}")) {
                    studentitem.setProperty(j, "");
                }

            }
            Students.add(i, students);
        }

        return Students;
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
    public interface OnStudentFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onStudentFragmentInteraction(String id);
    }

}
