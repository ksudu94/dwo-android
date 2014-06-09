package com.akadasoftware.danceworksonline.classes;

import android.app.Activity;
import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

/**
 * Created by Kyle on 4/7/2014.
 * Globally available methods that you might use anywhere.
 */
public class Globals {

    private static AppPreferences _appPrefs;

    /**
     * Builds the query string for filter and select by dialogs
     */
    public String BuildQuery(int selection, int sort, String mTitle) {

        String Query, Sort = "", Select = "";

        if (mTitle.equals("Accounts") || mTitle.equals("Home")) {
            switch (sort) {
                case 0:
                    Sort = " ORDER BY LName,FName,AcctNo";
                    break;
                case 1:
                    Sort = " ORDER BY FName,LName,AcctNo";
                    break;
                case 2:
                    Sort = " ORDER BY AcctNo";
                    break;
                case 3:
                    Sort = " ORDER BY Phone,LName,FName,AcctNo";
                    break;
                case 4:
                    Sort = " ORDER BY Email,LName,FName,AcctNo";
                    break;
                default:
                    Sort = " ORDER BY LName,FName,AcctNo";
                    break;
            }

            switch (selection) {
                case 0:
                    Select = " AND Status=0";
                    break;
                case 1:
                    Select = " AND Status=1";
                    break;
                case 2:
                    Select = " AND (Status=0 or Status=1)";
                    break;
                case 3:
                    Select = " AND Status=2";
                    break;
                case 4:
                    Select = " AND Status=3";
                    break;
                default:
                    Select = "";
                    break;
            }
        } else if (mTitle.equals("Students")) {
            switch (sort) {
                case 0:
                    Sort = " ORDER BY LName,FName,StuID";
                    break;
                case 1:
                    Sort = " ORDER BY FName,LName,StuID";
                    break;
                case 2:
                    Sort = " ORDER BY Phone,LName,FName,StuID";
                    break;
                case 3:
                    Sort = "ORDER BY Email,LName,FName,StuID";
                    break;
                case 4:
                    Sort = " ORDER BY Birthdate,LName,FName,StuID";
                    break;
                case 5:
                    Sort = " ORDER BY AcctNo,StuID";
                    break;
            }

            switch (selection) {
                case 0:
                    Select = " AND Status=0";
                    break;
                case 1:
                    Select = " AND Status=1";
                    break;
                case 2:
                    Select = " AND (Status=0 or Status=1)";
                    break;
                case 3:
                    Select = " AND Status=2";
                    break;
                default:
                    Select = "";
                    break;

            }
        }
        Query = Select + Sort;


        return Query;
    }

    /**
     * Updates the account and then resaves it into the arraylist
     */

    public void updateAccount(Account account, int position, Activity activity) {

        _appPrefs = new AppPreferences(activity);
        ArrayList<Account> AccountsArray = new ArrayList<Account>();
        AccountsArray = _appPrefs.getAccounts();
        AccountsArray.set(position, account);
        _appPrefs.saveAccounts(AccountsArray);

    }

    /**
     * Creates a new soap object
     */
    public static SoapObject GetSoapObject(String NAMESPACE, String MethodName) {
        return new SoapObject(NAMESPACE, MethodName);
    }

    public class Data {

        public static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        public static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }


    /**
     * Creates a new soap request
     */
    public SoapObject getSoapRequest(String NameSpace, String MethodName) {
        SoapObject Request = new SoapObject(NameSpace, MethodName);

        return Request;
    }

    /**
     * Property info's for the session b/c we use it in a spinner on several pages.
     */
    public SoapObject setSessionPropertyInfo(SoapObject objRequest, int SchID, String SoapAction, User inputUser) {

        PropertyInfo piUserID = new PropertyInfo();
        piUserID.setName("UserID");
        piUserID.setValue(inputUser.UserID);
        objRequest.addProperty(piUserID);

        PropertyInfo piUserGUID = new PropertyInfo();
        piUserGUID.setType("STRING_CLASS");
        piUserGUID.setName("UserGUID");
        piUserGUID.setValue(inputUser.UserGUID);
        objRequest.addProperty(piUserGUID);

        PropertyInfo Order = new PropertyInfo();
        Order.setName("Order");
        Order.setValue(" WHERE SchID= " + SchID + "AND DisplaySession='True' ORDER BY SDate,EDate,SessionName");
        objRequest.addProperty(Order);

        objRequest = getNewSoapObject(SoapAction, objRequest);

        return objRequest;
    }

    public SoapObject getNewSoapObject(String SoapAction, SoapObject Request) {

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        envelope.dotNet = true;
        envelope.setOutputSoapObject(Request);

        SoapObject response = null;
        HttpTransportSE HttpTransport = new HttpTransportSE(Data.URL);
        try {
            HttpTransport.call(SoapAction, envelope);
            response = (SoapObject) envelope.getResponse();

        } catch (Exception e) {

            e.printStackTrace();
        }
        return response;

    }

