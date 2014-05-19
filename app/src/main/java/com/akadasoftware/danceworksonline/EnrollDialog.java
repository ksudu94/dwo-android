package com.akadasoftware.danceworksonline;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.akadasoftware.danceworksonline.classes.AppPreferences;


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


        builder.setView(view)
                // Title of dialog
                .setTitle("Enroll Student in Class")

                        // Add action buttons
                .setPositiveButton("Enroll", new DialogInterface.OnClickListener() {
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
