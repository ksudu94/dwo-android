package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.akadasoftware.danceworksonline.Adapters.AccountListAdapater;
import com.akadasoftware.danceworksonline.Classes.Account;
import com.akadasoftware.danceworksonline.Classes.AppPreferences;
import com.akadasoftware.danceworksonline.Classes.Globals;
import com.akadasoftware.danceworksonline.Classes.User;

import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class AccountListFragment extends ListFragment {

    ArrayList<Account> AccountsArray = new ArrayList<Account>();

    private AppPreferences _appPrefs;
    String METHOD_NAME = "";
    static String SOAP_ACTION = "getAccounts";
    static String strQuery = "";
    static SoapSerializationEnvelope envelopeOutput;
    private PullToRefreshLayout mPullToRefreshLayout;

    Activity activity;
    static User oUser;
    Globals oGlobals;
    /**
     * Listener to handle clicks on the list
     */
    private OnAccountSelectedListener mListener;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    AccountListAdapater acctListAdpater;


    /**
     * Creates a new instance of the Accountlist fragment and sets the arguments of that fragment to
     * a list of accounts. Creating a new AccountListFragment runs the onCreate method which then
     * checks whether the accountsarray is populated or not and takes the appropriate action. Bundle
     * args is used to send in arguments to the onCreate method (we think).
     */
    public interface OnAccountSelectedListener {
        // TODO: Update argument type and name
        public void OnAccountSelected(int id);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _appPrefs = new AppPreferences(getActivity());
        AccountsArray = _appPrefs.getAccounts();
        activity = getActivity();
        strQuery = _appPrefs.getAccountQuery();
        oGlobals = new Globals();

        /**
         * The loading of the accounts list is done on the splash/login screens
         */
        acctListAdpater = new AccountListAdapater(getActivity(),
                R.layout.item_accountlist, AccountsArray);
        setListAdapter(acctListAdpater);
        acctListAdpater.setNotifyOnChange(true);


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
                        getAccountsListAsync getAccounts = new getAccountsListAsync();
                        getAccounts.execute();
                        mPullToRefreshLayout.setRefreshComplete();

                    }

                })

                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        ListView listView = (ListView) activity.findViewById(R.id.list);
        View emptyView = listView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText("There ain't no accounts in here");
        }
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
            mListener = (OnAccountSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAccountSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Called when one of the accounts is selected in the accountlist page calling onAccountSelected
     * takes us to the onAccountSelected method on the home page were it saves the account position
     * in the list and then starts our new activity which is opening the AccountInformationFragment
     * page.
     */

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        mListener.OnAccountSelected(position);
    }

    class Data {

        private static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }

    /**
     * Gets account list
     */
    private class getAccountsListAsync extends
            AsyncTask<Globals.Data, Void, ArrayList<Account>> {

        ProgressDialog progress;

        protected void onPreExecute() {
            progress = ProgressDialog.show(activity, "Refreshing List", "Loading...", true);
        }

        @Override
        protected ArrayList<Account> doInBackground(Globals.Data... data) {

            return oGlobals.getAccounts(_appPrefs);
        }

        protected void onPostExecute(ArrayList<Account> result) {
            progress.dismiss();
            _appPrefs.saveAccounts(result);
            AccountsArray = result;
            acctListAdpater = new AccountListAdapater(getActivity(),
                    R.layout.item_accountlist, AccountsArray);
            setListAdapter(acctListAdpater);
            acctListAdpater.setNotifyOnChange(true);
        }
    }
}
