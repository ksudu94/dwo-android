package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.akadasoftware.danceworksonline.classes.AppPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


/**
 * Created by Kyle on 5/19/2014.
 */
public class EnrollDialog extends DialogFragment {

    AppPreferences _appPrefs;

    public EnrollDialog() {

    }

    public interface EnrollDialogListener {
        public void onEnrollDialogPositiveClick();
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

        SchoolClasses objSchoolClasses = new SchoolClasses();

        String strJsonSchoolClasses = getActivity().getIntent().getStringExtra("SchoolClasses");
        ;
        if (strJsonSchoolClasses != "None found") {
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(strJsonSchoolClasses).getAsJsonArray();
            objSchoolClasses = gson.fromJson(array.get(0), SchoolClasses.class);
        }

        int intStuID = getActivity().getIntent().getIntExtra("SchID", 0);

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

                        dialogListener.onEnrollDialogPositiveClick();
                    }


                })
                .setNeutralButton("WaitList", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dialogListener.onEnrollDialogPositiveClick();
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
