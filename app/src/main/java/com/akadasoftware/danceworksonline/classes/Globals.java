package com.akadasoftware.danceworksonline.classes;

import android.app.Activity;
import android.widget.Spinner;

import com.akadasoftware.danceworksonline.SessionAdapter;

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

        String Query, strSort = "", strSelect = "";

        if (mTitle.equals("Accounts") || mTitle.equals("Home")) {
            switch (sort) {
                case 0:
                    strSort = " ORDER BY LName,FName,AcctNo";
                    break;
                case 1:
                    strSort = " ORDER BY FName,LName,AcctNo";
                    break;
                case 2:
                    strSort = " ORDER BY AcctNo";
                    break;
                case 3:
                    strSort = " ORDER BY Phone,LName,FName,AcctNo";
                    break;
                case 4:
                    strSort = " ORDER BY Email,LName,FName,AcctNo";
                    break;
                default:
                    strSort = " ORDER BY LName,FName,AcctNo";
                    break;
            }

            switch (selection) {
                case 0:
                    strSelect = " AND Status=0";
                    break;
                case 1:
                    strSelect = " AND Status=1";
                    break;
                case 2:
                    strSelect = " AND (Status=0 or Status=1)";
                    break;
                case 3:
                    strSelect = " AND Status=2";
                    break;
                case 4:
                    strSelect = " AND Status=3";
                    break;
                default:
                    strSelect = "";
                    break;
            }
        } else if (mTitle.equals("Students")) {
            switch (sort) {
                case 0:
                    strSort = " ORDER BY LName,FName,StuID";
                    break;
                case 1:
                    strSort = " ORDER BY FName,LName,StuID";
                    break;
                case 2:
                    strSort = " ORDER BY Phone,LName,FName,StuID";
                    break;
                case 3:
                    strSort = "ORDER BY Email,LName,FName,StuID";
                    break;
                case 4:
                    strSort = " ORDER BY Birthdate,LName,FName,StuID";
                    break;
                case 5:
                    strSort = " ORDER BY AcctNo,StuID";
                    break;
            }

            switch (selection) {
                case 0:
                    strSelect = " AND Status=0";
                    break;
                case 1:
                    strSelect = " AND Status=1";
                    break;
                case 2:
                    strSelect = " AND (Status=0 or Status=1)";
                    break;
                case 3:
                    strSelect = " AND Status=2";
                    break;
                default:
                    strSelect = "";
                    break;

            }
        }
        Query = strSelect + strSort;


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
     * Updates the student and then resaves it into the arraylist
     */

    public void updateStudent(Student oStudent, int position, Activity activity) {

        _appPrefs = new AppPreferences(activity);
        ArrayList<Student> StudentsArray = new ArrayList<Student>();
        StudentsArray = _appPrefs.getStudents();
        StudentsArray.set(position, oStudent);
        _appPrefs.saveStudents(StudentsArray);

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

    public ArrayList<Session> getSessions(int intSchID, int intUserID, String UserGUID) {
        String MethodName = "getSessions";
        SoapObject response = InvokeSessionMethod(Data.URL, MethodName, intSchID, intUserID, UserGUID);
        return RetrieveSessionsFromSoap(response);
    }

    public static SoapObject InvokeSessionMethod(String URL, String METHOD_NAME, int intSchID, int intUserID, String UserGUID) {

        SoapObject requestSession = new SoapObject(Data.NAMESPACE, METHOD_NAME);

        PropertyInfo piUserID = new PropertyInfo();
        piUserID.setName("UserID");
        piUserID.setValue(intUserID);
        requestSession.addProperty(piUserID);

        PropertyInfo piUserGUID = new PropertyInfo();
        piUserGUID.setType("STRING_CLASS");
        piUserGUID.setName("UserGUID");
        piUserGUID.setValue(UserGUID);
        requestSession.addProperty(piUserGUID);

        PropertyInfo Order = new PropertyInfo();
        Order.setName("Order");
        Order.setValue(" WHERE SchID= " + intSchID + "AND DisplaySession='True' ORDER BY SDate,EDate,SessionName");
        requestSession.addProperty(Order);

        SoapSerializationEnvelope envelopeSessions = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        envelopeSessions.dotNet = true;
        envelopeSessions.setOutputSoapObject(requestSession);

        envelopeSessions.dotNet = true;
        return MakeSessionCall(URL, envelopeSessions, Data.NAMESPACE, METHOD_NAME);
    }

    public static SoapObject MakeSessionCall(String URL,
                                             SoapSerializationEnvelope envelope, String NAMESPACE,
                                             String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        SoapObject responseSession = null;
        try {
            envelope.addMapping(Data.NAMESPACE, "Session",
                    new Session().getClass());
            HttpTransport.call(METHOD_NAME, envelope);
            responseSession = (SoapObject) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseSession;
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

    /**
     * Sets the spinner to display the current session for the school
     *
     * @param spinnerSession
     * @param oSchool
     */
    public void setCurrentSession(Spinner spinnerSession, School oSchool) {
        SessionAdapter adapter = (SessionAdapter) spinnerSession.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {

            Session oSession = null;
            try {
                oSession = (Session) spinnerSession.getItemAtPosition(position);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (oSession.SessionID == oSchool.SessionID) {
                spinnerSession.setSelection(position);
                break;
            }
        }

    }

    /**
     * Get User used on initial login screen
     */

    public User getUser(AppPreferences _appPrefsNew, String strEmail, String strPassword) {
        _appPrefs = _appPrefsNew;
        String MethodName = "getUser";
        SoapObject response = InvokeNewUserMethod(Data.URL, MethodName, strEmail, strPassword);
        return RetrieveUserFromSoap(response);

    }


    public static SoapObject InvokeNewUserMethod(String URL, String METHOD_NAME, String strEmail, String strPassword) {

        SoapObject requestNewUser = new SoapObject(Data.NAMESPACE, METHOD_NAME);

        PropertyInfo piEmail = new PropertyInfo();
        piEmail.setType("STRING_CLASS");
        piEmail.setName("email");
        piEmail.setValue(strEmail);
        requestNewUser.addProperty(piEmail);

        PropertyInfo piPassword = new PropertyInfo();
        piPassword.setType("STRING_CLASS");
        piPassword.setName("password");
        piPassword.setValue(strPassword);
        requestNewUser.addProperty(piPassword);

        SoapSerializationEnvelope envelopeNewUser = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        envelopeNewUser.dotNet = true;
        envelopeNewUser.setOutputSoapObject(requestNewUser);

        envelopeNewUser.dotNet = true;
        return MakeUserCall(URL, envelopeNewUser, Data.NAMESPACE, METHOD_NAME);
    }

    public static SoapObject MakeUserCall(String URL,
                                          SoapSerializationEnvelope envelope, String NAMESPACE,
                                          String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        SoapObject responseUser = null;
        try {
            envelope.addMapping(Data.NAMESPACE, "User",
                    new User().getClass());
            HttpTransport.call(METHOD_NAME, envelope);
            responseUser = (SoapObject) envelope.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseUser;
    }

    public static User RetrieveUserFromSoap(SoapObject soap) {
        User inputUser = new User();
        for (int i = 0; i < soap.getPropertyCount(); i++) {
            inputUser.setProperty(i, soap.getProperty(i).toString());
        }
        return inputUser;
    }


    /**
     * Get User by ID used on splash screen, uses the same MakeUserCall and RetrieveUserFromSoap
     * methods as the getUser async on initial login
     */

    public static User getUserByID(AppPreferences _appPrefsNew) {
        _appPrefs = _appPrefsNew;
        String MethodName = "getUserByID";
        SoapObject response = InvokeUserMethod(Data.URL, MethodName);
        return RetrieveUserFromSoap(response);

    }

    public static SoapObject InvokeUserMethod(String URL, String MethodName) {
        User oUser = _appPrefs.getUser();
        SoapObject requestUser = GetSoapObject(MethodName);

        PropertyInfo piUserID = new PropertyInfo();
        piUserID.setName("UserID");
        piUserID.setValue(oUser.UserID);
        requestUser.addProperty(piUserID);

        PropertyInfo piUserGUID = new PropertyInfo();
        piUserGUID.setName("UserGUID");
        piUserGUID.setValue(oUser.UserGUID);
        requestUser.addProperty(piUserGUID);

        SoapSerializationEnvelope envelopeUser = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        envelopeUser.dotNet = true;
        envelopeUser.setOutputSoapObject(requestUser);

        envelopeUser.dotNet = true;
        return MakeUserCall(URL, envelopeUser, Data.NAMESPACE, MethodName);
    }


    /**
     * Get School
     */

    public static School getSchool(AppPreferences _appPrefsNew) {
        _appPrefs = _appPrefsNew;
        String MethodName = "getSchool";
        SoapObject response = InvokeSchoolMethod(Data.URL, MethodName);
        return RetrieveSchoolFromSoap(response);

    }

    public static SoapObject InvokeSchoolMethod(String URL, String METHOD_NAME) {
        User oUser = _appPrefs.getUser();
        SoapObject requestSchool = new SoapObject(Data.NAMESPACE,
                METHOD_NAME);

        PropertyInfo piSchID = new PropertyInfo();
        piSchID.setName("SchID");
        piSchID.setValue(oUser.SchID);
        requestSchool.addProperty(piSchID);

        PropertyInfo userid = new PropertyInfo();
        userid.setName("UserID");
        userid.setValue(oUser.UserID);
        requestSchool.addProperty(userid);

        PropertyInfo userguid = new PropertyInfo();
        userguid.setName("UserGUID");
        userguid.setValue(oUser.UserGUID);
        requestSchool.addProperty(userguid);

        SoapSerializationEnvelope envelopeSchool = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        envelopeSchool.dotNet = true;
        envelopeSchool.setOutputSoapObject(requestSchool);

        envelopeSchool.dotNet = true;
        return MakeSchoolCall(URL, envelopeSchool, Data.NAMESPACE, METHOD_NAME);
    }

    public static SoapObject MakeSchoolCall(String URL,
                                            SoapSerializationEnvelope envelope, String NAMESPACE,
                                            String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        SoapObject responseSchool = null;
        try {
            envelope.addMapping(Data.NAMESPACE, "School",
                    new School().getClass());
            HttpTransport.call(METHOD_NAME, envelope);
            responseSchool = (SoapObject) envelope.getResponse();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseSchool;
    }

    public static School RetrieveSchoolFromSoap(SoapObject soap) {
        School inputSchool = new School();
        for (int i = 0; i < soap.getPropertyCount(); i++) {
            inputSchool.setProperty(i, soap.getProperty(i).toString());
        }
        return inputSchool;
    }


    /**
     * Get accounts list
     */
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
        SoapObject responseAccounts = null;
        try {
            envelope.addMapping(Data.NAMESPACE, "Account",
                    new Account().getClass());
            HttpTransport.call(METHOD_NAME, envelope);
            responseAccounts = (SoapObject) envelope.getResponse();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseAccounts;
    }

    public static ArrayList<Account> RetrieveAccountsFromSoap(SoapObject soap) {

        ArrayList<Account> Accounts = new ArrayList<Account>();
        for (int i = 0; i < soap.getPropertyCount(); i++) {

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


    /**
     * Get students list
     */

    public static ArrayList<Student> getStudents(AppPreferences _appPrefsNew, int intAcctID) {
        _appPrefs = _appPrefsNew;
        String MethodName = "getStudents";
        SoapObject response = InvokeMethod(Data.URL, MethodName, intAcctID);
        return RetrieveStudentsFromSoap(response);
    }

    public static SoapObject InvokeMethod(String URL, String MethodName, int intAcctID) {

        SoapObject request = GetSoapObject(MethodName);

        Globals oGlobals = new Globals();
        User oUser = _appPrefs.getUser();
        String strStudentQuery = "";
        /**
         * Loads students for one particular account
         */
        if (intAcctID > 0) {
            strStudentQuery = oGlobals.BuildQuery(4, _appPrefs.getStudentSortBy(), "Students");
        }
        /**
         * Loads all students
         */
        else {
            strStudentQuery = _appPrefs.getStudentQuery();
        }


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
        piAcctID.setValue(intAcctID);
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
        SoapObject response = null;
        try {
            envelope.addMapping(Data.NAMESPACE, "Student",
                    new Student().getClass());
            HttpTransport.call(METHOD_NAME, envelope);
            SoapSerializationEnvelope envelopeOutput = envelope;
            response = (SoapObject) envelope.getResponse();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static ArrayList<Student> RetrieveStudentsFromSoap(SoapObject soap) {
        ArrayList<Student> Students = new ArrayList<Student>();
        for (int i = 0; i < soap.getPropertyCount(); i++) {

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

    /**
     * Get class list
     */

    public ArrayList<SchoolClasses> getClasses(AppPreferences _appPrefsNew, int intSessionID, int intStuID) {
        _appPrefs = _appPrefsNew;
        String MethodName = "getSchoolClasses";
        SoapObject response = InvokeClassMethod(Globals.Data.URL, MethodName, intSessionID, intStuID);
        return RetrieveClassFromSoap(response);

    }

    public SoapObject InvokeClassMethod(String URL, String MethodName, int intSessionID, int intStuID) {

        SoapObject request = GetClassSoapObject(MethodName);

        User oUser = _appPrefs.getUser();

        PropertyInfo piUserID = new PropertyInfo();
        piUserID.setName("UserID");
        piUserID.setValue(oUser.UserID);
        request.addProperty(piUserID);

        PropertyInfo piUserGUID = new PropertyInfo();
        piUserGUID.setName("UserGUID");
        piUserGUID.setValue(oUser.UserGUID);
        request.addProperty(piUserGUID);

        PropertyInfo piSessionID = new PropertyInfo();
        piSessionID.setName("intSessionID");
        piSessionID.setValue(intSessionID);
        request.addProperty(piSessionID);

        PropertyInfo piBoolEnroll = new PropertyInfo();
        piBoolEnroll.setName("boolEnroll");
        piBoolEnroll.setValue(true);
        request.addProperty(piBoolEnroll);

        PropertyInfo piStuID = new PropertyInfo();
        piStuID.setName("intStuID");
        piStuID.setValue(intStuID);
        request.addProperty(piStuID);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        return MakeClassCall(URL, envelope, Globals.Data.NAMESPACE, MethodName);
    }

    public static SoapObject GetClassSoapObject(String MethodName) {
        return new SoapObject(Globals.Data.NAMESPACE, MethodName);
    }

    public static SoapObject MakeClassCall(String URL,
                                           SoapSerializationEnvelope envelope, String NAMESPACE,
                                           String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        SoapObject response = null;
        try {
            envelope.addMapping(Globals.Data.NAMESPACE, "SchoolClasses",
                    new SchoolClasses().getClass());
            HttpTransport.call(METHOD_NAME, envelope);
            response = (SoapObject) envelope.getResponse();


        } catch (Exception e) {
            e.printStackTrace();

        }
        return response;
    }

    public static ArrayList<SchoolClasses> RetrieveClassFromSoap(SoapObject soap) {

        ArrayList<SchoolClasses> schClassesArray = new ArrayList<SchoolClasses>();
        for (int i = 0; i < soap.getPropertyCount(); i++) {

            SoapObject classItem = (SoapObject) soap.getProperty(i);

            SchoolClasses classes = new SchoolClasses();
            for (int j = 0; j < classItem.getPropertyCount(); j++) {
                classes.setProperty(j, classItem.getProperty(j)
                        .toString());
                if (classItem.getProperty(j).equals("anyType{}")) {
                    classItem.setProperty(j, "");
                }

            }
            schClassesArray.add(i, classes);
        }

        return schClassesArray;
    }


}