    public static ArrayList<Session> RetrieveSessionsFromSoap(SoapObject soap) {

        ArrayList<Session> sessionArray = new ArrayList<Session>();

        for (int i = 0; i < soap.getPropertyCount(); i++) {

            SoapObject sessionItem = (SoapObject) soap.getProperty(i);

            Session Session = new Session();
            for (int j = 0; j < sessionItem.getPropertyCount(); j++) {
                Session.setProperty(j, sessionItem.getProperty(j)
                        .toString());
                if (sessionItem.getProperty(j).equals("anyType{}")) {
                    sessionItem.setProperty(j, "");
                }

            }
            sessionArray.add(i, Session);
        }

        return sessionArray;
    }

    private class getStudentsList extends AsyncTask<Data, Void, ArrayList<Student>> {

        protected ArrayList<Student> doInBackground(Data... data) {
            return getStudents();
        }

        protected void onPostExecute(ArrayList<Student> result) {
            _appPrefs.saveStudents(result);
        }

    }

    public static ArrayList<Student> getStudents() {
        String MethodName = "getStudents";
        SoapObject response = InvokeMethod(Data.URL, MethodName);
        return RetrieveFromSoap(response);
    }

    public static SoapObject InvokeMethod(String URL, String MethodName) {

        SoapObject request = GetSoapObject(MethodName);

        User oUser = _appPrefs.getUser();
        String strStudentQuery = _appPrefs.getStudentQuery();

        PropertyInfo piWhere = new PropertyInfo();
        piWhere.setType("STRING_CLASS");
        piWhere.setName("Where");
        piWhere.setValue(strStudentQuery);
        request.addProperty(piWhere);

        PropertyInfo piSchID = new PropertyInfo();
        piSchID.setName("SchID");
        piSchID.setValue(oUser.SchID);
        request.addProperty(piSchID);

        PropertyInfo piAcctID = new PropertyInfo();
        piAcctID.setName("AcctID");
        piAcctID.setValue(0);
        request.addProperty(piAcctID);

        PropertyInfo piUserID = new PropertyInfo();
        piUserID.setName("UserID");
        piUserID.setValue(oUser.UserID);
        request.addProperty(piUserID);

        PropertyInfo piUserGUID = new PropertyInfo();
        piUserGUID.setType("STRING_CLASS");
        piUserGUID.setName("UserGUID");
        piUserGUID.setValue(oUser.UserGUID);
        request.addProperty(piUserGUID);

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
            HttpTransport.call("getStudents", envelope);
            SoapSerializationEnvelope envelopeOutput = envelope;
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
            for (int j = 0; j < studentlistitem.getPropertyCount(); j++) {
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


    public static ArrayList<Account> getAccounts(AppPreferences _appPrefsNew) {
        _appPrefs = _appPrefsNew;
        String MethodName = "getAccounts";
        SoapObject response = InvokeAccountMethod(Data.URL, MethodName);
        return RetrieveAccountsFromSoap(response);

    }

    public static SoapObject InvokeAccountMethod(String URL, String MethodName) {

        SoapObject requestAccounts = GetAccountSoapObject(MethodName);

        User oUser = _appPrefs.getUser();
        String strAccontQuery = _appPrefs.getAccountQuery();

        PropertyInfo piOrder = new PropertyInfo();
        piOrder.setType("STRING_CLASS");
        piOrder.setName("Order");
        piOrder.setValue(strAccontQuery);
        requestAccounts.addProperty(piOrder);

        PropertyInfo piSchID = new PropertyInfo();
        piSchID.setName("SchID");
        piSchID.setValue(oUser.SchID);
        requestAccounts.addProperty(piSchID);

        PropertyInfo piUserID = new PropertyInfo();
        piUserID.setName("UserID");
        piUserID.setValue(oUser.UserID);
        requestAccounts.addProperty(piUserID);

        PropertyInfo piUserGUID = new PropertyInfo();
        piUserGUID.setType("STRING_CLASS");
        piUserGUID.setName("UserGUID");
        piUserGUID.setValue(oUser.UserGUID);
        requestAccounts.addProperty(piUserGUID);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(requestAccounts);
        return MakeAccountCall(URL, envelope, Data.NAMESPACE, MethodName);
    }

    public static SoapObject GetAccountSoapObject(String MethodName) {
        return new SoapObject(Data.NAMESPACE, MethodName);
    }

    public static SoapObject MakeAccountCall(String URL,
                                             SoapSerializationEnvelope envelope, String NAMESPACE,
                                             String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        try {
            envelope.addMapping(Data.NAMESPACE, "Account",
                    new Account().getClass());
            HttpTransport.call("getAccounts", envelope);
            SoapSerializationEnvelope envelopeOutput = envelope;
            SoapObject responseAccounts = (SoapObject) envelope.getResponse();

            return responseAccounts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Account> RetrieveAccountsFromSoap(SoapObject soap) {

        ArrayList<Account> Accounts = new ArrayList<Account>();
        for (int i = 0; i < soap.getPropertyCount() - 1; i++) {

            SoapObject accountlistitem = (SoapObject) soap.getProperty(i);
            Account account = new Account();
            for (int j = 0; j < accountlistitem.getPropertyCount(); j++) {
                account.setProperty(j, accountlistitem.getProperty(j)
                        .toString());
                if (accountlistitem.getProperty(j).equals("anyType{}")) {
                    accountlistitem.setProperty(j, "");
                }

            }
            Accounts.add(i, account);
        }

        return Accounts;
    }

}
