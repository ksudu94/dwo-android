package com.akadasoftware.danceworksonline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.School;
import com.akadasoftware.danceworksonline.classes.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

public class Splash extends ActionBarActivity {

    private static SharedPreferences loginPreferences;
    private static SharedPreferences.Editor loginEditor;
    private AppPreferences _appPrefs;
    User user = new User();
    String METHOD_NAME = "";
    String SOAP_ACTION = "";
    Integer UserID = 0;
    String UserGUID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*
         *Uses loginPreferences to get the loggedin field to check wether or not the user has a
         *saved profile. From there it either goes to the login screen or the home screen.
         */
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginEditor = loginPreferences.edit();
        Boolean loggedin = loginPreferences.getBoolean("loggedin", false);
        _appPrefs = new AppPreferences(getApplicationContext());


        Thread timer = new Thread() {
            public void run() {
                try {
                    //Displays splash image for 1 second
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //Checks if logged in and where to go next
                    Boolean loggedin = loginPreferences.getBoolean("loggedin", false);
                    if (loggedin) {
                        UserID = loginPreferences.getInt("UserID", 0);
                        if (UserID > 0) {
                            UserGUID = loginPreferences.getString("UserGUID", "");
                            new getUserByID().execute();
                        } else {
                            loginEditor.clear();
                            loginEditor.putBoolean("loggedin", false);
                            loginEditor.commit();

                        }
                    } else {

                        Intent openMainPage = new Intent("com.akadasoftware.danceworksonline.Login");
                        startActivity(openMainPage);
                    }

                }
            }
        };
        timer.start();

    }

    class Data {

        static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
        private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
    }

    //Finds users by ID
    public class getUserByID extends AsyncTask<Data, Void, User> {

        @Override
        protected User doInBackground(Data... data) {

            METHOD_NAME = "getUserByID";
            SOAP_ACTION = "getUserByID";

            SoapObject requestUser = new SoapObject(Data.NAMESPACE, METHOD_NAME);

            PropertyInfo piUserID = new PropertyInfo();
            piUserID.setName("UserID");
            piUserID.setValue(UserID);
            requestUser.addProperty(piUserID);

            PropertyInfo piUserGUID = new PropertyInfo();
            piUserGUID.setName("UserGUID");
            piUserGUID.setValue(UserGUID);
            requestUser.addProperty(piUserGUID);

            SoapSerializationEnvelope envelopeUser = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelopeUser.dotNet = true;
            envelopeUser.setOutputSoapObject(requestUser);

            HttpTransportSE httpTransport = new HttpTransportSE(Data.URL);

            try {
                httpTransport.call(SOAP_ACTION, envelopeUser);
                SoapObject responseUser = (SoapObject) envelopeUser
                        .getResponse();
                for (int i = 0; i < responseUser.getPropertyCount(); i++) {
                    user.setProperty(i, responseUser.getProperty(i).toString());
                }
                                            /*_appPrefs.saveEmail(strEmail);*/

                ArrayList<User> userarray = new ArrayList<User>();
                userarray.add(0, user);
                if (user.SchID > 0) {
                    _appPrefs.saveSchID(user.SchID);
                    _appPrefs.saveUserID(user.UserID);
                    _appPrefs.saveUser(userarray);
                    ArrayList<Account> AccountsArray = new ArrayList<Account>();
                    _appPrefs.saveAccounts(AccountsArray);
                    METHOD_NAME = "getSchool";
                    SOAP_ACTION = "getSchool";
                    School school = new School();
                    SoapObject requestSchool = new SoapObject(Data.NAMESPACE,
                            METHOD_NAME);

                    PropertyInfo piSchID = new PropertyInfo();
                    piSchID.setName("SchID");
                    piSchID.setValue(user.SchID);
                    requestSchool.addProperty(piSchID);

                    SoapSerializationEnvelope envelopeSchool = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    envelopeSchool.dotNet = true;
                    envelopeSchool.setOutputSoapObject(requestSchool);

                    httpTransport.call(SOAP_ACTION, envelopeSchool);
                    SoapObject responseSchool = (SoapObject) envelopeSchool
                            .getResponse();

                    school.SessionID = Integer.parseInt(responseSchool
                            .getProperty(3).toString());
                    school.CCProcessor = responseSchool.getProperty(88)
                            .toString();

                    _appPrefs.saveSessionID(school.SessionID);
                    _appPrefs.saveCCProcessor(school.CCProcessor);
                }

            } catch (Exception exception) {

            }

            return user;

        }

        protected void onPostExecute(User user) {
            if (user.UserID > 0) {

                Intent openMainPage = new Intent("com.akadasoftware.danceworksonline.Home");
                startActivity(openMainPage);
            } else {
                Intent openMainPage = new Intent("com.akadasoftware.danceworksonline.Login");
                startActivity(openMainPage);
            }

        }

    }


}
