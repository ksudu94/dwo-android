package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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

public class Login extends ActionBarActivity {

    private static SharedPreferences loginPreferences;
    private static SharedPreferences.Editor loginEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }

    }

    //May want in future
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id)
        {
            case R.id.action_settings:
                return true;
            case R.id.log_out:
                loginEditor.clear();
                loginEditor.commit();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new Login.LoginFragment()).commit();
                break;
            case R.id.profile:
                return true;
            default:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/

    /**
     * A Login fragment containing edit texts for the email and password and various other
     * views.
     */
    public static class LoginFragment extends Fragment {
        View rootView;
        Activity activity;
        private AppPreferences _appPrefs;
        Button btnLogin;
        EditText etEmail;
        EditText etPassword;
        CheckBox chkShowPassword;
        CheckBox chkRemember;
        TextView tvForgotPassword;
        TextView tvError;
        User user = new User();
        String METHOD_NAME = "";
        String SOAP_ACTION = "";
        Integer UserID = 0;
        String UserGUID = "";

        public LoginFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_login, container, false);
            activity = this.getActivity();
            _appPrefs = new AppPreferences(activity);

            btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
            etEmail = (EditText) rootView.findViewById(R.id.etEmail);
            etPassword = (EditText) rootView.findViewById(R.id.etPassword);
            chkShowPassword = (CheckBox) rootView.findViewById(R.id.chkShowPassword);
            chkRemember = (CheckBox) rootView.findViewById(R.id.chkRemember);
            tvForgotPassword = (TextView) rootView.findViewById(R.id.tvForgotPassword);
            tvError = (TextView) rootView.findViewById(R.id.tvError);
            //
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
            chkShowPassword.setChecked(false);
            chkShowPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chkShowPassword.isChecked()) {
                        etPassword.setTransformationMethod(null);
                        etPassword.setSelection(etPassword.getText().length());
                    } else {
                        etPassword.setTransformationMethod(new PasswordTransformationMethod());
                        etPassword.setSelection(etPassword.getText().length());
                    }
                }
            });
            tvError.setText("");
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvError.setText("");
                    new getUser().execute();
                }
            });

            loginPreferences = activity.getSharedPreferences("loginPrefs", MODE_PRIVATE);
            loginEditor = loginPreferences.edit();
            Boolean loggedin = loginPreferences.getBoolean("loggedin", false);

            /*if (loggedin) {
                UserID = loginPreferences.getInt("UserID", 0);
                if (UserID > 0) {
                    UserGUID = loginPreferences.getString("UserGUID", "");
                    tvError.setText("");
                    new getUserByID().execute();
                } else {
                    tvError.setText("Please enter your email address and password to login");
                    loginEditor.clear();
                    loginEditor.putBoolean("loggedin", false);
                    loginEditor.commit();
                }
            }*/

            tvForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (etEmail.getText().length() > 0)
                        _appPrefs.saveEmail(etEmail.getText().toString());
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new RecoverPasswordFragment())
                            .commit();
                }
            });
            return rootView;

        }

        @Override
        public void onStart() {
            super.onStart();
        }

        class Data {

            static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
            private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
        }

        public class getUser extends AsyncTask<Data, Void, User> {

            @Override
            protected User doInBackground(Data... data) {

                METHOD_NAME = "getUser";
                SOAP_ACTION = "getUser";

                SoapObject requestUser = new SoapObject(Data.NAMESPACE, METHOD_NAME);

                PropertyInfo piEmail = new PropertyInfo();
                piEmail.setType("STRING_CLASS");
                piEmail.setName("email");

                piEmail.setValue(etEmail.getText().toString());
                requestUser.addProperty(piEmail);

                PropertyInfo piPassword = new PropertyInfo();
                piPassword.setType("STRING_CLASS");
                piPassword.setName("password");

                piPassword.setValue(etPassword.getText().toString());
                requestUser.addProperty(piPassword);

                SoapSerializationEnvelope envelopeUser = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);

                envelopeUser.dotNet = true;
                envelopeUser.setOutputSoapObject(requestUser);

                //Creates an instance of HttpTransportSE with set url
                HttpTransportSE httpTransport = new HttpTransportSE(Data.URL);

                try {
                    //Sets the desired soapAction field which corresponds with our webservice
                    //method
                    httpTransport.call(SOAP_ACTION, envelopeUser);
                    //A dynamic object that is used to build soap calls.
                    SoapObject responseUser = (SoapObject) envelopeUser
                            .getResponse();
                    for (int i = 0; i < responseUser.getPropertyCount(); i++) {
                        user.setProperty(i, responseUser.getProperty(i).toString());
                    }
                    ArrayList<User> userarray = new ArrayList<User>();
                    userarray.add(0, user);
                    // If the user logged in before it will not hit this break point but instead
                    // go to the splash page
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
                        _appPrefs.saveLogoName(school.LogoName);

                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                return user;

            }

            protected void onPostExecute(User user) {
                if (user.UserID > 0) {
                    if (chkRemember.isChecked()) {
                        loginEditor.putBoolean("loggedin", true);
                        loginEditor.putInt("UserID", user.UserID);
                        loginEditor.putString("UserGUID", user.UserGUID);
                        loginEditor.commit();
                    } else {
                        loginEditor.clear();
                        loginEditor.putBoolean("loggedin", false);
                        loginEditor.commit();
                    }
                    Intent openMainPage = new Intent("com.akadasoftware.danceworksonline.Home");
                    startActivity(openMainPage);
                } else {
                    tvError.setText("Login information is incorrect");
                }
            }

        }

       /* public class getUserByID extends AsyncTask<Data, Void, User> {

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
                                            *//*_appPrefs.saveEmail(strEmail);*//*

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
                    tvError.setText("Login information is incorrect");
                }

            }

        }
*/
    }

    public static class RecoverPasswordFragment extends Fragment {
        View rootView;
        Activity activity;
        SharedPreferences loginPreferences;
        SharedPreferences.Editor loginEditor;
        private AppPreferences _appPrefs;
        TextView tvMessage;
        TextView tvResponse;
        EditText etRecoverPassword;
        Button btnSendEmail;
        Button btnCancel;


        public RecoverPasswordFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_recover_password, container, false);
            activity = this.getActivity();
            _appPrefs = new AppPreferences(activity);
            btnSendEmail = (Button) rootView.findViewById(R.id.btnSendEmail);
            btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
            etRecoverPassword = (EditText) rootView.findViewById(R.id.etRecoverPassword);
            tvMessage = (TextView) rootView.findViewById(R.id.tvMessage);
            tvResponse = (TextView) rootView.findViewById(R.id.tvResponse);
            //

            etRecoverPassword.setText(_appPrefs.getEmailPref());
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new LoginFragment())
                            .commit();
                }
            });

            return rootView;

        }

        @Override
        public void onStart() {
            super.onStart();
        }

        class Data {

            static final String NAMESPACE = "http://app.akadasoftware.com/MobileAppWebService/";
            private static final String URL = "http://app.akadasoftware.com/MobileAppWebService/Android.asmx";
        }

    }

}
