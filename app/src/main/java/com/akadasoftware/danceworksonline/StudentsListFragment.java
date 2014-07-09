package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.akadasoftware.danceworksonline.Adapters.StudentListAdapter;
import com.akadasoftware.danceworksonline.Classes.AppPreferences;
import com.akadasoftware.danceworksonline.Classes.Globals;
import com.akadasoftware.danceworksonline.Classes.Student;
import com.akadasoftware.danceworksonline.Classes.User;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Displays a list of activity students
 */
public class StudentsListFragment extends ListFragment implements AbsListView.OnItemClickListener {


    ArrayList<Student> studentsArray = new ArrayList<Student>();
    private AppPreferences _appPrefs;
    String METHOD_NAME = "";
    static String SOAP_ACTION = "getStudents";
    static String strQuery = "";
    static SoapSerializationEnvelope envelopeOutput;
    Activity activity;
    static User oUser;
    Globals oGlobals;


    StudentListAdapter stuListAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;

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
        _appPrefs = new AppPreferences(activity);
        studentsArray = _appPrefs.getStudents();
        strQuery = _appPrefs.getStudentQuery();
        oGlobals = new Globals();

        /**
         * The loading of the students list is done on the splash/login screens
         */
        stuListAdapter = new StudentListAdapter(activity,
                R.layout.item_studentlist, studentsArray);
        setListAdapter(stuListAdapter);
        stuListAdapter.setNotifyOnChange(true);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup viewGroup = (ViewGroup) view;

        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        ActionBarPullToRefresh.from(activity)

                // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
                .insertLayoutInto(viewGroup)

                        // We need to mark the ListView and it's Empty View as pull-able
                        // This is because they are not direct children of the ViewGroup
                .theseChildrenArePullable(getListView(), getListView().getEmptyView())

                        // Set the OnRefreshListener
                .options(Options.create().refreshOnUp(true).build())


                .listener(new OnRefreshListener() {
                    @Override
                    public void onRefreshStarted(View view) {
                        getStudentsListAsync getStudents = new getStudentsListAsync();
                        getStudents.execute();
                        mPullToRefreshLayout.setRefreshComplete();

                    }

                })

                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

    }

    /**
     * When fragment detaches from activity ie different screen, saves the instance state so that it
     * can be restored later when returned to fragment
     *
     * @param activity
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


}
