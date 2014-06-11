package com.akadasoftware.danceworksonline;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.Globals;
import com.akadasoftware.danceworksonline.classes.SchoolClasses;
import com.akadasoftware.danceworksonline.classes.Student;
import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;


public class Enroll extends FragmentActivity implements
        StudentEnrollFragment.OnStudentEnrollListener,
        StudentEnrollFragment.onEnrollDialog,
        EnrollDialog.EnrollDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new StudentEnrollFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enroll, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStudentEnrollInteraction(String id) {

    }

    @Override
    public void onEnrollDialog(SchoolClasses objSchoolClass, Student oStudent, ArrayList<String> conflicksArray) {
        FragmentManager fm = getSupportFragmentManager();
        EnrollDialog enrollDialog = new EnrollDialog();


        SchoolClasses[] arraySchoolClasses = new SchoolClasses[1];
        arraySchoolClasses[0] = objSchoolClass;

        Gson gson = new Gson();
        String strJsonSchoolClasses = gson.toJson(arraySchoolClasses);


        getIntent().putExtra("SchoolClasses", strJsonSchoolClasses);
        getIntent().putExtra("StuID", oStudent.StuID);

        getIntent().putStringArrayListExtra("Conflicks", conflicksArray);

        //Just the name of the dialog. Has no effect on it.
        enrollDialog.show(fm, "");
    }

    @Override
    public void onEnrollDialogPositiveClick() {

        EnrollStudentAsync enrollStudent = new EnrollStudentAsync();
        enrollStudent.execute();


    }

    private class EnrollStudentAsync extends
            AsyncTask<Globals.Data, Void, Boolean> {

        ProgressDialog progress;

        protected void onPreExecute() {
            progress = ProgressDialog.show(Enroll.this, "Parsing the Internet", "Loading...", true);
        }

        @Override
        protected Boolean doInBackground(Globals.Data... data) {
            /**
             * Enrolling student in class, even if there are conflicts.
             */
            return EnrollStudent();
        }

        protected void onPostExecute(Boolean result) {

            if (result == true) {
                Toast toast = Toast.makeText(Enroll.this, "The student was enrolled in the class", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(Enroll.this, "The student was not enrolled in the class", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    public Boolean EnrollStudent() {
        AppPreferences _appPrefs = new AppPreferences(Enroll.this);
        String MethodName = "enrollStudent";
        SoapObject response = InvokeEnrollMethod(Globals.Data.URL, MethodName, _appPrefs);
        return RetrieveEnrollFromSoap(response);
    }

    public static SoapObject InvokeEnrollMethod(String URL, String METHOD_NAME, AppPreferences _appPrefsNew) {

        SoapObject requestEnroll = new SoapObject(Globals.Data.NAMESPACE, METHOD_NAME);
        AppPreferences _appPrefs = _appPrefsNew;

        SchoolClasses oSchoolClasses = _appPrefs.getSchoolClassList();

        PropertyInfo piUserID = new PropertyInfo();
        piUserID.setName("UserID");
        piUserID.setValue(_appPrefs.getUserID());
        requestEnroll.addProperty(piUserID);

        PropertyInfo piUserGUID = new PropertyInfo();
        piUserGUID.setName("UserGUID");
        piUserGUID.setValue(_appPrefs.getUserGUID());
        requestEnroll.addProperty(piUserGUID);

        PropertyInfo piStuID = new PropertyInfo();
        piStuID.setName("intStuID");
        piStuID.setValue(_appPrefs.getStuID());
        requestEnroll.addProperty(piStuID);

        PropertyInfo piClID = new PropertyInfo();
        piClID.setName("intClID");
        piClID.setValue(oSchoolClasses.ClID);
        requestEnroll.addProperty(piClID);

        PropertyInfo piClRID = new PropertyInfo();
        piClRID.setName("intClRID");
        piClRID.setValue(oSchoolClasses.ClRID);
        requestEnroll.addProperty(piClRID);

        PropertyInfo piWaitID = new PropertyInfo();
        piWaitID.setName("intWaitID");
        piWaitID.setValue(oSchoolClasses.WaitID);
        requestEnroll.addProperty(piWaitID);

        SoapSerializationEnvelope envelopeEnroll = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        envelopeEnroll.dotNet = true;
        envelopeEnroll.setOutputSoapObject(requestEnroll);

        envelopeEnroll.dotNet = true;
        return MakeEnrollCall(URL, envelopeEnroll, Globals.Data.NAMESPACE, METHOD_NAME);
    }

    public static SoapObject MakeEnrollCall(String URL,
                                            SoapSerializationEnvelope envelope, String NAMESPACE,
                                            String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        try {

            HttpTransport.call(METHOD_NAME, envelope);
            SoapObject responseEnroll = (SoapObject) envelope.getResponse();

            return responseEnroll;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean RetrieveEnrollFromSoap(SoapObject soap) {
        Boolean response = Boolean.parseBoolean(soap.toString());

        return response;
    }


}
