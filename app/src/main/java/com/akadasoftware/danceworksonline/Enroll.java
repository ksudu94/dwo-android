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
import com.akadasoftware.danceworksonline.classes.User;
import com.google.gson.Gson;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;


public class Enroll extends FragmentActivity implements
        StudentEnrollFragment.OnStudentEnrollListener,
        StudentEnrollFragment.onEnrollDialog,
        EnrollDialog.EnrollDialogListener {

    SchoolClasses globalSchoolClasses;
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
    public void onEnrollDialogPositiveClick(int intStuID, SchoolClasses inputSchoolClasses) {

        EnrollStudentAsync enrollStudent = new EnrollStudentAsync();
        enrollStudent.execute();

        globalSchoolClasses = inputSchoolClasses;

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
            return EnrollStudent(globalSchoolClasses);
        }

        protected void onPostExecute(Boolean result) {
            progress.dismiss();
            if (result == true) {
                Toast toast = Toast.makeText(Enroll.this, "The student was enrolled in the class", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(Enroll.this, "The student was not enrolled in the class", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    public Boolean EnrollStudent(SchoolClasses objSchoolClasses) {
        AppPreferences _appPrefs = new AppPreferences(Enroll.this);
        String MethodName = "enrollStudent";
        SoapPrimitive response = InvokeEnrollMethod(Globals.Data.URL, MethodName, _appPrefs, objSchoolClasses);
        return RetrieveEnrollFromSoap(response);
    }

    /**
     * @param URL              Something
     * @param METHOD_NAME      enrollStudent
     * @param _appPrefsNew     Used to get User and StuID which for some reason would not save otherwise.
     * @param objSchoolClasses Comes from the selected class from yhe enroll dialog. Originally in
     *                         on positive click which is then saved to a global variable so it can passed
     *                         into EnrollStudent and ultimately InvokeEnrollMethod
     * @return Boolean true or false
     */
    public static SoapPrimitive InvokeEnrollMethod(String URL, String METHOD_NAME, AppPreferences _appPrefsNew
            , SchoolClasses objSchoolClasses) {

        SoapObject requestEnroll = new SoapObject(Globals.Data.NAMESPACE, METHOD_NAME);
        AppPreferences _appPrefs = _appPrefsNew;

        SchoolClasses selectedSchoolClasses = objSchoolClasses;

        User oUser = _appPrefs.getUser();
        int intStuID = _appPrefs.getStuID();

        PropertyInfo piUserID = new PropertyInfo();
        piUserID.setName("UserID");
        piUserID.setValue(oUser.UserID);
        requestEnroll.addProperty(piUserID);

        PropertyInfo piUserGUID = new PropertyInfo();
        piUserGUID.setName("UserGUID");
        piUserGUID.setValue(oUser.UserGUID);
        requestEnroll.addProperty(piUserGUID);

        PropertyInfo piStuID = new PropertyInfo();
        piStuID.setName("intStuID");
        piStuID.setValue(intStuID);
        requestEnroll.addProperty(piStuID);

        PropertyInfo piClID = new PropertyInfo();
        piClID.setName("intClID");
        piClID.setValue(selectedSchoolClasses.ClID);
        requestEnroll.addProperty(piClID);

        PropertyInfo piClRID = new PropertyInfo();
        piClRID.setName("intClRID");
        piClRID.setValue(selectedSchoolClasses.ClRID);
        requestEnroll.addProperty(piClRID);

        PropertyInfo piWaitID = new PropertyInfo();
        piWaitID.setName("intWaitID");
        piWaitID.setValue(selectedSchoolClasses.WaitID);
        requestEnroll.addProperty(piWaitID);

        SoapSerializationEnvelope envelopeEnroll = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        envelopeEnroll.dotNet = true;
        envelopeEnroll.setOutputSoapObject(requestEnroll);

        envelopeEnroll.dotNet = true;
        return MakeEnrollCall(URL, envelopeEnroll, Globals.Data.NAMESPACE, METHOD_NAME);
    }

    public static SoapPrimitive MakeEnrollCall(String URL,
                                            SoapSerializationEnvelope envelope, String NAMESPACE,
                                            String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        SoapPrimitive responseEnroll = null;
        try {

            HttpTransport.call(METHOD_NAME, envelope);
            responseEnroll = (SoapPrimitive) envelope.getResponse();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseEnroll;
    }

    public static Boolean RetrieveEnrollFromSoap(SoapPrimitive soap) {
        Boolean response = Boolean.parseBoolean(soap.toString());

        return response;
    }


}
