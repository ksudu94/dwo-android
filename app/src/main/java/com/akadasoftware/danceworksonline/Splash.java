package com.akadasoftware.danceworksonline;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.FrameLayout;

import com.akadasoftware.danceworksonline.classes.Account;
import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.School;
import com.akadasoftware.danceworksonline.classes.User;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Splash extends ActionBarActivity {

    private static SharedPreferences loginPreferences;
    private static SharedPreferences.Editor loginEditor;
    private AppPreferences _appPrefs;
    User user = new User();
    String METHOD_NAME, SOAP_ACTION, UserGUID, LogoName;
    Integer UserID = 0;
    Boolean isTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _appPrefs = new AppPreferences(getApplicationContext());
        LogoName = _appPrefs.getLogoName();

        if (android.os.Build.VERSION.SDK_INT > 16) {
            setContentView(R.layout.activity_logo_splash);
            getBackground bg = new getBackground();
            bg.execute();

        } else {
            setContentView(R.layout.activity_default_splash);
        }

        /*
         *Uses loginPreferences to get the loggedin field to check whether or not the user has a
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

    public class getBackground extends AsyncTask<Data, Void, Void> {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected Void doInBackground(Data... datas) {
            final FrameLayout fm = (FrameLayout) findViewById(R.id.container);
            final Drawable background = ImageOperations("https://a77f5e8a78b68f5605b7-acb3eef5f1b15" +
                    "6a5a4173453f521b028.ssl.cf1.rackcdn.com/11-a6136d18-1d02-4387-abb3-3471cf8f4390." +
                    "png", "company_logo");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fm.setBackground(background);
                    isTablet = getResources().getBoolean(R.bool.isTablet);
                    if (isTablet) {
                        fm.setScaleX(1);
                        fm.setScaleY(1);
                    } else {
                        fm.setScaleX(Float.valueOf("0.5"));
                        fm.setScaleY(Float.valueOf("0.5"));
                    }
                }
            });

            return null;
        }

    }

    private Drawable ImageOperations(String url, String saveFilename) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, saveFilename);
            return d;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object fetch(String address) throws IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
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
                    _appPrefs.saveUserGUID(user.UserGUID);
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

                    PropertyInfo userid = new PropertyInfo();
                    userid.setName("UserID");
                    userid.setValue(user.UserID);
                    requestSchool.addProperty(userid);

                    PropertyInfo userguid = new PropertyInfo();
                    userguid.setName("UserGUID");
                    userguid.setValue(user.UserGUID);
                    requestSchool.addProperty(userguid);

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
                    school.ST1Rate = Float.parseFloat(responseSchool
                            .getProperty(56).toString());
                    school.ST2Rate = Float.parseFloat(responseSchool
                            .getProperty(59).toString());

                    _appPrefs.saveSessionID(school.SessionID);
                    _appPrefs.saveCCProcessor(school.CCProcessor);
                    _appPrefs.saveST1Rate(school.ST1Rate);
                    _appPrefs.saveST2Rate(school.ST2Rate);
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
