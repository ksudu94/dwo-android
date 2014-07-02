package com.akadasoftware.danceworksonline;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.Globals;
import com.akadasoftware.danceworksonline.classes.School;
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
    Student globalStudent;
    private AppPreferences _appPrefs;
    SchoolClassAdapter objClassAdapter;
    int intClassPosition;

    View objView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        _appPrefs = new AppPreferences(Enroll.this);
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
    public void onStudentEnrollInteraction(int classPosition, int newClRID) {

    }

    @Override
    public void onEnrollDialog(SchoolClasses objSchoolClass, Student oStudent, ArrayList<String> conflicksArray
            , int positionOfClass, SchoolClassAdapter inputClassAdapter) {
        FragmentManager fm = getSupportFragmentManager();
        EnrollDialog enrollDialog = new EnrollDialog();


        SchoolClasses[] arraySchoolClasses = new SchoolClasses[1];
        arraySchoolClasses[0] = objSchoolClass;

        Student[] arrayStudents = new Student[1];
        arrayStudents[0] = oStudent;

        Gson gson = new Gson();
        String strJsonSchoolClasses = gson.toJson(arraySchoolClasses);
        String strStudent = gson.toJson(arrayStudents);


        getIntent().putExtra("SchoolClasses", strJsonSchoolClasses);
        getIntent().putExtra("Student", strStudent);
        getIntent().putExtra("StuID", oStudent.StuID);
        getIntent().putExtra("Position", positionOfClass);


        objClassAdapter = inputClassAdapter;

        getIntent().putStringArrayListExtra("Conflicks", conflicksArray);

        //Just the name of the dialog. Has no effect on it.
        enrollDialog.show(fm, "");
    }

    @Override
    public void onEnrollDialogPositiveClick( SchoolClasses inputSchoolClasses, int classPosition) {

        globalSchoolClasses = inputSchoolClasses;
        intClassPosition = classPosition;

        EnrollStudentAsync enrollStudent = new EnrollStudentAsync();
        enrollStudent.execute();

    }

    @Override
    public void onEnrollDialogNuetralClick( SchoolClasses inputSchoolClasses, Student inputStudent,
                                            int classPosition) {
        globalSchoolClasses = inputSchoolClasses;
        globalStudent = inputStudent;
        intClassPosition = classPosition;


        WaitListStudentAsync waitList = new WaitListStudentAsync();
        waitList.execute();

    }


    /**
     * Places student on waitlist
     */
    private class WaitListStudentAsync extends
            AsyncTask<Globals.Data, Void, Integer> {

        ProgressDialog progress;

        protected void onPreExecute() {
            progress = ProgressDialog.show(Enroll.this, "Parsing the Internet", "Accessing WaitList...", true);
        }

        @Override
        protected Integer doInBackground(Globals.Data... data) {
            /**
             * Placing student on waitlist
             *
             */
            return WaitListStudent(globalSchoolClasses, globalStudent);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if (result > 0) {
                Toast toast = Toast.makeText(Enroll.this, "The student was placed on the waitlist", Toast.LENGTH_LONG);
                toast.show();
                /**
                 * Returns new WatiID
                 */
                globalSchoolClasses.WaitID = result;
                globalSchoolClasses.EnrollmentStatus = 3;

                objClassAdapter.replaceSchoolClass(globalSchoolClasses,intClassPosition);

            } else {
                Toast toast = Toast.makeText(Enroll.this, "The student was not placed on the waitlist", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    public int WaitListStudent(SchoolClasses oSchoolClasses, Student oStudent) {
        AppPreferences _appPrefs = new AppPreferences(Enroll.this);
        String MethodName = "waitListStudent";
        SoapPrimitive response = InvokeWaitListMethod(Globals.Data.URL, MethodName, _appPrefs,
                oSchoolClasses, oStudent);
        return RetrieveWaitListFromSoap(response);
    }

    public static SoapPrimitive InvokeWaitListMethod(String URL, String METHOD_NAME, AppPreferences _appPrefsNew
            , SchoolClasses objSchoolClasses, Student objStudent) {

        SoapObject requestWaitList = new SoapObject(Globals.Data.NAMESPACE, METHOD_NAME);

        AppPreferences _appPrefs = _appPrefsNew;

        SchoolClasses selectedSchoolClasses = objSchoolClasses;
        Student selectedStudent = objStudent;
        User oUser = _appPrefs.getUser();
        School oSchool = _appPrefs.getSchool();


        PropertyInfo piStrAction = new PropertyInfo();
        piStrAction.setName("strAction");
        piStrAction.setValue("WaitlistClass");
        requestWaitList.addProperty(piStrAction);

        PropertyInfo piClID = new PropertyInfo();
        piClID.setName("intClID");
        piClID.setValue(selectedSchoolClasses.ClID);
        requestWaitList.addProperty(piClID);

        PropertyInfo piUserID = new PropertyInfo();
        piUserID.setName("intUserID");
        piUserID.setValue(oUser.UserID);
        requestWaitList.addProperty(piUserID);

        PropertyInfo piUserGUID = new PropertyInfo();
        piUserGUID.setName("UserGUID");
        piUserGUID.setValue(oUser.UserGUID);
        requestWaitList.addProperty(piUserGUID);

        PropertyInfo piSchID = new PropertyInfo();
        piSchID.setName("intSchID");
        piSchID.setValue(oSchool.SchID);
        requestWaitList.addProperty(piSchID);

        PropertyInfo piStuID = new PropertyInfo();
        piStuID.setName("intStuID");
        piStuID.setValue(selectedStudent.StuID);
        requestWaitList.addProperty(piStuID);


        PropertyInfo piAcctID = new PropertyInfo();
        piAcctID.setName("intAcctID");
        piAcctID.setValue(selectedStudent.AcctID);
        requestWaitList.addProperty(piAcctID);

        PropertyInfo piCurrentSessionID = new PropertyInfo();
        piCurrentSessionID.setName("intCurrSessionID");
        piCurrentSessionID.setValue(oSchool.SessionID);
        requestWaitList.addProperty(piCurrentSessionID);

        PropertyInfo piSelectedClassSessionID = new PropertyInfo();
        piSelectedClassSessionID.setName("intClassSessionID");
        piSelectedClassSessionID.setValue(oSchool.SessionID);
        requestWaitList.addProperty(piSelectedClassSessionID);

        PropertyInfo piFName = new PropertyInfo();
        piFName.setName("FName");
        piFName.setValue(selectedStudent.FName);
        requestWaitList.addProperty(piFName);

        PropertyInfo piLName = new PropertyInfo();
        piLName.setName("LName");
        piLName.setValue(selectedStudent.LName);
        requestWaitList.addProperty(piLName);

        PropertyInfo piAcctName = new PropertyInfo();
        piAcctName.setName("strAcctName");
        piAcctName.setValue(selectedStudent.AcctName);
        requestWaitList.addProperty(piAcctName);

        PropertyInfo piPhone = new PropertyInfo();
        piPhone.setName("strPhone");
        piPhone.setValue(selectedStudent.Phone);
        requestWaitList.addProperty(piPhone);

        PropertyInfo piNotes = new PropertyInfo();
        piNotes.setName("strNotes");
        piNotes.setValue(selectedStudent.Notes);
        requestWaitList.addProperty(piNotes);


        SoapSerializationEnvelope envelopeWaitList = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        envelopeWaitList.dotNet = true;
        envelopeWaitList.setOutputSoapObject(requestWaitList);

        return MakeWaitListCall(URL, envelopeWaitList, Globals.Data.NAMESPACE, METHOD_NAME);
    }

    public static SoapPrimitive MakeWaitListCall(String URL,
                                                 SoapSerializationEnvelope envelope, String NAMESPACE,
                                                 String METHOD_NAME) {
        HttpTransportSE HttpTransport = new HttpTransportSE(URL);
        SoapPrimitive responseWaitList = null;
        try {

            HttpTransport.call(METHOD_NAME, envelope);
            responseWaitList = (SoapPrimitive) envelope.getResponse();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseWaitList;
    }

    public static int RetrieveWaitListFromSoap(SoapPrimitive soap) {
        int response = Integer.parseInt(soap.toString());

        return response;
    }


    /**
     * Enrolls a student in a class
     */
    private class EnrollStudentAsync extends
            AsyncTask<Globals.Data, Void, Integer> {

        ProgressDialog progress;

        protected void onPreExecute() {
            progress = ProgressDialog.show(Enroll.this, "Enrolling Student", "Loading...", true);
        }

        @Override
        protected Integer doInBackground(Globals.Data... data) {
            /**
             * Enrolling student in class, even if there are conflicts.
             */
            return EnrollStudent(globalSchoolClasses);
        }

        protected void onPostExecute(Integer result) {
            progress.dismiss();
            if (result > 0) {
                Toast toast = Toast.makeText(Enroll.this, "The student was enrolled in the class", Toast.LENGTH_LONG);
                toast.show();

                /**
                 * Update ClRID and EnrollmentStatus
                 */
                globalSchoolClasses.ClRID = result;
                globalSchoolClasses.EnrollmentStatus = 1;

                objClassAdapter.replaceSchoolClass(globalSchoolClasses,intClassPosition);



                /**
                 * Get Class list
                 */
                ArrayList<SchoolClasses> classesArray = _appPrefs.getSchoolClassList();

                /**
                 * Update class list with new class with updated variables
                 */
                classesArray.set(intClassPosition, globalSchoolClasses);
                _appPrefs.saveSchoolClassList(classesArray);

            } else {
                Toast toast = Toast.makeText(Enroll.this, "The student was not enrolled in the class", Toast.LENGTH_LONG);
                toast.show();
            }
        }

    }

    public Integer EnrollStudent(SchoolClasses objSchoolClasses) {
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

    public static Integer RetrieveEnrollFromSoap(SoapPrimitive soap) {
        int response = Integer.parseInt(soap.toString());

        return response;
    }


}
