package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.Globals;
import com.akadasoftware.danceworksonline.classes.SchoolClasses;
import com.akadasoftware.danceworksonline.classes.Session;
import com.akadasoftware.danceworksonline.classes.User;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.ArrayList;

public class ClassesListFragment extends ListFragment {


    Globals oGlobal;
    Activity activity;
    User oUser;
    SchoolClasses oSchoolClass;
    private AppPreferences _appPrefs;
    Session session;
    int SessionID, position;
    String METHOD_NAME = "";
    String SOAP_ACTION = "";
    static SoapSerializationEnvelope envelopeOutput;


    ArrayList<Session> sessionArrayList = new ArrayList<Session>();
    SessionAdapter sessionAdapter;

    ArrayList<SchoolClasses> schoolClasssesArray = new ArrayList<SchoolClasses>();

    ArrayList<String> conflicksArray = new ArrayList<String>();

    Spinner sessionClassSpinner;
    private SchoolClassAdapter classAdapter;

    private OnClassesInteractionListener classListener;


    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static ClassesListFragment newInstance(int position) {
        ClassesListFragment fragment = new ClassesListFragment();
        Bundle args = new Bundle();
        args.putInt("Position", position);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ClassesListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        _appPrefs = new AppPreferences(activity);
        oUser = _appPrefs.getUser();
        oGlobal = new Globals();

        classAdapter = new SchoolClassAdapter(getActivity(),
                R.layout.item_studentclass, schoolClasssesArray);
        setListAdapter(classAdapter);
        classAdapter.setNotifyOnChange(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classeslistfragment_list, container, false);

        sessionClassSpinner = (Spinner) view.findViewById(R.id.sessionClassSpinner);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            classListener = (OnClassesInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnClassesInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        classListener = null;
    }


    @Override
    public void onResume() {
        super.onResume();

        getSessionsAsync getSessions = new getSessionsAsync();
        getSessions.execute();

    }

    /**
     * Creates a new instance
     */
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        super.onListItemClick(l, v, position, id);
        oSchoolClass = (SchoolClasses) this.getListAdapter().getItem(position);


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
    public interface OnClassesInteractionListener {
        // TODO: Update argument type and name
        public void onClassesInteraction(String id);
    }

    class Data {

        private static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }

    //Asycn task to get the ChgDesc field to be used to populate the spinner
    public class getSessionsAsync extends
            AsyncTask<Globals.Data, Void, ArrayList<Session>> {

        @Override
        protected ArrayList<Session> doInBackground(Globals.Data... data) {

            SoapObject session = oGlobal.getSoapRequest(Globals.Data.NAMESPACE, "getSessions");
            session = oGlobal.setSessionPropertyInfo(session, oUser.SchID, "getSessions", oUser);
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

        sessionClassSpinner.setAdapter(sessionAdapter);

        sessionClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setSelectedSession(sessionClassSpinner);

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

            return oGlobal.getClasses(_appPrefs, SessionID);
        }

        protected void onPostExecute(ArrayList<SchoolClasses> result) {

            schoolClasssesArray = result;
            classAdapter = new SchoolClassAdapter(getActivity(),
                    R.layout.item_studentclass, schoolClasssesArray);
            setListAdapter(classAdapter);
            classAdapter.setNotifyOnChange(true);

        }
    }

}
