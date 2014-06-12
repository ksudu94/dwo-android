package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.akadasoftware.danceworksonline.classes.SchoolClasses;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.ArrayList;


/**
 * Created by Kyle on 5/19/2014.
 */
public class EnrollDialog extends DialogFragment {

    AppPreferences _appPrefs;
    SchoolClasses objSchoolClasses;

    public EnrollDialog() {

    }

    public interface EnrollDialogListener {
        public void onEnrollDialogPositiveClick(int intStuID, SchoolClasses oSchoolClasses);
    }

    EnrollDialogListener dialogListener;

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dialogListener = (EnrollDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement FilterDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.enrolldialog, null);


        _appPrefs = new AppPreferences(getActivity());


        String strJsonSchoolClasses = getActivity().getIntent().getStringExtra("SchoolClasses");

        if (strJsonSchoolClasses != "None found") {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(strJsonSchoolClasses).getAsJsonArray();
            objSchoolClasses = gson.fromJson(array.get(0), SchoolClasses.class);
        }

        Intent I = getActivity().getIntent();
        ArrayList<String> conflictsArray = I.getStringArrayListExtra("Conflicks");

        final int intStuID = getActivity().getIntent().getIntExtra("StuID", 0);

        TextView tvConflicts = (TextView) view.findViewById(R.id.tvConflicks);

        TextView tvClassType = (TextView) view.findViewById(R.id.tvClassType);

        TextView tvClassLevel = (TextView)
                view.findViewById(R.id.tvClassLevel);

        TextView tvDescription = (TextView)
                view.findViewById(R.id.tvDescription);

        TextView tvInstructor = (TextView)
                view.findViewById(R.id.tvInstructor);

        TextView tvDay = (TextView)
                view.findViewById(R.id.tvDay);

        TextView tvStart = (TextView)
                view.findViewById(R.id.tvStart);

        TextView tvStop = (TextView)
                view.findViewById(R.id.tvStop);

        TextView tvRoom = (TextView)
                view.findViewById(R.id.tvRoom);

        int arrayCount = conflictsArray.size();

        String conflitMessage = "";

        if (arrayCount == 0)
            conflitMessage = "Do you want to enrol this student in the class listed below?";
        else if (arrayCount == 1)
            conflitMessage = "The following class conflict with the class below";
        else
            conflitMessage = "The following classes conflict with the class below";

        for (int i = 0; i < arrayCount; i++) {
            if (conflictsArray.get(i).toString().equals("anyType{}"))
                conflitMessage += "";
            else
                conflitMessage += "\n" + conflictsArray.get(i).toString();

        }
        tvConflicts.setText(conflitMessage);

        String Day = "";
        tvClassType.setText(objSchoolClasses.ClType);

        tvClassLevel.setText(" - " + objSchoolClasses.ClLevel + " - ");
        tvDescription.setText(objSchoolClasses.ClDescription);
        tvInstructor.setText(objSchoolClasses.ClInstructor);

        if (objSchoolClasses.MultiDay) {
            if (objSchoolClasses.Monday) {
                Day = "Mon";
            }
            if (objSchoolClasses.Tuesday) {
                if (Day.isEmpty()) {
                    Day = "Tue";
                } else {
                    Day = Day + "/Tue";
                }
            }
            if (objSchoolClasses.Wednesday) {
                if (Day.isEmpty()) {
                    Day = "Wed";
                } else {
                    Day = Day + "/Wed";
                }
            }
            if (objSchoolClasses.Thursday) {
                if (Day.isEmpty()) {
                    Day = "Thu";
                } else {
                    Day = Day + "/Thu";
                }
            }
            if (objSchoolClasses.Friday) {
                if (Day.isEmpty()) {
                    Day = "Fri";
                } else {
                    Day = Day + "/Fri";
                }
            }
            if (objSchoolClasses.Saturday) {
                if (Day.isEmpty()) {
                    Day = "Sat";
                } else {
                    Day = Day + "/Sat";
                }
            }
            if (objSchoolClasses.Sunday) {
                if (Day.isEmpty()) {
                    Day = "Sun";
                } else {
                    Day = Day + "/Sun";
                }
            }
        } else {
            Day = objSchoolClasses.ClDay;
        }
        tvDay.setText(Day + " from ");
        tvStart.setText(objSchoolClasses.ClStart + " - ");
        tvStop.setText(objSchoolClasses.ClStop);
        tvRoom.setText(objSchoolClasses.ClRoom);

        builder.setView(view)
                // Title of dialog
                .setTitle("Enroll Student in Class?")

                        // Add action buttons
                .setPositiveButton("Enroll", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialogListener.onEnrollDialogPositiveClick(intStuID, objSchoolClasses);
                    }


                })
                .setNeutralButton("WaitList", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        //dialogListener.onEnrollDialogPositiveClick(intStuID, objSchoolClasses);
                    }

                })
                .setNegativeButton("Return", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EnrollDialog.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }

}
