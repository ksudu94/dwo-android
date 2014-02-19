package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.akadasoftware.danceworksonline.classes.AccountTransactions;
import com.akadasoftware.danceworksonline.classes.AppPreferences;
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
public class AccountTransactionsFragment extends ListFragment {


    ArrayList<AccountTransactions> TransactionArray = new ArrayList<AccountTransactions>();
    private static AppPreferences _appPrefs;
    String METHOD_NAME = "";
    static String SOAP_ACTION = "getAccountTransactions";
    static SoapSerializationEnvelope envelopeOutput;
    Activity activity;
    static User user;


    private OnTransactionSelected mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private AccountTransactionAdapter transAdapter;

    //Method defined in AccountInformation
    public interface OnTransactionSelected {
        // TODO: Update argument type and name
        public void OnTransactionSelected(int id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        activity = getActivity();
        _appPrefs = new AppPreferences(activity);
        getAccountTransactions trans = new getAccountTransactions();
        trans.execute();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTransactionSelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTransactionSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container,
                savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_accounttransactions_list, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setListAdapter(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Notify the parent activity of selected item
        mListener.OnTransactionSelected(position);
    }

    class Data {

        private static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }

    public class getAccountTransactions extends
            AsyncTask<Data, Void, ArrayList<AccountTransactions>> {

        @Override
        protected ArrayList<AccountTransactions> doInBackground(Data... data) {

            return getTransactions();
        }

        protected void onPostExecute(ArrayList<AccountTransactions> result) {

            TransactionArray = result;
            transAdapter = new AccountTransactionAdapter(getActivity(),
                    R.layout.item_transactionlist, TransactionArray);
            setListAdapter(transAdapter);
            transAdapter.setNotifyOnChange(true);

        }
    }

    public static ArrayList<AccountTransactions> getTransactions() {
        String MethodName = "getAccountTransactions";
        SoapObject response = InvokeMethod(Data.URL, MethodName);
        return RetrieveFromSoap(response);

    }

    public static SoapObject InvokeMethod(String URL, String MethodName) {

        SoapObject request = GetSoapObject(MethodName);
        user = _appPrefs.getUser();

        PropertyInfo Order = new PropertyInfo();
        Order.setType("STRING_CLASS");
        Order.setName("Order");
        Order.setValue(" ORDER BY TDate");
        request.addProperty(Order);

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

    public static SoapObject MakeCall(String URL,
                                      SoapSerializationEnvelope envelope, String NAMESPACE,
                                      String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        try {
            envelope.addMapping(Data.NAMESPACE, "AccountTransactions",
                    new AccountTransactions().getClass());
            HttpTransport.call(SOAP_ACTION, envelope);
            envelopeOutput = envelope;
            SoapObject response = (SoapObject) envelope.getResponse();

            return response;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public static ArrayList<AccountTransactions> RetrieveFromSoap(SoapObject soap) {

        ArrayList<AccountTransactions> AccountTrans = new ArrayList<AccountTransactions>();
        for (int i = 0; i < soap.getPropertyCount() - 1; i++) {

            SoapObject accounttransitem = (SoapObject) soap.getProperty(i);

            AccountTransactions trans = new AccountTransactions();
            for (int j = 0; j < accounttransitem.getPropertyCount() - 1; j++) {
                trans.setProperty(j, accounttransitem.getProperty(j)
                        .toString());
                if (accounttransitem.getProperty(j).equals("anyType{}")) {
                    accounttransitem.setProperty(j, "");
                }

            }
            AccountTrans.add(i, trans);
        }

        return AccountTrans;
    }

}
