package com.akadasoftware.danceworksonline;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

import com.akadasoftware.danceworksonline.classes.SchoolClasses;
import com.akadasoftware.danceworksonline.classes.Student;
import com.google.gson.Gson;

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

    }


}
